package com.dxu.sso.common.dto.course;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CourseSaveRequest {

    @NotBlank
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    private Long teacherId;
    private List<Long> enrolledStudentIds;
}
