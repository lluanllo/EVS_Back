package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service.Inter;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO.AuthResponse;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO.LoginRequest;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO.RegisterRequest;

/**
 * Interfaz para autenticación (Single Responsibility)
 */
public interface IAuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void logout(String token);

    boolean validateToken(String token);

    String refreshToken(String token);
}

