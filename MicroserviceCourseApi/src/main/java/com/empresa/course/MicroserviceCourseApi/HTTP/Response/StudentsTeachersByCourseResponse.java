package com.empresa.course.MicroserviceCourseApi.HTTP.Response;

import com.empresa.course.MicroserviceCourseApi.Controller.DTO.StudentDTO;
import com.empresa.course.MicroserviceCourseApi.Controller.DTO.TeacherDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentsTeachersByCourseResponse {

    private String CourseName;
    private List<StudentDTO> studentsDTOList;
    private List<TeacherDTO> teachersDTOList;
}
