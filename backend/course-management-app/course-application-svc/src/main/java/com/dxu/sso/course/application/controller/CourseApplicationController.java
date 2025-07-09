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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @RequireRoles({"STUDENT", "TEACHER", "ADMIN"})
    @GetMapping("/by-course/{courseId}/{status}")
    public ResponseEntity<List<CourseApplicationDto>> findByCourseAndStatus(@PathVariable("courseId") Long courseId,
                                                                            @PathVariable("status") CourseApplicationStatus status) {
        log.info("find applications by courseId: {} status: {}", courseId, status);

        List<CourseApplicationDto> applications = service.findByCourseAndStatus(courseId, status);
        return ResponseEntity.ok(applications);
    }

    /**
     * Find the applications of the current logged in STUDENT
     * @param courseId course application id
     * @return the current student's course applications
     */
    @RequireRoles({"STUDENT"})
    @GetMapping("/my")
    public ResponseEntity<List<CourseApplicationDto>> findMyApplications(@RequestParam(required = false) Long courseId) {
        log.info("find my applications for course: {}", courseId == null ? "ALL" : courseId);

        List<CourseApplicationDto> applications = courseId == null ?
                service.findByStudentId(getCurrentUserId())
                : service.findByStudentAndCourse(getCurrentUserId(), courseId);
        return ResponseEntity.ok(applications);
    }

    /**
     * Student applies a course
     * @param request request payload for applying a course
     * @return the created course application
     */
    @RequireRoles({"STUDENT"})
    @PostMapping
    public ResponseEntity<CourseApplicationDto> apply(@RequestBody @Valid CourseApplicationCreateRequest request) {
        log.info("apply courseId: {} studentId: {}", request.getCourseId(), getCurrentUserId());

        CourseApplicationDto application = service.apply(request.getCourseId(), getCurrentUserId());
        return ResponseEntity.ok(application);
    }

    /**
     * Student cancels a pending application
     * @param id course application id
     * @return the cancelled application
     */
    @RequireRoles({"STUDENT"})
    @PutMapping("/{id}/cancel")
    public ResponseEntity<CourseApplicationDto> cancel(@PathVariable("id") Long id) {
        log.info("cancel applicationId: {} by reviewerId: {}", id, getCurrentUserId());

        CourseApplicationDto application = service.cancel(id, getCurrentUserId());
        return ResponseEntity.ok(application);
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
    public ResponseEntity<CourseApplicationDto> startReview(@PathVariable("id") Long id) {
        log.info("start review applicationId: {} by reviewerId: {}", id, getCurrentUserId());

        CourseApplicationDto application = service.startReview(id, getCurrentUserId());
        return ResponseEntity.ok(application);
    }

    /**
     * ADMIN approves/rejects the course application
     * @param id course application id
     * @return the approved/rejected course application
     */
    @RequireRoles({"ADMIN"})
    @PutMapping("/{id}/decide")
    public ResponseEntity<CourseApplicationDto> decide(@PathVariable("id") Long id,
                                                       @RequestBody @Valid CourseApplicationDecisionRequest request) {
        log.info("{} applicationId: {} by reviewerId: {}", request.isApprove() ? "approve" : "reject", id, getCurrentUserId());

        CourseApplicationDto application = service.decide(id, getCurrentUserId(), request.isApprove(), request.getComment());
        return ResponseEntity.ok(application);
    }
}
