package com.dxu.sso.common.event;

import java.time.Instant;
import java.util.UUID;

// ------------- Management side → Application side -------------
public record CourseEnrollmentFailedEvent(
        UUID sagaId,
        Instant timestamp,
        Long courseId,
        Long studentId,
        Long applicationId,
        String reason        // e.g. duplicate PK, DB error…
) implements CourseSagaEvent { }
