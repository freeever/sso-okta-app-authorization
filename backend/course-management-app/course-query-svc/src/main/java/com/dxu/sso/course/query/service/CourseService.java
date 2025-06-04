package com.dxu.sso.course.query.service;

import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.model.Course;
import com.dxu.sso.common.service.ProfileWebClient;
import com.dxu.sso.course.query.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CourseService {

    private final CourseRepository courseRepo;
    private final ProfileWebClient profileWebClient;

    public List<Course> findAll() throws SsoApplicationException {
        return courseRepo.findAll();
    }
}
