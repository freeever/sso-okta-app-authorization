package com.dxu.sso.common.model.course;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "course_enrollment", schema = "ssocourse")
public class CourseEnrollment {

    @EmbeddedId
    private CourseEnrollmentId id;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
