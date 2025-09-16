package com.dxu.sso.user.admin.service;

import com.dxu.sso.common.event.student.StudentDeleteCommand;
import com.dxu.sso.common.event.student.StudentDeleteCommandType;
import com.dxu.sso.common.event.student.StudentDeleteReply;
import com.dxu.sso.user.admin.saga.common.SagaStatus;
import com.dxu.sso.user.admin.saga.common.StepStatus;
import com.dxu.sso.user.admin.saga.student.StudentDeleteSaga;
import com.dxu.sso.user.admin.repository.StudentDeleteSagaRepository;
import com.dxu.sso.user.admin.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import static com.dxu.sso.common.constant.KafkaEventConstants.GROUP_USER_ADMIN_ORCHESTRATOR;
import static com.dxu.sso.common.constant.KafkaEventConstants.TOPIC_STUDENT_DELETE_CMDS;
import static com.dxu.sso.common.constant.KafkaEventConstants.TOPIC_STUDENT_DELETE_REPLIES;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudentDeleteOrchestrator {

    private final UserRepository userRepository;
    private final StudentDeleteSagaRepository sagaRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Clock clock = Clock.systemUTC();

    /**
     * Start Saga of deleting the specified student
     * @param studentId student id
     * @return the Saga Id
     */
    @Transactional
    public UUID startDeleteStudent(Long studentId) {
        log.info("start deleting student {}", studentId);

        // Step 1: Local update
        userRepository.markDeleted(studentId, true); // logical delete = set deleted=true

        Instant now = Instant.now(clock);
        UUID sagaId = UUID.randomUUID();
        addSaga(studentId, sagaId, now);

        // Step 2: fire commands to both application and enrollment services
        sendCommand(new StudentDeleteCommand(sagaId, now, studentId, StudentDeleteCommandType.DELETE_ENROLLMENTS));
        sendCommand(new StudentDeleteCommand(sagaId, now, studentId, StudentDeleteCommandType.DELETE_APPLICATIONS));

        log.info("Saga {} started for deleting student {}", sagaId, studentId);
        return sagaId;
    }

    /**
     * Create StudentDeleteSaga and save to the DB
     */
    private void addSaga(Long studentId, UUID sagaId, Instant now) {
        var saga = StudentDeleteSaga.builder()
                .id(sagaId)
                .studentId(studentId)
                .status(SagaStatus.FORWARDING)
                .enrollments(StepStatus.PENDING)
                .applications(StepStatus.PENDING)
                .startedAt(now)
                .updatedAt(now)
                .build();
        sagaRepository.save(saga);
    }

    /**
     * Fire Saga command to delete student
     * @param cmd the Saga command
     */
    private void sendCommand(StudentDeleteCommand cmd) {
        String key = "student-" + cmd.studentId();
        kafkaTemplate.send(TOPIC_STUDENT_DELETE_CMDS, key, cmd);
        log.info("▶️ sent {}", cmd);
    }

    /* ===================== Replies ===================== */

    @Transactional
    @KafkaListener(topics = TOPIC_STUDENT_DELETE_REPLIES, groupId = GROUP_USER_ADMIN_ORCHESTRATOR)
    public void onReply(StudentDeleteReply reply) {
        log.info("Received reply {}", reply);
        sagaRepository.findById(reply.sagaId()).ifPresentOrElse(
                saga -> processReply(saga, reply),
                () -> log.warn("Reply for unknown saga {}", reply.sagaId())
        );
    }

    private void processReply(StudentDeleteSaga saga, StudentDeleteReply reply) {
        log.info("Processing reply {}", reply);
        if (isTerminal(saga.getStatus())) {
            log.debug("Ignoring reply for terminal saga {}: {}", saga.getId(), reply.type());
            return;
        }

        // 1) Update saga step status by reply.type()
        updateSagaStepStatusByReplyType(saga, reply);
        saga.setUpdatedAt(Instant.now(clock));

        // 2) Route by saga phase
        switch (saga.getStatus()) {
            case FORWARDING -> handleForwardingPhase(saga);
            case COMPENSATING -> handleCompensatingPhase(saga);
            default -> { /* STARTED/COMPLETED/FAILED shouldn't happen here */ }
        }

        sagaRepository.save(saga);
    }

    /* ---------- Step 1: map reply → saga step ---------- */
    private void updateSagaStepStatusByReplyType(StudentDeleteSaga saga, StudentDeleteReply reply) {
        log.info("Applying reply {} to saga {}", reply.type(), reply.sagaId());
        switch (reply.type()) {
            // forward success/failure
            case ENROLLMENTS_DELETED          -> saga.setEnrollments(StepStatus.DONE);
            case ENROLLMENTS_DELETE_FAILED    -> saga.setEnrollments(StepStatus.FAILED);
            case APPLICATIONS_DELETED         -> saga.setApplications(StepStatus.DONE);
            case APPLICATIONS_DELETE_FAILED   -> saga.setApplications(StepStatus.FAILED);

            // compensation success/failure
            case ENROLLMENTS_RESTORED         -> saga.setEnrollments(StepStatus.COMP_DONE);
            case ENROLLMENTS_RESTORE_FAILED   -> saga.setEnrollments(StepStatus.COMP_FAILED);
            case APPLICATIONS_RESTORED        -> saga.setApplications(StepStatus.COMP_DONE);
            case APPLICATIONS_RESTORE_FAILED  -> saga.setApplications(StepStatus.COMP_FAILED);
        }
    }

    /* ---------- Step 2a: forwarding phase logic ---------- */
    private void handleForwardingPhase(StudentDeleteSaga saga) {
        log.info("Forwarding phase for saga {}, enrollments: {}, applications: {}",
                saga.getId(), saga.getEnrollments(), saga.getApplications());
        if (hasForwardFailure(saga)) {
            saga.setStatus(SagaStatus.COMPENSATING);
            // Compensate only the steps that actually succeeded in forward phase
            sendCompensation(
                    saga.getId(),
                    saga.getStudentId(),
                    saga.getEnrollments() == StepStatus.DONE,
                    saga.getApplications() == StepStatus.DONE
            );
            return;
        }

        if (isForwardComplete(saga)) {
            saga.setStatus(SagaStatus.COMPLETED);
            log.info("Saga {} COMPLETED (student {})", saga.getId(), saga.getStudentId());
        }
    }

    /* ---------- Step 2b: compensating phase logic ---------- */

    private void handleCompensatingPhase(StudentDeleteSaga saga) {
        log.info("Compensating phase for saga {}, enrollments: {}, applications: {}",
                saga.getId(), saga.getEnrollments(), saga.getApplications());
        if (!allCompensationsFinished(saga)) return;

        boolean anyForwardSucceeded = saga.getEnrollments()  == StepStatus.COMP_DONE
                                   || saga.getApplications() == StepStatus.COMP_DONE;

        if (anyForwardSucceeded) {
            log.info("Set \"deleted\" to false for student {}", saga.getStudentId());
            // local undo
            userRepository.markDeleted(saga.getStudentId(), false);
        }
        saga.setStatus(SagaStatus.FAILED);
        log.warn("Saga {} ROLLED BACK (student restored? {})", saga.getId(), anyForwardSucceeded);
    }

    // Check if the whole
    private static boolean isTerminal(SagaStatus sagaStatus) {
        return sagaStatus == SagaStatus.COMPLETED || sagaStatus == SagaStatus.FAILED;
    }

    // Check if update for enrollments and/or applications failed
    private static boolean hasForwardFailure(StudentDeleteSaga saga) {
        return saga.getEnrollments() == StepStatus.FAILED || saga.getApplications() == StepStatus.FAILED;
    }

    // Check if update for both enrollments and applications have been completed successfully
    private static boolean isForwardComplete(StudentDeleteSaga saga) {
        return saga.getEnrollments() == StepStatus.DONE && saga.getApplications() == StepStatus.DONE;
    }

    private static boolean allCompensationsFinished(StudentDeleteSaga saga) {
        return isCompFinished(saga.getEnrollments()) && isCompFinished(saga.getApplications());
    }

    private static boolean isCompFinished(StepStatus stepStatus) {
        return stepStatus == StepStatus.COMP_DONE || stepStatus == StepStatus.COMP_FAILED;
    }

    // Fire Saga compensation
    private void sendCompensation(UUID sagaId, Long studentId, boolean forEnrollments, boolean forApplications) {
        if (forEnrollments) {
            sendCommand(new StudentDeleteCommand(
                    sagaId, Instant.now(clock), studentId, StudentDeleteCommandType.RESTORE_ENROLLMENTS));
        }

        if (forApplications) {
            sendCommand(new StudentDeleteCommand(
                    sagaId, Instant.now(clock), studentId, StudentDeleteCommandType.RESTORE_APPLICATIONS));
        }
    }

}
