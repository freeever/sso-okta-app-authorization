package com.dxu.sso.common.dto.mapper;

import com.dxu.sso.common.dto.course.CourseApplicationDto;
import com.dxu.sso.common.model.courseapp.CourseApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseApplicationMapper {

    // keep only basic ID-to-DTO conversion, and do enrichment manually later.
    @Mapping(target = "courseName", ignore = true)
    @Mapping(target = "studentFirstName", ignore = true)
    @Mapping(target = "studentLastName", ignore = true)
    @Mapping(target = "studentEmail", ignore = true)
    @Mapping(target = "reviewerFirstName", ignore = true)
    @Mapping(target = "reviewerLastName", ignore = true)
    @Mapping(target = "reviewerEmail", ignore = true)
    CourseApplicationDto toDto(CourseApplication entity);

    List<CourseApplicationDto> toDtoList(List<CourseApplication> entities);
}
