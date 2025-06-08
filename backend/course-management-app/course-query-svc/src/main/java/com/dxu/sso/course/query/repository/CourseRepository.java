package com.dxu.sso.course.query.repository;

import com.dxu.sso.common.model.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>  {

    @Query(value = "SELECT student_id FROM course_enrollment WHERE course_id = :courseId", nativeQuery = true)
    List<Long> findStudentIdsByCourseId(@Param("courseId") Long courseId);

}
