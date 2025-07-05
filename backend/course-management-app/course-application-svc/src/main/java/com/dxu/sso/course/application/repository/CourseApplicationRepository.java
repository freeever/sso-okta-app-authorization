package com.dxu.sso.course.application.repository;

import com.dxu.sso.common.model.CourseApplicationStatus;
import com.dxu.sso.common.model.courseapp.CourseApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseApplicationRepository extends JpaRepository<CourseApplication, Long> {

    List<CourseApplication> findByCourseIdAndStatus(Long courseId, CourseApplicationStatus status);

    List<CourseApplication> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<CourseApplication> findByStudentId(Long studentId);

    boolean existsByStudentIdAndCourseIdAndStatusIn(Long studentId, Long courseId, List<CourseApplicationStatus> statuses);
}
