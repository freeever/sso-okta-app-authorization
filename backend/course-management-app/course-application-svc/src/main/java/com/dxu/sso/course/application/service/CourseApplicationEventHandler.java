package com.dxu.sso.course.application.service;

import com.dxu.sso.common.event.CourseEnrollmentCreatedEvent;
import com.dxu.sso.common.event.CourseEnrollmentExistedEvent;
import com.dxu.sso.common.event.CourseEnrollmentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.dxu.sso.common.constant.CourseApplicationStatus.COURSE_ENROLLMENT_EXISTED;
import static com.dxu.sso.common.constant.CourseApplicationStatus.COURSE_ENROLLMENT_FAILED;
import static com.dxu.sso.common.constant.KafkaEventConstants.GROUP_COURSE_APPLICATION;
import static com.dxu.sso.common.constant.KafkaEventConstants.TOPIC_COURSE_ENROLLMENT_CREATED;
import static com.dxu.sso.common.constant.KafkaEventConstants.TOPIC_COURSE_ENROLLMENT_EXISTED;
import static com.dxu.sso.common.constant.KafkaEventConstants.TOPIC_COURSE_ENROLLMENT_FAILED;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseApplicationEventHandler {

    private final CourseApplicationService service;

    /* ------------- SAGA CALLBACKS ------------- */

    @KafkaListener(
            topics = TOPIC_COURSE_ENROLLMENT_CREATED,
            groupId = GROUP_COURSE_APPLICATION
    )
    public void onEnrollmentCreated(CourseEnrollmentCreatedEvent evt) {
        log.info("⬅️  received event CourseEnrollmentCreatedEvent. course: {} student {}", evt.courseId(), evt.studentId());
    }

    @KafkaListener(topics =
            TOPIC_COURSE_ENROLLMENT_EXISTED,
            groupId = GROUP_COURSE_APPLICATION
    )
    public void onEnrollmentExisted(CourseEnrollmentExistedEvent evt) {
        log.info("⬅️  received event CourseEnrollmentExistedEvent. course: {} student {}", evt.courseId(), evt.studentId());

        service.updateStatus(evt.applicationId(), COURSE_ENROLLMENT_EXISTED);
        log.info("⬅️  enrollment existed, status updated");
    }

    @KafkaListener(
            topics = TOPIC_COURSE_ENROLLMENT_FAILED,
            groupId = GROUP_COURSE_APPLICATION
    )
    public void onEnrollmentFailed(CourseEnrollmentFailedEvent evt) {
        log.info("⬅️  received event CourseEnrollmentFailedEvent. course: {} student {}", evt.courseId(), evt.studentId());

        service.updateStatus(evt.applicationId(), COURSE_ENROLLMENT_FAILED);
        log.info("⬅️  enrollment failed, status updated");
    }
}

