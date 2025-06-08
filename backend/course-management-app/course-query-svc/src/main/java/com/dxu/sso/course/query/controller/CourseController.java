package com.dxu.sso.course.query.controller;

import com.dxu.sso.common.dto.course.CourseDetailsDto;
import com.dxu.sso.common.dto.course.CourseDto;
import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.model.course.Course;
import com.dxu.sso.common.security.RequireUserProfile;
import com.dxu.sso.course.query.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @RequireUserProfile
    @GetMapping
    public ResponseEntity<List<CourseDto>> findAll()
            throws SsoApplicationException {
        log.info("[course-query-svc] - Fetching courses");

        List<CourseDto> courses = courseService.findAll();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDetailsDto> getCourseDetails(@PathVariable Long id) {
        CourseDetailsDto courseDetails = courseService.getCourseDetails(id);
        return ResponseEntity.ok(courseDetails);
    }

}
