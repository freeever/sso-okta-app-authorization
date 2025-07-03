package com.dxu.sso.course.query.repository;

import com.dxu.sso.common.model.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>  {

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.enrollments")
    List<Course> findAllWithEnrollments();

}
