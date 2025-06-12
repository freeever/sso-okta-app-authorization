package com.dxu.sso.common.dto.mapper;

import com.dxu.sso.common.dto.course.CourseDetailsDto;
import com.dxu.sso.common.dto.course.CourseDto;
import com.dxu.sso.common.dto.course.CourseSaveRequest;
import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.model.course.Course;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseMapperTest {

    private final CourseMapper mapper = Mappers.getMapper(CourseMapper.class);

    @Test
    void testToDto() {
        Course course = new Course();
        course.setId(1L);
        course.setName("Math 101");
        course.setDescription("Basic math");
        course.setTeacherId(10L);

        CourseDto dto = mapper.toDto(course);

        assertEquals(1L, dto.getId());
        assertEquals("Math 101", dto.getName());
        assertEquals("Basic math", dto.getDescription());
        assertEquals(10L, dto.getTeacherId());
    }

    @Test
    void testToEntity() {
        CourseSaveRequest request = new CourseSaveRequest();
        request.setName("Science");
        request.setDescription("Physics and Chemistry");
        request.setTeacherId(12L);
        request.setEnrolledStudentIds(List.of(101L, 102L));

        Course course = mapper.toEntity(request);

        assertEquals("Science", course.getName());
        assertEquals("Physics and Chemistry", course.getDescription());
        assertEquals(12L, course.getTeacherId());
    }

    @Test
    void testToDetailsDto() {
        Course course = new Course();
        course.setId(2L);
        course.setName("English");
        course.setDescription("Grammar and Composition");
        course.setTeacherId(22L);

        AppUserDto teacher = new AppUserDto(22L, "okta1", "teacher@example.com", "Jane", "Doe", "TEACHER", "F", LocalDate.of(1985, 5, 10));

        List<AppUserDto> students = List.of(
                new AppUserDto(31L, "okta2", "student1@example.com", "Tom", "Lee", "STUDENT", "M", LocalDate.of(2000, 1, 1)),
                new AppUserDto(32L, "okta3", "student2@example.com", "Anna", "Kim", "STUDENT", "F", LocalDate.of(2001, 2, 2))
                                           );

        CourseDetailsDto dto = mapper.toDetailsDto(course, teacher, students);

        assertEquals(2L, dto.getId());
        assertEquals("English", dto.getName());
        assertEquals("Grammar and Composition", dto.getDescription());
        assertEquals("Jane", dto.getTeacher().getFirstName());
        assertEquals(2, dto.getEnrolledStudents().size());
        assertEquals("Tom", dto.getEnrolledStudents().get(0).getFirstName());
    }
}
