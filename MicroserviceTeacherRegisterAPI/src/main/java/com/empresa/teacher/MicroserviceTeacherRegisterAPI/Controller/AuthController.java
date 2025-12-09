package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO.AuthRequest;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO.AuthResponse;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO.TeacherRequest;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registra un nuevo profesor
     */
    @PostMapping("/register/teacher")
    public ResponseEntity<AuthResponse> registerTeacher(@Valid @RequestBody TeacherRequest request) {
        return ResponseEntity.ok(authService.registerTeacher(request));
    }

    /**
     * Inicia sesión
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Registra un administrador (endpoint protegido en producción)
     */
    @PostMapping("/register/admin")
    public ResponseEntity<AuthResponse> registerAdmin(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String name) {
        return ResponseEntity.ok(authService.registerAdmin(email, password, name));
    }
}

