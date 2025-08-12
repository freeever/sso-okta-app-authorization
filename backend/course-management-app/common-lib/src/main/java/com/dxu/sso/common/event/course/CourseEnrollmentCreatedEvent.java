package com.dxu.sso.common.event.course;

import java.time.Instant;
import java.util.UUID;

// ------------- Management side → Application side -------------
public record CourseEnrollmentCreatedEvent(
        UUID sagaId,
        Instant timestamp,
        Long courseId,
        Long studentId,
        Long applicationId
) implements CourseSagaEvent { }
