package com.dxu.sso.course.application.service;

import com.dxu.sso.common.event.student.StudentDeleteCommand;
import com.dxu.sso.common.event.student.StudentDeleteReply;
import com.dxu.sso.common.event.student.StudentDeleteReplyType;
import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.course.application.repository.CourseApplicationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

import static com.dxu.sso.common.constant.KafkaEventConstants.GROUP_COURSE_APP_STUDENT_DELETE;
import static com.dxu.sso.common.constant.KafkaEventConstants.TOPIC_STUDENT_DELETE_CMDS;
import static com.dxu.sso.common.constant.KafkaEventConstants.TOPIC_STUDENT_DELETE_REPLIES;
import static com.dxu.sso.common.event.student.StudentDeleteReplyType.APPLICATIONS_DELETED;
import static com.dxu.sso.common.event.student.StudentDeleteReplyType.APPLICATIONS_DELETE_FAILED;
import static com.dxu.sso.common.event.student.StudentDeleteReplyType.APPLICATIONS_RESTORED;
import static com.dxu.sso.common.event.student.StudentDeleteReplyType.APPLICATIONS_RESTORE_FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentDeleteParticipantApplications {

    private final CourseApplicationRepository applicationRepo;
    private final KafkaTemplate<String, Object> kafka;
    private final Clock clock = Clock.systemUTC();

    @KafkaListener(topics = TOPIC_STUDENT_DELETE_CMDS, groupId = GROUP_COURSE_APP_STUDENT_DELETE)
    @Transactional
    public void onCommand(StudentDeleteCommand cmd) throws Exception {
        log.info("onCommand {} for saga {}", cmd.type(), cmd.sagaId());
        switch (cmd.type()) {
            case DELETE_APPLICATIONS ->
                handleCommand(cmd, true, APPLICATIONS_DELETED, APPLICATIONS_DELETE_FAILED);
            case RESTORE_APPLICATIONS ->
                handleCommand(cmd, false, APPLICATIONS_RESTORED, APPLICATIONS_RESTORE_FAILED);
            default -> log.debug("Ignoring {} for saga {}", cmd.type(), cmd.sagaId());
        }
    }

    private void handleCommand(StudentDeleteCommand cmd,
                               boolean deletedFlag,
                               StudentDeleteReplyType successReply,
                               StudentDeleteReplyType failureReply) throws SsoApplicationException {
        try {
            int updated = applicationRepo.markStudentDeleted(cmd.studentId(), deletedFlag);
            // Idempotent: updated can be 0 if already in desired state
            reply(new StudentDeleteReply(cmd.sagaId(), Instant.now(clock), cmd.studentId(), successReply, null));
            log.info("Marked student_deleted AS {} for student: {} of {} course APPLICATIONS",
                    deletedFlag, cmd.studentId(), updated);
        } catch (Exception e) {
            log.error("Failed to mark student_deleted AS {} for student {} course APPLICATIONS",
                    cmd.studentId(), cmd.studentId());
            reply(new StudentDeleteReply(cmd.sagaId(), Instant.now(clock), cmd.studentId(), failureReply, e.getMessage()));
            throw new SsoApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    private void reply(StudentDeleteReply reply) {
        kafka.send(TOPIC_STUDENT_DELETE_REPLIES, "student-" + reply.studentId(), reply);
        log.info("▶️ sent reply: {}", reply);
    }
}
