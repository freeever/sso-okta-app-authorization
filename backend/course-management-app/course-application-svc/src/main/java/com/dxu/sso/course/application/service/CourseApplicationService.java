package com.dxu.sso.course.application.service;

import com.dxu.sso.common.dto.course.CourseApplicationDto;
import com.dxu.sso.common.dto.course.CourseDto;
import com.dxu.sso.common.dto.mapper.CourseApplicationMapper;
import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.integration.CourseWebClient;
import com.dxu.sso.common.integration.UserWebClient;
import com.dxu.sso.common.model.CourseApplicationStatus;
import com.dxu.sso.common.model.courseapp.CourseApplication;
import com.dxu.sso.course.application.repository.ReactiveCourseApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.dxu.sso.common.model.CourseApplicationStatus.APPROVED;
import static com.dxu.sso.common.model.CourseApplicationStatus.CANCELLED;
import static com.dxu.sso.common.model.CourseApplicationStatus.IN_PROGRESS;
import static com.dxu.sso.common.model.CourseApplicationStatus.PENDING;

@Slf4j
@RequiredArgsConstructor
@Service
public class CourseApplicationService {

    private final UserWebClient userWebClient;
    private final CourseWebClient courseWebClient;
    private final ReactiveCourseApplicationRepository repository;
    private final CourseApplicationMapper mapper;

    /**
     * Find applications of a course by status (used by ADMIN or TEACHER)
     * @param courseId course id
     * @param status course application status
     * @return List of the applications for the given course and status
     */
    public Flux<CourseApplicationDto> findByCourseAndStatus(Long courseId, CourseApplicationStatus status) {
        log.info("find applications by courseId: {} status: {}", courseId, status);

        return repository.findByCourseIdAndStatus(courseId, status)
                .collectList()
                .flatMapMany(this::populateApplicationsInfo);
    }

    /**
     * Find course applications by student id
     * @param studentId student id
     * @return List of the applications for the given student
     */
    public Flux<CourseApplicationDto> findByStudentId(Long studentId) {
        log.info("find applications by studentId: {}", studentId);

        return repository.findByStudentId(studentId)
                .collectList()
                .flatMapMany(this::populateApplicationsInfo);
        // returns Flux<CourseApplicationDto>
    }

    /**
     * Find applications of a course by student id
     * @param studentId student id
     * @param courseId course id
     * @return List of the applications for the given course and student
     */
    public Flux<CourseApplicationDto> findByStudentAndCourse(Long studentId, Long courseId) {
        log.info("find applications by studentId: {} and courseId: {}", studentId, courseId);

        return repository.findByStudentIdAndCourseId(studentId, courseId)
                .collectList()
                .flatMapMany(this::populateApplicationsInfo);
    }

    /**
     * Student applies a course
     * @param courseId the course id
     * @param studentId the student id
     * @return the created course application
     */
    public Mono<CourseApplicationDto> apply(Long courseId, Long studentId) {
        log.info("apply courseId: {} studentId: {}", courseId, studentId);

        return repository.existsByStudentIdAndCourseIdAndStatusIn(
                        studentId, courseId, List.of(PENDING, IN_PROGRESS, APPROVED))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new SsoApplicationException(HttpStatus.BAD_REQUEST.value(),
                                "You already have a pending/in-progress/approved application for this course."));
                    }

                    CourseApplication application = CourseApplication.builder()
                            .courseId(courseId)
                            .studentId(studentId)
                            .status(PENDING)
                            .createdAt(LocalDateTime.now())
                            .build();

                    return repository.save(application); // returns Mono<CourseApplication>
                })
                .flatMap(savedApp -> populateApplicationsInfo(List.of(savedApp)).next());
    }

    /**
     * Student cancels a pending application
     * @param applicationId the application id
     * @param studentId the student id
     * @return the cancelled course application
     */
    public Mono<CourseApplicationDto> cancel(Long applicationId, Long studentId) {
        log.info("cancel applicationId: {} studentId: {}", applicationId, studentId);

        return findById(applicationId)
                .flatMap(application -> {
                    if (!application.getStudentId().equals(studentId)) {
                        return Mono.error(new SsoApplicationException(HttpStatus.FORBIDDEN.value(), "Not your application"));
                    }
                    if (!application.getStatus().equals(PENDING)) {
                        return Mono.error(new SsoApplicationException(HttpStatus.BAD_REQUEST.value(), "Only pending applications can be cancelled"));
                    }

                    application.setStatus(CANCELLED);
                    application.setCancelledAt(LocalDateTime.now());

                    return repository.save(application);
                })
                .flatMap(savedApp -> populateApplicationsInfo(List.of(savedApp)).next());
    }

    /**
     * 3. Admin user starts review a course application
     * @param applicationId the course application id
     * @param reviewerId the reviewer id
     * @return the application that is going to be reviewed
     */
    public Mono<CourseApplicationDto> startReview(Long applicationId, Long reviewerId) {
        log.info("Start reviewing applicationId: {} by reviewerId: {}", applicationId, reviewerId);

        return findById(applicationId)
                .flatMap(application -> {
                    if (!application.getStatus().equals(PENDING)) {
                        return Mono.error(new SsoApplicationException(HttpStatus.BAD_REQUEST.value(), "Only pending applications can be reviewed"));
                    }

                    application.setStatus(CourseApplicationStatus.IN_PROGRESS);
                    application.setReviewStartedAt(LocalDateTime.now());
                    application.setReviewerId(reviewerId);

                    return repository.save(application);
                })
                .flatMap(savedApp -> populateApplicationsInfo(List.of(savedApp)).next());
    }

    /**
     * ADMIN approves/rejects the course application
     * @param applicationId course application id
     * @param reviewerId the reviewer id
     * @param approve true is approve, false is reject
     * @param comment decision comment
     * @return the approved/rejected course application
     */
    public Mono<CourseApplicationDto> decide(Long applicationId, Long reviewerId, boolean approve, String comment) {
        log.info("Approve/reject applicationId: {} by reviewerId: {}, decision: {}", applicationId, reviewerId,
                approve ? "approved" : "rejected");

        return findById(applicationId)
                .flatMap(application -> {
                    if (!application.getStatus().equals(IN_PROGRESS)) {
                        return Mono.error(new SsoApplicationException(
                                HttpStatus.BAD_REQUEST.value(), "Only in-progress applications can be decided"));
                    }

                    application.setStatus(approve ? CourseApplicationStatus.APPROVED : CourseApplicationStatus.REJECTED);
                    application.setReviewedAt(LocalDateTime.now());
                    application.setReviewerId(reviewerId);
                    application.setDecisionComment(comment);

                    return repository.save(application);
                })
                .flatMap(saved -> populateApplicationsInfo(List.of(saved)).next())
                .doOnNext(app -> {
                    // TODO: If approved, emit event to Kafka for course enrollment
                    if (approve) {
                        // Example stub - we’ll implement the event later
                        // kafkaProducer.send(new CourseApplicationApprovedEvent(app.getCourseId(), app.getStudentId()));
                    }
                });
    }

    /**
     * Populate all the necessary course applications information
     * @param applications course application list
     * @return course application list with all the required inforamtion
     */
    private Flux<CourseApplicationDto> populateApplicationsInfo(List<CourseApplication> applications) {
        if (applications == null || applications.isEmpty()) {
            return Flux.empty();
        }

        List<CourseApplicationDto> applicationDtos = applications.stream().map(mapper::toDto).toList();
        return enrich(applicationDtos);
    }

    /**
     * Enrich all the applications information
     * @param applications course application list
     * @return course applications with enriched data
     */
    private Flux<CourseApplicationDto> enrich(List<CourseApplicationDto> applications) {
        if (applications == null || applications.isEmpty()) {
            return Flux.empty();
        }

        // 1: Get data for enrich
        Mono<Map<Long, AppUserDto>> userMapMono = getUsers(applications);
        Mono<Map<Long, CourseDto>> courseMapMono = getCourses(applications);

        // 2: Enrich applications
        return Mono.zip(userMapMono, courseMapMono)
                .flatMapMany(tuple -> {
                    Map<Long, AppUserDto> userMap = tuple.getT1();
                    Map<Long, CourseDto> courseMap = tuple.getT2();

                    return Flux.fromIterable(applications)
                            .map(app -> {
                                enrichApplication(app, userMap, courseMap);
                                return app;
                            });
                });
    }

    /**
     * Retrieve all the related users (student, reviewer), if exist
     * @param applications course applications
     * @return user list
     */
    private Mono<Map<Long, AppUserDto>> getUsers(List<CourseApplicationDto> applications) {
        // Step 1: Extract IDs
        List<Long> userIds = getAllUserIds(applications);
        // Step 2: Fetch data
        Flux<AppUserDto> users = userWebClient.getUsersByIds(userIds);

        // Step 3: Map by ID
        return users.collectMap(AppUserDto::getId);
    }

    /**
     * Retrieve all the related users (student, reviewer), if exist
     * @param applications course applications
     * @return user list
     */
    private Mono<Map<Long, CourseDto>> getCourses(List<CourseApplicationDto> applications) {
        // Step 1: Extract IDs
        List<Long> courseIds = getCourseIds(applications);

        // Step 2: Fetch data
        Flux<CourseDto> courses = courseWebClient.findCoursesByIds(courseIds);

        // Step 3: Map by ID
        return courses.collectMap(CourseDto::getId);
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

    private Mono<CourseApplication> findById(Long applicationId) {
        return repository.findById(applicationId)
                .switchIfEmpty(Mono.error(
                        new SsoApplicationException(HttpStatus.BAD_REQUEST.value(), "Application not found")));
    }
}
