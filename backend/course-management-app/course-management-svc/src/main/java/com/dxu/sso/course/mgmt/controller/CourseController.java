package com.dxu.sso.course.mgmt.controller;

import com.dxu.sso.course.mgmt.model.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/course-mgmt")
@RequiredArgsConstructor
public class CourseController {

    private final WebClient.Builder webClientBuilder;

    @Value("${user-profile.url:}")
    private String userProfileUrl;

    @GetMapping
    public ResponseEntity<?> findCourses(@AuthenticationPrincipal Jwt jwt) {
        log.info("[course-management-svc] - Fetching courses");

        String accessToken = jwt.getTokenValue();

        AppUser user = webClientBuilder.build()
                .get()
                .uri(userProfileUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(AppUser.class)
                .block();

        if (user == null || user.getRole() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User profile or role not found");
        }

        // Simulated course data
        List<String> courses = switch (user.getRole()) {
            case "ADMIN" -> List.of("All Courses (Admin View)");
            case "TEACHER" -> List.of("Assigned Courses (Teacher View)");
            case "STUDENT" -> List.of("Available Courses (Student View)");
            default -> List.of("No access - unknown role");
        };

        return ResponseEntity.ok(courses);
    }
}
