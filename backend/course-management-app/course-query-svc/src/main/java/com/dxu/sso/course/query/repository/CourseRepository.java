package com.dxu.sso.course.query.repository;

import com.dxu.sso.common.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long>  {

    List<Course> findByTeacherEmail(String teacherEmail);
}
