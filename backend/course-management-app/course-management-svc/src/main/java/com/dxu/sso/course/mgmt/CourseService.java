package com.dxu.sso.course.mgmt;

import com.dxu.sso.common.dto.course.CourseDetailsDto;
import com.dxu.sso.common.dto.course.CourseSaveRequest;
import com.dxu.sso.common.dto.mapper.CourseMapper;
import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.integration.UserWebClient;
import com.dxu.sso.common.model.course.Course;
import com.dxu.sso.common.model.course.CourseEnrollment;
import com.dxu.sso.common.model.course.CourseEnrollmentId;
import com.dxu.sso.course.mgmt.repository.CourseEnrollmentRepository;
import com.dxu.sso.course.mgmt.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

        // 💥 Rebuild enrollment list
        List<CourseEnrollment> newEnrollments = request.getEnrolledStudentIds() != null
                ? request.getEnrolledStudentIds().stream()
                .map(studentId -> CourseEnrollment.builder()
                        .id(new CourseEnrollmentId(course.getId(), studentId))
                        .course(course)
                        .build())
                .toList()
                : new ArrayList<>();

        // ⚠️ Clear and replace enrollments
        course.getEnrollments().clear();
        course.getEnrollments().addAll(newEnrollments);

        Course updated = courseRepository.save(course);
        return getCourseDetails(updated);
    }

    public void deleteById(Long id) {
        courseEnrollmentRepository.deleteByCourseId(id);
        courseRepository.deleteById(id);
    }

    private CourseDetailsDto getCourseDetails(Course course) {
        // Fetch teacher
        AppUserDto teacher = course.getTeacherId() != null ? userWebClient.getUserById(course.getTeacherId()).block() : null;
        List<Long> studentIds = course.getEnrollments().stream()
                .map(e -> e.getId().getStudentId())
                .toList();

        // Fetch students
        List<AppUserDto> students = userWebClient.getUsersByIds(studentIds).collectList().block();

        return courseMapper.toDetailsDto(course, teacher, students);
    }

}
