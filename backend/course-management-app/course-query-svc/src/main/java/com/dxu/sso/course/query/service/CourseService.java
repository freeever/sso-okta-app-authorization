package com.dxu.sso.course.query.service;

import com.dxu.sso.common.dto.course.CourseDto;
import com.dxu.sso.common.dto.mapper.CourseMapper;
import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.model.course.Course;
import com.dxu.sso.common.service.ProfileWebClient;
import com.dxu.sso.course.query.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CourseService {

    private final CourseRepository courseRepo;
    private final CourseMapper courseMapper;

    public List<CourseDto> findAll() throws SsoApplicationException {
        List<Course> courses = courseRepo.findAll();
        return courses.stream().map(courseMapper::toDto).collect(Collectors.toList());
    }
}
