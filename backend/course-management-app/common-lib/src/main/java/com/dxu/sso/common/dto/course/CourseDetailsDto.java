package com.dxu.sso.common.dto.course;

import com.dxu.sso.common.dto.user.AppUserDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CourseDetailsDto {

    private Long id;
    private String name;
    private String description;
    private Long teacherId;
    private AppUserDto teacher;
    private List<AppUserDto> students;
}
