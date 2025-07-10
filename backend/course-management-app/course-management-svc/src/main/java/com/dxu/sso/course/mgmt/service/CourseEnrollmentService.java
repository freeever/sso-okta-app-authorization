package com.dxu.sso.course.mgmt.service;

import com.dxu.sso.common.model.course.Course;
import com.dxu.sso.common.model.course.CourseEnrollment;
import com.dxu.sso.common.model.course.CourseEnrollmentId;
import com.dxu.sso.course.mgmt.repository.CourseEnrollmentRepository;
import com.dxu.sso.course.mgmt.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseEnrollmentService {

    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;

    @Transactional
    public void enrollStudent(Long courseId, Long studentId) {
        log.info("enroll student {} to course {}", studentId, courseId);

        // Do nothing if the course enrollment exists
        CourseEnrollmentId enrollmentId = new CourseEnrollmentId(courseId, studentId);
        boolean alreadyEnrolled = courseEnrollmentRepository.existsById(enrollmentId);
        if (alreadyEnrolled) {
            log.info("Student {} already enrolled in course {}", studentId, courseId);
            return;
        }

        Course courseRef = courseRepository.getReferenceById(courseId);
        // Create course enrollment
        CourseEnrollment enrollment = CourseEnrollment.builder()
                .id(new CourseEnrollmentId(courseId, studentId))
                .course(courseRef)
                .createdAt(LocalDateTime.now())
                .build();

        courseEnrollmentRepository.save(enrollment);
    }
}

