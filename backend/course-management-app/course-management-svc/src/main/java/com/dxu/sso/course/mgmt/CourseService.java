package com.dxu.sso.course.mgmt;

import com.dxu.sso.common.dto.course.CourseDetailsDto;
import com.dxu.sso.common.dto.course.CourseSaveRequest;
import com.dxu.sso.common.dto.mapper.CourseMapper;
import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.integration.UserWebClient;
import com.dxu.sso.common.model.course.Course;
import com.dxu.sso.course.mgmt.repository.CourseEnrollmentRepository;
import com.dxu.sso.course.mgmt.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final CourseMapper courseMapper;
    private final UserWebClient userWebClient;

    public CourseDetailsDto create(CourseSaveRequest request) {
        Course course = courseMapper.toEntity(request);
        Course saved =  courseRepository.save(course);

        return getCourseDetails(saved);
    }

    public CourseDetailsDto updateCourse(Long id, CourseSaveRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new SsoApplicationException(HttpStatus.BAD_REQUEST.value(), "Course not found"));

        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setStartDate(request.getStartDate());
        course.setEndDate(request.getEndDate());
        course.setTeacherId(request.getTeacherId());
        course.setEnrolledStudentIds(request.getEnrolledStudentIds());

        Course updated = courseRepository.save(course);
        return getCourseDetails(updated);
    }

    public void deleteById(Long id) {
        courseEnrollmentRepository.deleteByCourseId(id);
        courseRepository.deleteById(id);
    }

    private CourseDetailsDto getCourseDetails(Course saved) {
        // Fetch teacher
        AppUserDto teacher = saved.getTeacherId() != null ? userWebClient.getUserById(saved.getTeacherId()) : null;
        // Fetch students
        List<AppUserDto> students = userWebClient.getUsersByIds(saved.getEnrolledStudentIds());

        return courseMapper.toDetailsDto(saved, teacher, students);
    }

}
