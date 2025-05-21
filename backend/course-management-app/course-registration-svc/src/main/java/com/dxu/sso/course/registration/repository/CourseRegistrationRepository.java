package com.dxu.sso.course.registration.repository;

import com.dxu.sso.course.registration.model.CourseRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, Long> {
    List<CourseRegistration> findByStudentEmail(String email);
    List<CourseRegistration> findByCourseId(Long courseId);
}
