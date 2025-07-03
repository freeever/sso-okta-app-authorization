package com.dxu.sso.common.dto.mapper;

import com.dxu.sso.common.dto.course.CourseDetailsDto;
import com.dxu.sso.common.dto.course.CourseDto;
import com.dxu.sso.common.dto.course.CourseSaveRequest;
import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.model.course.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseDto toDto(Course course);

    // We’ll use this to build a new Course (without enrollments)
    @Mapping(target = "enrollments", ignore = true) // handled in service
    Course toEntity(CourseSaveRequest request);

    @Mapping(target = "id", source = "course.id")
    @Mapping(target = "name", source = "course.name")
    @Mapping(target = "description", source = "course.description")
    @Mapping(target = "startDate", source = "course.startDate")
    @Mapping(target = "endDate", source = "course.endDate")
    @Mapping(target = "teacherId", source = "course.teacherId")
    @Mapping(target = "enrolledStudentIds", expression = "java(course.getEnrollments().stream().map(e -> e.getId().getStudentId()).toList())")
    @Mapping(target = "teacher", source = "teacher")
    @Mapping(target = "enrolledStudents", source = "students")
    CourseDetailsDto toDetailsDto(Course course, AppUserDto teacher, List<AppUserDto> students);
}
