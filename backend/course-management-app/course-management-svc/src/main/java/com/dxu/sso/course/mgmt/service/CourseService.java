package com.dxu.sso.course.mgmt.service;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final CourseMapper courseMapper;
    private final UserWebClient userWebClient;

    @Transactional
    public CourseDetailsDto create(CourseSaveRequest request) {
        Course course = courseMapper.toEntity(request);
        if (request.getEnrolledStudentIds() != null && !request.getEnrolledStudentIds().isEmpty()) {
            Set<Long> studentIds = new HashSet<>(request.getEnrolledStudentIds());
            addNewEnrollments(course, studentIds, new HashSet<>());
        }
        Course saved =  courseRepository.save(course);

        return getCourseDetails(saved);
    }

    @Transactional
    public CourseDetailsDto updateCourse(Long id, CourseSaveRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new SsoApplicationException(HttpStatus.BAD_REQUEST.value(), "Course not found"));

        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setStartDate(request.getStartDate());
        course.setEndDate(request.getEndDate());
        course.setTeacherId(request.getTeacherId());

        processEnrollments(request, course);

        Course updated = courseRepository.save(course); // dirty checking handles diffs
        return getCourseDetails(updated);
    }

    /**
     * Compare the enrollments (enrolledStudentIds) in the request and the existing enrollments in DB, and
     *  - dd new enrollments, and
     *  - delete enrollments do not exist in the enrollment of the request
     * @param request the payload of request for saving course data
     * @param course the course data to be saved
     */
    private static void processEnrollments(CourseSaveRequest request, Course course) {
        // Normalize submitted students IDs
        Set<Long> submittedStudentIds = Optional.ofNullable(request.getEnrolledStudentIds())
                .map(HashSet::new)
                .orElseGet(HashSet::new);

        // Build a quick lookup of current studentIds
        Set<Long> existingStudentIds = course.getEnrollments().stream()
                .map(CourseEnrollment::getStudentId)
                .collect(Collectors.toSet());

        // 1) Remove enrollment NOT in submitted (orphanRemoval will delete rows)
        course.getEnrollments().removeIf(enrol -> !submittedStudentIds.contains(enrol.getStudentId()));

        // 2) Add new enrollments (keep existing ones untouched -> preserves createdAt
        addNewEnrollments(course, submittedStudentIds, existingStudentIds);
    }

    private static void addNewEnrollments(Course course, Set<Long> studentIds, Set<Long> existingStudentIds) {
        for (Long studentId : studentIds) {
            if (!existingStudentIds.contains(studentId)) {
                course.getEnrollments().add(CourseEnrollment.builder()
                                .id(new CourseEnrollmentId(course.getId(), studentId))
                                .course(course)
                                .createdAt(LocalDateTime.now())
                                .studentDeleted(false)
                                .build());
            }
        }
    }

    @Transactional
    public void deleteById(Long id) {
        courseEnrollmentRepository.deleteByCourseId(id);
        courseRepository.deleteById(id);
    }

    private CourseDetailsDto getCourseDetails(Course course) {
        // Fetch teacher
        AppUserDto teacher = course.getTeacherId() != null ? userWebClient.getUserById(course.getTeacherId()) : null;
        List<Long> studentIds = course.getEnrollments() == null ? Collections.emptyList() :
                course.getEnrollments().stream()
                        .map(e -> e.getId().getStudentId())
                        .toList();

        // Fetch students
        List<AppUserDto> students = userWebClient.getUsersByIds(studentIds);

        return courseMapper.toDetailsDto(course, teacher, students);
    }

}
