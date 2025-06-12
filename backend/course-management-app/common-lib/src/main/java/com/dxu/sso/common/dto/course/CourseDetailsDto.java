package com.dxu.sso.common.dto.course;

import com.dxu.sso.common.dto.user.AppUserDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CourseDetailsDto {

    private Long id;
    private String name;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Long teacherId;
    private List<Long> enrolledStudentIds;

    private AppUserDto teacher;
    private List<AppUserDto> enrolledStudents;
}
