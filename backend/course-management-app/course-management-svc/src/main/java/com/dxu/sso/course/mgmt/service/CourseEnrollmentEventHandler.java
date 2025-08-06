package com.dxu.sso.course.mgmt.service;

import com.dxu.sso.common.event.SagaDLTMessage;
import com.dxu.sso.common.event.CourseApplicationApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.dxu.sso.common.constant.KafkaEventConstants.GROUP_COURSE_MANAGEMENT;
import static com.dxu.sso.common.constant.KafkaEventConstants.GROUP_DLT_MONITOR;
import static com.dxu.sso.common.constant.KafkaEventConstants.TOPIC_COURSE_APPLICATION_APPROVED;
import static com.dxu.sso.common.constant.KafkaEventConstants.TOPIC_COURSE_APPLICATION_DLT;


@Slf4j
@Component
@RequiredArgsConstructor
public class CourseEnrollmentEventHandler {

    private final CourseEnrollmentService enrollmentService;

    /**
     * containerFactory = "kafkaListenerContainerFactory" annotation tells Spring explicitly to use the factory we
     * just defined (kafkaListenerContainerFactory) in KafkaConfig instead of the default one.
     * It ensures that the consumer uses:
     *  - Your retry/backoff configuration
     *  - The DLT error handler
     */
    @KafkaListener(
            topics = TOPIC_COURSE_APPLICATION_APPROVED,
            groupId = GROUP_COURSE_MANAGEMENT,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onApplicationApproved(CourseApplicationApprovedEvent event) {
            log.info("receive application approved event: course: {} student: {}", event.courseId(), event.studentId());
            enrollmentService.enroll(event);
    }

    @KafkaListener(
            topics = TOPIC_COURSE_APPLICATION_DLT,
            groupId = GROUP_DLT_MONITOR
    )
    public void handleDLT(SagaDLTMessage dltMessage) {
        log.error("🚨 DLT message received: {}\nError: {}\nStack Trace:\n{}",
                dltMessage.getOriginalEvent(),
                dltMessage.getErrorMessage(),
                dltMessage.getStackTrace());
    }

}

