package com.empresa.students.MicroserviceStudentsAPI.Controller.DTO;

import com.empresa.students.MicroserviceStudentsAPI.Entities.Enums.SkillLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String lastName;

    private String dni;

    @Email(message = "El email debe ser válido")
    private String email;

    private String password;

    private Boolean socio;

    private String phone1;

    private String phone2;

    private SkillLevel skillLevel;

    private LocalDate birthDate;

    private Set<Long> courseIds;

    private String notes;

    private String emergencyContact;

    private String emergencyPhone;
}

