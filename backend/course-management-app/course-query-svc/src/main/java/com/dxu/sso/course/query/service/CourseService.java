package com.dxu.sso.course.query.service;

import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.model.AppUser;
import com.dxu.sso.common.model.Course;
import com.dxu.sso.common.service.ProfileWebClient;
import com.dxu.sso.course.query.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CourseService {

    private final CourseRepository courseRepo;
    private final ProfileWebClient profileWebClient;

    public List<Course> findCourses(String accessToken) throws SsoApplicationException {
        AppUser user = profileWebClient.getUserProfile();

        return switch (user.getRole()) {
            case "ADMIN" -> courseRepo.findAll();
            case "TEACHER" -> courseRepo.findByTeacherEmail(user.getEmail());
            default -> throw new SsoApplicationException(HttpStatus.FORBIDDEN.value(), "Access denied");
        };
    }
}
