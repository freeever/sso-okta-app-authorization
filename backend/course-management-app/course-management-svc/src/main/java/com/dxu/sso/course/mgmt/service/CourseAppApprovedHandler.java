package com.dxu.sso.course.mgmt.service;

import com.dxu.sso.common.event.CourseApplicationApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseAppApprovedHandler {

    private final CourseEnrollmentService enrollmentService;

    @KafkaListener(topics = "course-application-approved", groupId = "course-management-group")
    public void consumeApplicationApproved(CourseApplicationApprovedEvent event) {
            log.info("consume application approved event: course: {} student: {}", event.getCourseId(), event.getStudentId());
            enrollmentService.enrollStudent(event.getCourseId(), event.getStudentId());
    }
}

