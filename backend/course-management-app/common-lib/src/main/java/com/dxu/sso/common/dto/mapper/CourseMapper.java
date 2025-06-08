package com.dxu.sso.common.dto.mapper;

import com.dxu.sso.common.dto.course.CourseDto;
import com.dxu.sso.common.model.course.Course;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    CourseDto toDto(Course course);

    Course toEntity(CourseDto courseDto);
}

