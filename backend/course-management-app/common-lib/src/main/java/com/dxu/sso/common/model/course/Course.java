package com.dxu.sso.common.model.course;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "course", schema = "ssocourse", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    @JoinColumn(name = "teacher_id")
    private Long teacherId;    // FK reference to AppUser.id (role=TEACHER)

    @ElementCollection
    @CollectionTable(name = "course_enrollment", schema = "ssocourse", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "student_id")
    private List<Long> enrolledStudentIds = new ArrayList<>();
}
