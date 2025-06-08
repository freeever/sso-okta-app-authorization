package com.dxu.sso.common.model.course;

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

    @JoinColumn(name = "teacher_id")
    private Long teacherId;    // FK reference to AppUser.id (role=TEACHER)

    // You cannot use a real @ManyToMany here because AppUser is in another schema
    // Maintain the student list manually via the join table
}
