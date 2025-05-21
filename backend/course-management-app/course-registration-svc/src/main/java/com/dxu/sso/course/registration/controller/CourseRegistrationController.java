package com.dxu.sso.course.registration.controller;

import com.dxu.sso.common.model.AppUser;
import com.dxu.sso.common.security.RequireRoles;
import com.dxu.sso.common.service.ProfileWebClient;
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
    private final ProfileWebClient profileWebClient;

    @RequireRoles({"STUDENT"})
    @PostMapping
    public ResponseEntity<?> register(@RequestParam Long courseId) {
        AppUser user = profileWebClient.getUserProfile();

        CourseRegistration reg = CourseRegistration.builder()
                .courseId(courseId)
                .studentEmail(user.getEmail())
                .status("PENDING")
                .build();

        return ResponseEntity.ok(registrationRepo.save(reg));
    }

    @RequireRoles({"TEACHER", "ADMIN"})
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseRegistration>> getRegistrationsForCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(registrationRepo.findByCourseId(courseId));
    }

    @RequireRoles({"STUDENT"})
    @GetMapping
    public ResponseEntity<List<CourseRegistration>> myRegistrations() {
        AppUser user = profileWebClient.getUserProfile();
        return ResponseEntity.ok(registrationRepo.findByStudentEmail(user.getEmail()));
    }

    @RequireRoles({"TEACHER"})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        CourseRegistration reg = registrationRepo.findById(id).orElse(null);
        if (reg == null) {
            return ResponseEntity.notFound().build();
        }

        reg.setStatus(status.toUpperCase());
        return ResponseEntity.ok(registrationRepo.save(reg));
    }
}
