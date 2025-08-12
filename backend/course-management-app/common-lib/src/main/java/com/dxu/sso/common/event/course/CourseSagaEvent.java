package com.dxu.sso.common.event.course;

import com.dxu.sso.common.event.common.SagaEvent;

import java.time.Instant;
import java.util.UUID;

public sealed interface CourseSagaEvent extends SagaEvent
        permits CourseApplicationApprovedEvent,
        CourseEnrollmentCreatedEvent,
        CourseEnrollmentExistedEvent,
        CourseEnrollmentFailedEvent {     // <-- subclasses

    UUID sagaId();
    Instant timestamp();
    Long courseId();
    Long studentId();
    Long applicationId();
}
