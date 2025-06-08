package com.dxu.sso.common.dto.mapper;

import com.dxu.sso.common.dto.course.CourseDetailsDto;
import com.dxu.sso.common.dto.course.CourseDto;
import com.dxu.sso.common.model.course.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    CourseDto toDto(Course course);

    Course toEntity(CourseDto courseDto);

    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "students", ignore = true)
    CourseDetailsDto toDetailsDto(Course course);
}

