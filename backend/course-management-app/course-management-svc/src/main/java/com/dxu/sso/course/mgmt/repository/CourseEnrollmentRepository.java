package com.dxu.sso.course.mgmt.repository;

import com.dxu.sso.common.model.course.CourseEnrollment;
import com.dxu.sso.common.model.course.CourseEnrollmentId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, CourseEnrollmentId> {

    @Modifying
    @Transactional
    @Query("DELETE FROM CourseEnrollment ce WHERE ce.id.courseId = :courseId")
    void deleteByCourseId(@Param("courseId") Long courseId);
}
