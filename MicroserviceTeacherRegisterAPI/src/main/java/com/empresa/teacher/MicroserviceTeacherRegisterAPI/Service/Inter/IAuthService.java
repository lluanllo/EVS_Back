package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service.Inter;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO.AuthRequest;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO.AuthResponse;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO.TeacherRequest;

/**
 * Interfaz para autenticación (Single Responsibility)
 */
public interface IAuthService {

    AuthResponse register(TeacherRequest request);

    AuthResponse login(AuthRequest request);

    void logout(String token);

    boolean validateToken(String token);

    String refreshToken(String token);
}
