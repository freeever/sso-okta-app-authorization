package com.dxu.sso.course.query.service;

import com.dxu.sso.common.dto.course.CourseDetailsDto;
import com.dxu.sso.common.dto.course.CourseDto;
import com.dxu.sso.common.dto.mapper.CourseMapper;
import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.model.course.Course;
import com.dxu.sso.common.integration.UserWebClient;
import com.dxu.sso.common.model.course.CourseEnrollment;
import com.dxu.sso.common.dto.course.CourseQueryRequest;
import com.dxu.sso.course.query.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CourseService {

    private final CourseRepository courseRepo;
    private final CourseMapper courseMapper;
    private final UserWebClient userWebClient;

    /**
     * Get all courses
     * @return course list
     */
    public List<CourseDto> findAll() throws SsoApplicationException {
        List<Course> courses = courseRepo.findAllWithEnrollments();

        List<Long> teacherIds = courses.stream().map(Course::getTeacherId).toList();
        List<AppUserDto> teachers = userWebClient.getUsersByIds(teacherIds);

        return toCourseDtoList(courses, teachers);
    }

    /**
     * Fetch course details by id
     * @param id course id
     * @return course details
     */
    public CourseDetailsDto findCourseDetails(Long id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new SsoApplicationException(HttpStatus.BAD_REQUEST.value(), "Course not found"));

        // Fetch teacher
        AppUserDto teacher = course.getTeacherId() != null ? userWebClient.getUserById(course.getTeacherId()) : null;
        // Fetch students
        List<AppUserDto> students = userWebClient.getUsersByIds(course.getEnrollments().stream()
                .map(CourseEnrollment::getStudentId)
                .collect(Collectors.toList()));

        return courseMapper.toDetailsDto(course, teacher, students);
    }

    /**
     * Fetch courses by ids
     * @param request request payload
     * @return course list
     */
    public List<CourseDto> findCoursesByIds(CourseQueryRequest request) {
        log.info("find courses by ids: {}", request.getCourseIds());

        List<Course> courses = courseRepo.findByIdIn(request.getCourseIds());
        return courses.stream().map(courseMapper::toDto).collect(Collectors.toList());
    }

    private List<CourseDto> toCourseDtoList(List<Course> courses, List<AppUserDto> teachers) {
        // Index teachers by their ID for fast lookup
        Map<Long, AppUserDto> teacherMap = Optional.ofNullable(teachers)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(AppUserDto::getId, Function.identity()));

        // Map courses to CourseDto and assign teacher from the map
        return Optional.ofNullable(courses)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(course -> {
                    AppUserDto teacher = teacherMap.get(course.getTeacherId());
                    CourseDto dto = courseMapper.toDto(course);
                    dto.setTeacher(teacher);
                    return dto;
                })
                .collect(Collectors.toList());
    }

}
