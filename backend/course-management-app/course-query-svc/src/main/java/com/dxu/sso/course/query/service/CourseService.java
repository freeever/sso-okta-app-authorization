package com.dxu.sso.course.query.service;

import com.dxu.sso.common.dto.course.CourseDetailsDto;
import com.dxu.sso.common.dto.course.CourseDto;
import com.dxu.sso.common.dto.mapper.CourseMapper;
import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.model.course.Course;
import com.dxu.sso.common.integration.UserWebClient;
import com.dxu.sso.course.query.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CourseService {

    private final CourseRepository courseRepo;
    private final CourseMapper courseMapper;
    private final UserWebClient userWebClient;

    public List<CourseDto> findAll() throws SsoApplicationException {
        return courseRepo.findAll().stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }

    public CourseDetailsDto getCourseDetails(Long id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new SsoApplicationException(HttpStatus.BAD_REQUEST.value(), "Course not found"));
        CourseDetailsDto courseDetails = courseMapper.toDetailsDto(course);

        // Fetch teacher
        if (course.getTeacherId() != null) {
            AppUserDto teacher = userWebClient.getUserById(id);
            courseDetails.setTeacher(teacher);
        }

        // Fetch students
        List<Long> studentIds = courseRepo.findStudentIdsByCourseId(id);
        List<AppUserDto> students = userWebClient.getUsersByIds(studentIds);
        courseDetails.setStudents(students);

        return courseDetails;
    }
}
