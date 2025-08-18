package com.dxu.sso.course.application.service;

import com.dxu.sso.common.constant.CourseApplicationStatus;
import com.dxu.sso.common.dto.course.CourseApplicationDto;
import com.dxu.sso.common.dto.course.CourseDto;
import com.dxu.sso.common.dto.mapper.CourseApplicationMapper;
import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.event.course.CourseApplicationApprovedEvent;
import com.dxu.sso.common.event.common.SagaEventUtil;
import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.integration.CourseWebClient;
import com.dxu.sso.common.integration.UserWebClient;
import com.dxu.sso.common.model.courseapp.CourseApplication;
import com.dxu.sso.course.application.repository.CourseApplicationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.dxu.sso.common.constant.CourseApplicationStatus.APPROVED;
import static com.dxu.sso.common.constant.CourseApplicationStatus.CANCELLED;
import static com.dxu.sso.common.constant.CourseApplicationStatus.IN_PROGRESS;
import static com.dxu.sso.common.constant.CourseApplicationStatus.PENDING;
import static com.dxu.sso.common.constant.KafkaEventConstants.TOPIC_COURSE_APPLICATION_APPROVED;

@Slf4j
@RequiredArgsConstructor
@Service
public class CourseApplicationService {

    private final UserWebClient userWebClient;
    private final CourseWebClient courseWebClient;
    private final CourseApplicationRepository repository;
    private final CourseApplicationMapper mapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Find applications of a course by status (used by ADMIN or TEACHER)
     * @param courseId course id
     * @param status course application status
     * @return List of the applications for the given course and status
     */
    public List<CourseApplicationDto> findByCourseAndStatus(Long courseId, CourseApplicationStatus status) {
        log.info("find applications by courseId: {} status: {}", courseId, status);

        List<CourseApplication> applications = repository.findByCourseIdAndStatus(courseId, status);
        return populateApplicationsInfo(applications);
    }

    /**
     * Find course applications by student id
     * @param studentId student id
     * @return List of the applications for the given student
     */
    public List<CourseApplicationDto> findByStudentId(Long studentId) {
        log.info("find applications by studentId: {}", studentId);

        List<CourseApplication> applications = repository.findByStudentId(studentId);
        return populateApplicationsInfo(applications);
    }

    /**
     * Find applications of a course by student id
     * @param studentId student id
     * @param courseId course id
     * @return List of the applications for the given course and student
     */
    public List<CourseApplicationDto> findByStudentAndCourse(Long studentId, Long courseId) {
        log.info("find applications by studentId: {} and courseId: {}", studentId, courseId);

        List<CourseApplication> applications = repository.findByStudentIdAndCourseId(studentId, courseId);
        return populateApplicationsInfo(applications);
    }

    /**
     * Student applies a course
     * @param courseId the course id
     * @param studentId the student id
     * @return the created course application
     */
    public CourseApplicationDto apply(Long courseId, Long studentId) {
        log.info("apply courseId: {} studentId: {}", courseId, studentId);

        boolean exists = repository.existsByStudentIdAndCourseIdAndStatusIn(
                studentId, courseId, List.of(PENDING, IN_PROGRESS, APPROVED));
        if (exists) {
            throw new SsoApplicationException(HttpStatus.BAD_REQUEST.value(),
                    "You already have a pending/in-progress/approved application for this course.");
        }

        CourseApplication application = CourseApplication.builder()
                .courseId(courseId)
                .studentId(studentId)
                .status(PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        application = repository.save(application);
        return populateApplicationsInfo(List.of(application)).get(0);
    }

    /**
     * Student cancels a pending application
     * @param applicationId the application id
     * @param studentId the student id
     * @return the cancelled course application
     */
    public CourseApplicationDto cancel(Long applicationId, Long studentId) {
        log.info("cancel applicationId: {} studentId: {}", applicationId, studentId);

        CourseApplication application = findById(applicationId);
        if (!application.getStudentId().equals(studentId)) {
            throw new SsoApplicationException(HttpStatus.FORBIDDEN.value(), "Not your application");
        }

        if (!application.getStatus().equals(PENDING)) {
            throw new SsoApplicationException(HttpStatus.BAD_REQUEST.value(), "Only pending applications can be cancelled");
        }

        application.setStatus(CANCELLED);
        application.setCancelledAt(LocalDateTime.now());

        application = repository.save(application);
        return populateApplicationsInfo(List.of(application)).get(0);
    }

    /**
     * 3. Admin user starts review a course application
     * @param applicationId the course application id
     * @param reviewerId the reviewer id
     * @return the application that is going to be reviewed
     */
    public CourseApplicationDto startReview(Long applicationId, Long reviewerId) {
        log.info("Start reviewing applicationId: {} by reviewerId: {}", applicationId, reviewerId);

        CourseApplication application = findById(applicationId);
        if (!application.getStatus().equals(PENDING)) {
            throw new SsoApplicationException(HttpStatus.BAD_REQUEST.value(), "Only pending applications can be reviewed");
        }

        application.setStatus(CourseApplicationStatus.IN_PROGRESS);
        application.setReviewStartedAt(LocalDateTime.now());
        application.setReviewerId(reviewerId);

        application = repository.save(application);
        return populateApplicationsInfo(List.of(application)).get(0);
    }

    /**
     * ADMIN approves/rejects the course application
     * @param applicationId course application id
     * @param reviewerId the reviewer id
     * @param approve true is approve, false is reject
     * @param comment decision comment
     * @return the approved/rejected course application
     */
    @Transactional
    public CourseApplicationDto decide(Long applicationId, Long reviewerId, boolean approve, String comment) {
        log.info("Approve/reject applicationId: {} by reviewerId: {}, decision: {}", applicationId, reviewerId,
                approve ? "approved" : "rejected");

        CourseApplication application = findById(applicationId);
        if (!application.getStatus().equals(IN_PROGRESS)) {
            throw new SsoApplicationException(HttpStatus.BAD_REQUEST.value(), "Only in-progress applications can be decided");
        }
        CourseApplicationDto savedApp = updateApplication(reviewerId, approve, comment, application);

        // If approved, emit event to Kafka to create course enrollment
        if (approve) {
            sendApprovedEvent(savedApp);
        }

        return savedApp;
    }

    /**
     * Update course-application
     * @param reviewerId reviewer's user id
     * @param approve true is approve
     *                false is reject
     * @param comment comments for decision
     * @param application the course-application
     * @return the updated course-application
     */
    private CourseApplicationDto updateApplication(Long reviewerId, boolean approve, String comment, CourseApplication application) {
        application.setStatus(approve ? CourseApplicationStatus.APPROVED : CourseApplicationStatus.REJECTED);
        application.setReviewedAt(LocalDateTime.now());
        application.setReviewerId(reviewerId);
        application.setDecisionComment(comment);

        application = repository.save(application);
        return populateApplicationsInfo(List.of(application)).get(0);
    }

    /**
     * Send CourseApplicationApprovedEvent to Kafka, which will be consumed by course-management-svc and
     * create course-enrollment record
     * @param app the approved course application
     */
    private void sendApprovedEvent(CourseApplicationDto app) {
        UUID sagaId = UUID.randomUUID();
        CourseApplicationApprovedEvent evt = new CourseApplicationApprovedEvent(sagaId, Instant.now(),
                app.getCourseId(), app.getStudentId(), app.getId());

        log.info("send application approved event. course: {} student: {}", evt.courseId(), evt.studentId());
        kafkaTemplate.send(TOPIC_COURSE_APPLICATION_APPROVED,
                SagaEventUtil.buildCourseApplicationEvtKey(app.getCourseId(), app.getStudentId()), evt);
        log.info("▶️  published {}", evt);
    }

    /**
     * Populate all the necessary course applications information
     * @param applications course application list
     * @return course application list with all the required inforamtion
     */
    private List<CourseApplicationDto> populateApplicationsInfo(List<CourseApplication> applications) {
        if (applications == null || applications.isEmpty()) {
            return List.of();
        }

        List<CourseApplicationDto> applicationDtos = applications.stream().map(mapper::toDto).toList();
        return enrich(applicationDtos);
    }

    /**
     * Enrich all the applications information
     * @param applications course application list
     * @return course applications with enriched data
     */
    private List<CourseApplicationDto> enrich(List<CourseApplicationDto> applications) {
        if (applications == null || applications.isEmpty()) {
            return List.of();
        }

        // 1: Get data for enrich
        Map<Long, AppUserDto> userMap = getUsers(applications);
        Map<Long, CourseDto> courseMap = getCourses(applications);

        // 2: Enrich applications
        for (CourseApplicationDto application : applications) {
            enrichApplication(application, userMap, courseMap);
        }

        return applications;
    }

    /**
     * Retrieve all the related users (student, reviewer), if exist
     * @param applications course applications
     * @return user list
     */
    private Map<Long, AppUserDto> getUsers(List<CourseApplicationDto> applications) {
        // Step 1: Extract IDs
        List<Long> userIds = getAllUserIds(applications);
        // Step 2: Fetch data
        List<AppUserDto> users = userWebClient.getUsersByIds(userIds);

        // Step 3: Map by ID
        return users.stream()
                .collect(Collectors.toMap(AppUserDto::getId, Function.identity()));
    }

    /**
     * Retrieve all the related users (student, reviewer), if exist
     * @param applications course applications
     * @return user list
     */
    private Map<Long, CourseDto> getCourses(List<CourseApplicationDto> applications) {
        // Step 1: Extract IDs
        List<Long> courseIds = getCourseIds(applications);
        // Step 2: Fetch data
        List<CourseDto> courses = courseWebClient.findCoursesByIds(courseIds);

        // Step 3: Map by ID
        return courses.stream()
                .collect(Collectors.toMap(CourseDto::getId, Function.identity()));
    }

    /**
     * Given the fetch data for enrichment, populate all the available necessary application details
     * @param application the course application
     * @param userMap the map of all related users (student and/or reviewer)
     * @param courseMap the map of courses
     */
    private void enrichApplication(CourseApplicationDto application, Map<Long, AppUserDto> userMap, Map<Long, CourseDto> courseMap) {
        AppUserDto student = userMap.get(application.getStudentId());
        if (student != null) {
            application.setStudentFirstName(student.getFirstName());
            application.setStudentLastName(student.getLastName());
            application.setStudentEmail(student.getEmail());
        }

        if (application.getReviewerId() != null) {
            AppUserDto reviewer = userMap.get(application.getReviewerId());
            if (reviewer != null) {
                application.setReviewerFirstName(reviewer.getFirstName());
                application.setReviewerLastName(reviewer.getLastName());
                application.setReviewerEmail(reviewer.getEmail());
            }
        }

        CourseDto course = courseMap.get(application.getCourseId());
        if (course != null) {
            application.setCourseName(course.getName());
        }
    }

    /**
     * By the given course application list, get the ID of all the related student(s) and reviewer(s)
     * @param applications the course application list
     * @return all users' IDs
     */
    private List<Long> getAllUserIds(List<CourseApplicationDto> applications) {
        List<Long> userIds = new ArrayList<>();
        if (applications != null) {
            for (CourseApplicationDto application : applications) {
                if (application.getStudentId() != null && !userIds.contains(application.getStudentId())) {
                    userIds.add(application.getStudentId());
                }
                if (application.getReviewerId() != null && !userIds.contains(application.getReviewerId())) {
                    userIds.add(application.getReviewerId());
                }
            }
        }
        return userIds;
    }

    /**
     * By the given course application list, get the course ID(s)
     * @param applications the course application list
     * @return all courses' IDs
     */
    private List<Long> getCourseIds(List<CourseApplicationDto> applications) {
        List<Long> courseIds = new ArrayList<>();
        if (applications != null) {
            for (CourseApplicationDto application : applications) {
                if (application.getCourseId() != null && !courseIds.contains(application.getCourseId())) {
                    courseIds.add(application.getCourseId());
                }
            }
        }
        return courseIds;
    }

    private CourseApplication findById(Long applicationId) {
        return repository.findById(applicationId)
                .orElseThrow(() -> new SsoApplicationException(HttpStatus.BAD_REQUEST.value(), "Application not found"));
    }

    public void updateStatus(Long applicationId, CourseApplicationStatus status) {
        CourseApplication ca = repository.findById(applicationId)
                .orElseThrow(() -> new SsoApplicationException("Course application not found: [" + applicationId + "]"));
        ca.setStatus(status);
        repository.save(ca);
    }
}
