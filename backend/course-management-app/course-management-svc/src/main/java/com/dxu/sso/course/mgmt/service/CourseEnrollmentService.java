package com.dxu.sso.course.mgmt.service;

import com.dxu.sso.common.event.course.CourseApplicationApprovedEvent;
import com.dxu.sso.common.event.course.CourseEnrollmentCreatedEvent;
import com.dxu.sso.common.event.course.CourseEnrollmentExistedEvent;
import com.dxu.sso.common.event.course.CourseEnrollmentFailedEvent;
import com.dxu.sso.common.event.common.SagaEvent;
import com.dxu.sso.common.event.common.SagaEventUtil;
import com.dxu.sso.common.model.course.Course;
import com.dxu.sso.common.model.course.CourseEnrollment;
import com.dxu.sso.common.model.course.CourseEnrollmentId;
import com.dxu.sso.course.mgmt.repository.CourseEnrollmentRepository;
import com.dxu.sso.course.mgmt.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.dxu.sso.common.constant.KafkaEventConstants.TOPIC_COURSE_ENROLLMENT_CREATED;
import static com.dxu.sso.common.constant.KafkaEventConstants.TOPIC_COURSE_ENROLLMENT_EXISTED;
import static com.dxu.sso.common.constant.KafkaEventConstants.TOPIC_COURSE_ENROLLMENT_FAILED;
import static java.time.Instant.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseEnrollmentService {

    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void enroll(CourseApplicationApprovedEvent evt) {
        Long courseId = evt.courseId();
        Long studentId = evt.studentId();
        Long applicationId = evt.applicationId();
        log.info("enroll student {} to course {}", studentId, courseId);

        // when the course enrollment exists by courseId and studentId
        CourseEnrollmentId enrollmentId = new CourseEnrollmentId(courseId, studentId);
        boolean alreadyEnrolled = courseEnrollmentRepository.existsById(enrollmentId);
        if (alreadyEnrolled) {
            log.info("Student {} already enrolled in course {}", studentId, courseId);
            emit(TOPIC_COURSE_ENROLLMENT_EXISTED,
                    new CourseEnrollmentExistedEvent(evt.sagaId(), now(), courseId, studentId, applicationId),
                    courseId, studentId);
            return;
        }

        createEnrollment(evt);
    }

    /**
     * Create course-enrollment, and emit event CourseEnrollmentCreatedEvent.
     * If failed to save, emit event CourseEnrollmentFailedEvent
     */
    private void createEnrollment(CourseApplicationApprovedEvent evt) {
        Long courseId = evt.courseId();
        Long studentId = evt.studentId();
        Long applicationId = evt.applicationId();
        try {
            Course courseRef = courseRepository.getReferenceById(courseId);
            // Create course enrollment
            CourseEnrollment enrollment = CourseEnrollment.builder()
                    .id(new CourseEnrollmentId(courseId, studentId))
                    .course(courseRef)
                    .createdAt(LocalDateTime.now())
                    .build();
            courseEnrollmentRepository.save(enrollment);

            // emit created event
            emit(TOPIC_COURSE_ENROLLMENT_CREATED,
                    new CourseEnrollmentCreatedEvent(evt.sagaId(), now(), courseId, studentId, applicationId),
                    courseId, studentId);
        } catch (Exception ex) {
            log.error("enroll student {} course {} failed", studentId, courseId, ex);
            // emit failed event
            emit(TOPIC_COURSE_ENROLLMENT_FAILED,
                    new CourseEnrollmentFailedEvent(evt.sagaId(), now(), courseId, studentId, applicationId, ex.getMessage()),
                    courseId, studentId);
        }
    }

    private void emit(String topic, SagaEvent evt, Long courseId, Long studentId) {
        kafkaTemplate.send(topic, SagaEventUtil.buildCourseApplicationEvtKey(courseId, studentId), evt);
        log.info("▶️  published {}", evt);
    }
}

