package com.dxu.sso.course.application.controller;

import com.dxu.sso.common.dto.course.CourseApplicationDto;
import com.dxu.sso.common.model.CourseApplicationStatus;
import com.dxu.sso.common.security.RequireRoles;
import com.dxu.sso.common.security.UserContext;
import com.dxu.sso.course.application.dto.CourseApplicationCreateRequest;
import com.dxu.sso.course.application.dto.CourseApplicationDecisionRequest;
import com.dxu.sso.course.application.service.CourseApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/course-application")
@RequiredArgsConstructor
public class CourseApplicationController {

    private final CourseApplicationService service;
    private final UserContext userContext;

    /**
     * Find all applications of a course by status (used by ADMIN or TEACHER)
     * @param courseId course id
     * @param status course application status
     * @return List of the applications for the given course and status
     */
    @RequireRoles({"TEACHER", "ADMIN"})
    @GetMapping("/by-course/{courseId}/{status}")
    public Flux<CourseApplicationDto> findByCourseAndStatus(@PathVariable Long courseId,
                                                             @PathVariable CourseApplicationStatus status) {
        log.info("find applications by courseId: {} status: {}", courseId, status);

        return service.findByCourseAndStatus(courseId, status);
    }

    /**
     * Find the applications of the current logged in STUDENT
     * @param courseId course application id
     * @return the current student's course applications
     */
    @RequireRoles({"STUDENT"})
    @GetMapping("/my")
    public Flux<CourseApplicationDto> findMyApplications(@RequestParam(required = false) Long courseId) {
        log.info("find my applications for course: {}", courseId == null ? "ALL" : courseId);

        return courseId == null ?
                service.findByStudentId(getCurrentUserId())
                : service.findByStudentAndCourse(getCurrentUserId(), courseId);
    }

    /**
     * Student applies a course
     * @param request request payload for applying a course
     * @return the created course application
     */
    @RequireRoles({"STUDENT"})
    @PostMapping
    public Mono<CourseApplicationDto> apply(@RequestBody @Valid CourseApplicationCreateRequest request) {
        log.info("apply courseId: {} studentId: {}", request.getCourseId(), getCurrentUserId());

        return service.apply(request.getCourseId(), getCurrentUserId());
    }

    /**
     * Student cancels a pending application
     * @param id course application id
     * @return the cancelled application
     */
    @RequireRoles({"STUDENT"})
    @PutMapping("/{id}/cancel")
    public Mono<CourseApplicationDto> cancel(@PathVariable Long id) {
        log.info("cancel applicationId: {} by reviewerId: {}", id, getCurrentUserId());

        return service.cancel(id, getCurrentUserId());
    }

    private Long getCurrentUserId() {
        return userContext.getAppUser().getId();
    }

    /**
     * ADMIN starts review the course application
     * @param id course application id
     * @return the IN_PROGRESS application
     */
    @RequireRoles({"ADMIN"})
    @PutMapping("/{id}/review")
    public Mono<CourseApplicationDto> startReview(@PathVariable Long id) {
        log.info("start review applicationId: {} by reviewerId: {}", id, getCurrentUserId());

        return service.startReview(id, getCurrentUserId());
    }

    /**
     * ADMIN approves/rejects the course application
     * @param id course application id
     * @return the approved/rejected course application
     */
    @RequireRoles({"ADMIN"})
    @PutMapping("/{id}/decide")
    public Mono<CourseApplicationDto> decide(@PathVariable Long id,
                                                       @RequestBody @Valid CourseApplicationDecisionRequest request) {
        log.info("{} applicationId: {} by reviewerId: {}", request.isApprove() ? "approve" : "reject", id, getCurrentUserId());

        return service.decide(id, getCurrentUserId(), request.isApprove(), request.getComment());
    }
}
