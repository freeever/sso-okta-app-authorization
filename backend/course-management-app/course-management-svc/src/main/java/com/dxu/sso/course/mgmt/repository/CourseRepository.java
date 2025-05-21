package com.dxu.sso.course.mgmt.repository;

import com.dxu.sso.course.mgmt.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTeacherEmail(String teacherEmail);
}
