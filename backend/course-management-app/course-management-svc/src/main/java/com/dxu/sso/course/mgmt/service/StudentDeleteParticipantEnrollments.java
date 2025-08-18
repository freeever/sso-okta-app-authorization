package com.dxu.sso.course.mgmt.service;

import com.dxu.sso.common.constant.KafkaEventConstants;
import com.dxu.sso.common.event.student.StudentDeleteCommand;
import com.dxu.sso.common.event.student.StudentDeleteCommandType;
import com.dxu.sso.common.event.student.StudentDeleteReply;
import com.dxu.sso.common.event.student.StudentDeleteReplyType;
import com.dxu.sso.course.mgmt.repository.CourseEnrollmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

import static com.dxu.sso.common.constant.KafkaEventConstants.GROUP_COURSE_APPLICATION;
import static com.dxu.sso.common.constant.KafkaEventConstants.GROUP_COURSE_ENROL_STUDENT_DELETE;
import static com.dxu.sso.common.constant.KafkaEventConstants.TOPIC_STUDENT_DELETE_CMDS;
import static com.dxu.sso.common.constant.KafkaEventConstants.TOPIC_STUDENT_DELETE_REPLIES;
import static com.dxu.sso.common.event.student.StudentDeleteCommandType.DELETE_ENROLLMENTS;
import static com.dxu.sso.common.event.student.StudentDeleteReplyType.ENROLLMENTS_DELETED;
import static com.dxu.sso.common.event.student.StudentDeleteReplyType.ENROLLMENTS_DELETE_FAILED;
import static com.dxu.sso.common.event.student.StudentDeleteReplyType.ENROLLMENTS_RESTORED;
import static com.dxu.sso.common.event.student.StudentDeleteReplyType.ENROLLMENTS_RESTORE_FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentDeleteParticipantEnrollments {

    private final CourseEnrollmentRepository enrollmentRepo;
    private final KafkaTemplate<String, Object> kafka;
    private final Clock clock = Clock.systemUTC();

    @Transactional
    @KafkaListener(topics = TOPIC_STUDENT_DELETE_CMDS, groupId = GROUP_COURSE_ENROL_STUDENT_DELETE)
    public void onCommand(StudentDeleteCommand cmd) {
        log.info("onCommand {} for saga {}", cmd.type(), cmd.sagaId());
        switch (cmd.type()) {
            case DELETE_ENROLLMENTS ->
                handleCommand(cmd, true, ENROLLMENTS_DELETED, ENROLLMENTS_DELETE_FAILED);
            case RESTORE_ENROLLMENTS ->
                handleCommand(cmd, false, ENROLLMENTS_RESTORED, ENROLLMENTS_RESTORE_FAILED);
            default -> log.debug("Ignoring {} for saga {}", cmd.type(), cmd.sagaId());
        }
    }

    private void handleCommand(StudentDeleteCommand cmd,
                               boolean deletedFlag,
                               StudentDeleteReplyType successReply,
                               StudentDeleteReplyType failureReply) {
        try {
            int updated = enrollmentRepo.markStudentDeleted(cmd.studentId(), deletedFlag);
            // Idempotent: updated can be 0 if already in desired state
            reply(new StudentDeleteReply(cmd.sagaId(), Instant.now(clock), cmd.studentId(), successReply, null));
            log.info("Enrollments {} for student {} (rows={})",
                    deletedFlag ? "deleted" : "restored", cmd.studentId(), updated);
        } catch (Exception e) {
            reply(new StudentDeleteReply(cmd.sagaId(), Instant.now(clock), cmd.studentId(), failureReply, e.getMessage()));
            throw e;
        }
    }

    private void reply(StudentDeleteReply reply) {
        kafka.send(TOPIC_STUDENT_DELETE_REPLIES, "student-" + reply.studentId(), reply);
        log.info("▶️ reply {}", reply);
    }
}
