package com.dxu.sso.course.application.repository;

import com.dxu.sso.course.application.model.CourseApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseApplicationRepository extends JpaRepository<CourseApplication, Long> {
    List<CourseApplication> findByStudentEmail(String email);
    List<CourseApplication> findByCourseId(Long courseId);
}
