package com.empresa.course.MicroserviceCourseApi.Controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {

    private String Name;
    private String lastName;
    private boolean Socio;
    private String Email;
    private int Phone1;
    private int Phone2;
    private String CurseLevel;
    private Long IdCourse;
}
