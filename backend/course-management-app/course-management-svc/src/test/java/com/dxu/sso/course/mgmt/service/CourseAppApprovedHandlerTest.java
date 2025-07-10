package com.dxu.sso.course.mgmt.service;

import com.dxu.sso.common.event.CourseApplicationApprovedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CourseAppApprovedHandlerTest {

    private CourseAppApprovedHandler courseAppApprovedHandler;
    private CourseEnrollmentService courseEnrollmentServiceMock;

    @BeforeEach
    void setUp() {
        courseEnrollmentServiceMock = mock(CourseEnrollmentService.class);
        courseAppApprovedHandler = new CourseAppApprovedHandler(courseEnrollmentServiceMock);
    }

    @Test
    void testConsumeApplicationApproved() {
        courseAppApprovedHandler.consumeApplicationApproved(new CourseApplicationApprovedEvent(2L, 3L, LocalDateTime.now()));
        verify(courseEnrollmentServiceMock, times(1));
    }
}