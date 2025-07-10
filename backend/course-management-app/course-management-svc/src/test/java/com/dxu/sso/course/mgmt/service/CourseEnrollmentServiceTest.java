package com.dxu.sso.course.mgmt.service;

import com.dxu.sso.course.mgmt.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CourseEnrollmentServiceTest {

    private CourseEnrollmentService courseEnrollmentService;
    private CourseRepository courseRepositoryMock;

    @BeforeEach
    void setUp() {
        courseRepositoryMock = mock(CourseRepository.class);
        courseEnrollmentService = new CourseEnrollmentService(courseRepositoryMock);
    }

    @Test
    void testEnrollStudent() {
//        courseEnrollmentService.enrollStudent(2L, 3L);
    }
}