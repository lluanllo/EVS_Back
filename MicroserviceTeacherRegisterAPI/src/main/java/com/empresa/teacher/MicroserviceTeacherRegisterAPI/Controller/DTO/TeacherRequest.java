package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.ContractType;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.Specialty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String lastName;

    @NotBlank(message = "El DNI es obligatorio")
    private String dni;

    private String phone;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotNull(message = "Las especialidades son obligatorias")
    private Set<Specialty> specialities;

    @NotNull(message = "El tipo de contrato es obligatorio")
    private ContractType contractType;

    private Integer maxWeeklyHours;
}
