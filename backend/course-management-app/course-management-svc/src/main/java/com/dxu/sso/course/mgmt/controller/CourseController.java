package com.dxu.sso.course.mgmt.controller;

import com.dxu.sso.common.model.Course;
import com.dxu.sso.common.security.RequireRoles;
import com.dxu.sso.common.security.RequireUserProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    @Value("${user-profile.url:}")
    private String userProfileUrl;

    @RequireUserProfile
    @RequireRoles({"ADMIN"})
    @PostMapping()
    public ResponseEntity<Course> create(@AuthenticationPrincipal Jwt jwt,
                                    Course course) {
        log.info("[course-management-svc] - Create new course");

        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }
}
