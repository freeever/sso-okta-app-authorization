package com.dxu.sso.course.mgmt.service;

import com.dxu.sso.common.event.course.CourseApplicationApprovedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CourseEnrollmentEventHandlerTest {

    private CourseEnrollmentEventHandler courseEnrollmentEventHandler;
    private CourseEnrollmentService courseEnrollmentServiceMock;

    @BeforeEach
    void setUp() {
        courseEnrollmentServiceMock = mock(CourseEnrollmentService.class);
        courseEnrollmentEventHandler = new CourseEnrollmentEventHandler(courseEnrollmentServiceMock);
    }

    @Test
    void testOnApplicationApproved() {
        courseEnrollmentEventHandler.onApplicationApproved(new CourseApplicationApprovedEvent(
                UUID.randomUUID(), Instant.now(), 2L, 3L, 1L));
        verify(courseEnrollmentServiceMock, times(1));
    }
}