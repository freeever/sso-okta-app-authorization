package com.dxu.sso.course.registration.controller;

import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.security.RequireRoles;
import com.dxu.sso.common.integration.UserWebClient;
import com.dxu.sso.course.registration.model.CourseRegistration;
import com.dxu.sso.course.registration.repository.CourseRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/course-registration")
@RequiredArgsConstructor
public class CourseRegistrationController {

    private final CourseRegistrationRepository registrationRepo;
    private final UserWebClient userWebClient;

    @RequireRoles({"STUDENT"})
    @PostMapping
    public ResponseEntity<?> register(@RequestParam Long courseId) {
        AppUserDto user = userWebClient.getUserProfile();

        CourseRegistration reg = CourseRegistration.builder()
                .courseId(courseId)
                .studentEmail(user.getEmail())
                .status("PENDING")
                .build();

        return ResponseEntity.ok(registrationRepo.save(reg));
    }

    @RequireRoles({"STUDENT"})
    @GetMapping
    public ResponseEntity<List<CourseRegistration>> myRegistrations() {
        AppUserDto user = userWebClient.getUserProfile();
        return ResponseEntity.ok(registrationRepo.findByStudentEmail(user.getEmail()));
    }

}
