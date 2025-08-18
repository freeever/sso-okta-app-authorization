package com.dxu.sso.course.mgmt.repository;

import com.dxu.sso.common.model.course.CourseEnrollment;
import com.dxu.sso.common.model.course.CourseEnrollmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, CourseEnrollmentId> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM CourseEnrollment ce WHERE ce.id.courseId = :courseId")
    void deleteByCourseId(@Param("courseId") Long courseId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update CourseEnrollment e set e.studentDeleted = :studentDeleted where e.id.studentId = :studentId")
    int markStudentDeleted(@Param("studentId") Long studentId, @Param("studentDeleted") boolean studentDeleted);
}
