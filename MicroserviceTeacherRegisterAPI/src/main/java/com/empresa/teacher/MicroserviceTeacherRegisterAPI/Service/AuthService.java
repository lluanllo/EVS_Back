package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO.AuthRequest;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO.AuthResponse;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO.TeacherRequest;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.Role;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Teacher;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.User;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Repository.TeacherRepository;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Repository.UserRepository;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de autenticación
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registra un nuevo profesor
     */
    @Transactional
    public AuthResponse registerTeacher(TeacherRequest request) {
        // Verificar que no existe el email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (teacherRepository.existsByDni(request.getDni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }

        // Crear profesor
        Teacher teacher = Teacher.builder()
                .name(request.getName())
                .lastName(request.getLastName())
                .dni(request.getDni())
                .phone(request.getPhone())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .specialities(request.getSpecialities())
                .contractType(request.getContractType())
                .maxWeeklyHours(request.getMaxWeeklyHours() != null ? request.getMaxWeeklyHours() : 40)
                .workedHours(0)
                .available(true)
                .confirmedAvailability(false)
                .role(Role.TEACHER)
                .build();

        teacher = teacherRepository.save(teacher);

        // Crear usuario
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .lastName(request.getLastName())
                .role(Role.TEACHER)
                .referenceId(teacher.getId())
                .enabled(true)
                .build();

        user = userRepository.save(user);

        // Generar token
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .userId(user.getId())
                .referenceId(teacher.getId())
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    /**
     * Inicia sesión
     */
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .userId(user.getId())
                .referenceId(user.getReferenceId())
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    /**
     * Registra un administrador (solo para uso interno)
     */
    @Transactional
    public AuthResponse registerAdmin(String email, String password, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        user = userRepository.save(user);

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .userId(user.getId())
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }
}

