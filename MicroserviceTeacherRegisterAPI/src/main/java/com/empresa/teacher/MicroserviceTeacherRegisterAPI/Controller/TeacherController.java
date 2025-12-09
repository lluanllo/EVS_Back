package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO.TeacherRequest;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller.DTO.TeacherResponse;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.ContractType;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.Speciality;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Teacher;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service.TeacherAssignmentService;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final TeacherAssignmentService assignmentService;

    /**
     * Obtener todos los profesores
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<TeacherResponse>> findAll() {
        return ResponseEntity.ok(teacherService.findAll());
    }

    /**
     * Obtener profesor por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<TeacherResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.findById(id));
    }

    /**
     * Crear profesor
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeacherResponse> create(@Valid @RequestBody TeacherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.create(request));
    }

    /**
     * Actualizar profesor
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeacherResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody TeacherRequest request) {
        return ResponseEntity.ok(teacherService.update(id, request));
    }

    /**
     * Eliminar profesor
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener profesores disponibles por especialidad
     */
    @GetMapping("/available/{speciality}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<TeacherResponse>> findAvailableBySpeciality(
            @PathVariable Speciality speciality) {
        return ResponseEntity.ok(teacherService.findAvailableBySpeciality(speciality));
    }

    /**
     * Obtener profesores por tipo de contrato
     */
    @GetMapping("/contract/{contractType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<TeacherResponse>> findByContractType(
            @PathVariable ContractType contractType) {
        return ResponseEntity.ok(teacherService.findByContractType(contractType));
    }

    /**
     * Actualizar disponibilidad del profesor
     */
    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> updateAvailability(@PathVariable Long id,
                                                    @RequestParam boolean available) {
        teacherService.updateAvailability(id, available);
        return ResponseEntity.ok().build();
    }

    /**
     * Confirmar disponibilidad para horario
     */
    @PostMapping("/{id}/confirm-schedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> confirmSchedule(@PathVariable Long id,
                                                 @RequestParam boolean confirmed) {
        teacherService.confirmAvailability(id, confirmed);
        return ResponseEntity.ok().build();
    }

    /**
     * Asignar profesor automáticamente para una clase
     */
    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignTeacher(@RequestParam Speciality speciality,
                                           @RequestParam int durationMinutes) {
        Optional<Teacher> teacher = assignmentService.assignTeacher(speciality, durationMinutes);
        return teacher
                .map(t -> ResponseEntity.ok(teacherService.findById(t.getId())))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Asignar múltiples profesores para una clase
     */
    @PostMapping("/assign-multiple")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TeacherResponse>> assignMultipleTeachers(
            @RequestParam Speciality speciality,
            @RequestParam int durationMinutes,
            @RequestParam int count) {
        List<Teacher> teachers = assignmentService.assignMultipleTeachers(speciality, durationMinutes, count);
        List<TeacherResponse> responses = teachers.stream()
                .map(t -> teacherService.findById(t.getId()))
                .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * Reasignar profesor
     */
    @PostMapping("/reassign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reassignTeacher(@RequestParam Long originalTeacherId,
                                              @RequestParam Speciality speciality,
                                              @RequestParam int durationMinutes) {
        Optional<Teacher> teacher = assignmentService.reassignTeacher(originalTeacherId, speciality, durationMinutes);
        return teacher
                .map(t -> ResponseEntity.ok(teacherService.findById(t.getId())))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtener estadísticas de distribución de horas
     */
    @GetMapping("/stats/hours-distribution")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<ContractType, Double>> getHoursDistribution() {
        return ResponseEntity.ok(assignmentService.getHoursDistributionStats());
    }

    /**
     * Resetear horas trabajadas (inicio de semana)
     */
    @PostMapping("/reset-hours")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resetWorkedHours() {
        teacherService.resetAllWorkedHours();
        return ResponseEntity.ok().build();
    }
}

