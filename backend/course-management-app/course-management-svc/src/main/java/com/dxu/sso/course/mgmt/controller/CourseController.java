package com.dxu.sso.course.mgmt.controller;

import com.dxu.sso.common.dto.course.CourseDetailsDto;
import com.dxu.sso.common.security.RequireRoles;
import com.dxu.sso.common.dto.course.CourseSaveRequest;
import com.dxu.sso.course.mgmt.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/manage/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @RequireRoles({"ADMIN"})
    @PostMapping()
    public ResponseEntity<CourseDetailsDto> create(@RequestBody @Valid CourseSaveRequest request) {
        log.info("[course-management-svc] - Create new course");

        CourseDetailsDto course = courseService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    @RequireRoles({"ADMIN"})
    @PutMapping("/{id}")
    public ResponseEntity<CourseDetailsDto> update(@PathVariable Long id,
                                                   @RequestBody @Valid CourseSaveRequest request) {
        log.info("[course-management-svc] - Update course");

        CourseDetailsDto course = courseService.updateCourse(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    @RequireRoles({"ADMIN"})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("[course-management-svc] - Delete course: {}", id);

        courseService.deleteById(id);
    }
}
