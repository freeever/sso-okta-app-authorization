package com.dxu.sso.course.query.controller;

import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.model.Course;
import com.dxu.sso.common.security.RequireUserProfile;
import com.dxu.sso.course.query.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/course-query")
public class CourseController {

    private final CourseService courseService;

//    @RequireUserProfile
    @GetMapping
    public ResponseEntity<List<Course>> findCourses(@AuthenticationPrincipal Jwt jwt)
            throws SsoApplicationException {
        log.info("[course-query-svc] - Fetching courses");

        List<Course> courses = courseService.findCourses(jwt.getTokenValue());
        return ResponseEntity.ok(courses);
    }
}
