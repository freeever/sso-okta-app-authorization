package com.dxu.sso.course.application.controller;

import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.security.RequireRoles;
import com.dxu.sso.common.integration.UserWebClient;
import com.dxu.sso.course.application.model.CourseApplication;
import com.dxu.sso.course.application.repository.CourseApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/course-application")
@RequiredArgsConstructor
public class CourseApplicationController {

    private final CourseApplicationRepository applicationRepo;
    private final UserWebClient userWebClient;

    @RequireRoles({"STUDENT"})
    @PostMapping
    public ResponseEntity<?> register(@RequestParam Long courseId) {
        AppUserDto user = userWebClient.getUserProfile();

        CourseApplication reg = CourseApplication.builder()
                .courseId(courseId)
                .studentEmail(user.getEmail())
                .status("PENDING")
                .build();

        return ResponseEntity.ok(applicationRepo.save(reg));
    }

    @RequireRoles({"STUDENT"})
    @GetMapping
    public ResponseEntity<List<CourseApplication>> myApplications() {
        AppUserDto user = userWebClient.getUserProfile();
        return ResponseEntity.ok(applicationRepo.findByStudentEmail(user.getEmail()));
    }

}
