package com.dxu.sso.common.event.course;

import java.time.Instant;
import java.util.UUID;

// ------------- Application side → Management side -------------
public record CourseApplicationApprovedEvent(
        UUID sagaId,
        Instant timestamp,
        Long courseId,
        Long studentId,
        Long applicationId   // handy for callbacks
) implements CourseSagaEvent { }
