package com.dxu.sso.course.application.service;

import com.dxu.sso.common.constant.KafkaEventConstants;
import com.dxu.sso.common.event.student.StudentDeleteCommand;
import com.dxu.sso.common.event.student.StudentDeleteReply;
import com.dxu.sso.common.event.student.StudentDeleteReplyType;
import com.dxu.sso.course.application.repository.CourseApplicationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void onCommand(StudentDeleteCommand cmd) {
        log.info("onCommand {} for saga {}", cmd.type(), cmd.sagaId());
        switch (cmd.type()) {
            case DELETE_APPLICATIONS ->
                handleCommand(cmd, true, APPLICATIONS_DELETED, APPLICATIONS_DELETE_FAILED);
            case RESTORE_APPLICATIONS ->
                handleCommand(cmd, true, APPLICATIONS_RESTORED, APPLICATIONS_RESTORE_FAILED);
        }
    }

    private void handleCommand(StudentDeleteCommand cmd,
                               boolean deletedFlag,
                               StudentDeleteReplyType successReply,
                               StudentDeleteReplyType failureReply) {
        try {
            int updated = applicationRepo.markStudentDeleted(cmd.studentId(), deletedFlag);
            // Idempotent: updated can be 0 if already in desired state
            reply(new StudentDeleteReply(cmd.sagaId(), Instant.now(clock), cmd.studentId(), successReply, null));
        } catch (Exception e) {
            reply(new StudentDeleteReply(cmd.sagaId(), Instant.now(clock), cmd.studentId(), failureReply, e.getMessage()));
            throw e;
        }
    }

    private void reply(StudentDeleteReply reply) {
        kafka.send(TOPIC_STUDENT_DELETE_REPLIES, "student-" + reply.studentId(), reply);
    }
}
