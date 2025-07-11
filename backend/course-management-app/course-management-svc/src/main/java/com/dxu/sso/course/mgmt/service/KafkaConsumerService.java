package com.dxu.sso.course.mgmt.service;

import com.dxu.sso.common.event.CourseApplicationApprovedDLTMessage;
import com.dxu.sso.common.event.CourseApplicationApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final CourseEnrollmentService enrollmentService;

    /**
     * containerFactory = "kafkaListenerContainerFactory" annotation tells Spring explicitly to use the factory we
     * just defined (kafkaListenerContainerFactory) in KafkaConfig instead of the default one.
     * It ensures that the consumer uses:
     *  - Your retry/backoff configuration
     *  - The DLT error handler
     */
    @KafkaListener(
            topics = "course-application-approved",
            groupId = "course-management-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeApplicationApproved(CourseApplicationApprovedEvent event) {
            log.info("consume application approved event: course: {} student: {}", event.getCourseId(), event.getStudentId());
            enrollmentService.enrollStudent(event.getCourseId(), event.getStudentId());
    }

    @KafkaListener(topics = "course-application-approved-dlt", groupId = "dlt-monitor-group")
    public void handleDLT(CourseApplicationApprovedDLTMessage dltMessage) {
        log.error("🚨 DLT message received: {}\nError: {}\nStack Trace:\n{}",
                dltMessage.getOriginalEvent(),
                dltMessage.getErrorMessage(),
                dltMessage.getStackTrace());
    }
}

