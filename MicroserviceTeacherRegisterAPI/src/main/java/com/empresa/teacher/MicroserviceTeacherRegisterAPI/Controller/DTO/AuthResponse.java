package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String name;
    private Role role;
    private Long userId;
    private Long referenceId;
    private Long expiresIn;
}

