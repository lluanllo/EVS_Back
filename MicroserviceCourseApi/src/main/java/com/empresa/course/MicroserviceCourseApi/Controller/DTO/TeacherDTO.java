package com.empresa.course.MicroserviceCourseApi.Controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDTO {

    private String name;
    private String lastName;
    private String dni;
    private String phone;
    private String email;
    private String speciality;
    private Long IdCourse;
}
