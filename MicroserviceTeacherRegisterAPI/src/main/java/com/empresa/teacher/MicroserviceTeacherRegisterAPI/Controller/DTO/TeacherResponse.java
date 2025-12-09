package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.ContractType;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.Speciality;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherResponse {
    private Long id;
    private String name;
    private String lastName;
    private String dni;
    private String phone;
    private String email;
    private Set<Speciality> specialities;
    private ContractType contractType;
    private Integer workedHours;
    private Integer maxWeeklyHours;
    private Boolean available;
    private Boolean confirmedAvailability;
}

