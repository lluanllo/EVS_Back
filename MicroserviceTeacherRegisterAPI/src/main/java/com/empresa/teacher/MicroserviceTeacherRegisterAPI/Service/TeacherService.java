package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO.TeacherRequest;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO.TeacherResponse;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.ContractType;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.Specialty;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Teacher;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de profesores
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Obtener todos los profesores
     */
    public List<TeacherResponse> findAll() {
        return teacherRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtener profesor por ID
     */
    public TeacherResponse findById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + id));
        return toResponse(teacher);
    }

    /**
     * Crear profesor
     */
    @Transactional
    public TeacherResponse create(TeacherRequest request) {
        if (teacherRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (teacherRepository.existsByDni(request.getDni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }

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
                .build();

        return toResponse(teacherRepository.save(teacher));
    }

    /**
     * Actualizar profesor
     */
    @Transactional
    public TeacherResponse update(Long id, TeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + id));

        teacher.setName(request.getName());
        teacher.setLastName(request.getLastName());
        teacher.setPhone(request.getPhone());
        teacher.setSpecialities(request.getSpecialities());
        teacher.setContractType(request.getContractType());
        if (request.getMaxWeeklyHours() != null) {
            teacher.setMaxWeeklyHours(request.getMaxWeeklyHours());
        }

        return toResponse(teacherRepository.save(teacher));
    }

    /**
     * Eliminar profesor
     */
    @Transactional
    public void delete(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new RuntimeException("Profesor no encontrado: " + id);
        }
        teacherRepository.deleteById(id);
    }

    /**
     * Obtener profesores disponibles por especialidad
     */
    public List<TeacherResponse> findAvailableBySpeciality(Specialty speciality) {
        return teacherRepository.findAvailableBySpecialityOrderByPriorityAndHours(speciality).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtener profesores por tipo de contrato
     */
    public List<TeacherResponse> findByContractType(ContractType contractType) {
        return teacherRepository.findByContractTypeOrderByWorkedHoursAsc(contractType).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar disponibilidad
     */
    @Transactional
    public void updateAvailability(Long id, boolean available) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + id));
        teacher.setAvailable(available);
        teacherRepository.save(teacher);
    }

    /**
     * Confirmar disponibilidad para horario
     */
    @Transactional
    public void confirmAvailability(Long id, boolean confirmed) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + id));
        teacher.setConfirmedAvailability(confirmed);
        if (!confirmed) {
            teacher.setAvailable(false);
        }
        teacherRepository.save(teacher);
    }

    /**
     * Añadir horas trabajadas
     */
    @Transactional
    public void addWorkedHours(Long id, int hours) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + id));
        teacher.setWorkedHours(teacher.getWorkedHours() + hours);
        teacherRepository.save(teacher);
    }

    /**
     * Resetear horas trabajadas (inicio de semana)
     */
    @Transactional
    public void resetAllWorkedHours() {
        List<Teacher> teachers = teacherRepository.findAll();
        teachers.forEach(t -> t.setWorkedHours(0));
        teacherRepository.saveAll(teachers);
    }

    private TeacherResponse toResponse(Teacher teacher) {
        return TeacherResponse.builder()
                .id(teacher.getId())
                .name(teacher.getName())
                .lastName(teacher.getLastName())
                .dni(teacher.getDni())
                .phone(teacher.getPhone())
                .email(teacher.getEmail())
                .specialities(teacher.getSpecialities())
                .contractType(teacher.getContractType())
                .workedHours(teacher.getWorkedHours())
                .maxWeeklyHours(teacher.getMaxWeeklyHours())
                .available(teacher.getAvailable())
                .confirmedAvailability(teacher.getConfirmedAvailability())
                .build();
    }
}
