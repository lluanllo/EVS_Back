package com.evs.Contability.MicroserviceContabilityApi.Controller;

import com.evs.Contability.MicroserviceContabilityApi.DTO.WorkedHoursDTO;
import com.evs.Contability.MicroserviceContabilityApi.Entities.WorkedHours;
import com.evs.Contability.MicroserviceContabilityApi.Service.Inter.IWorkedHoursService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/worked-hours")
@RequiredArgsConstructor
public class WorkedHoursController {

    private final IWorkedHoursService workedHoursService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<WorkedHours> registerHours(@Valid @RequestBody WorkedHoursDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workedHoursService.registerHours(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<WorkedHours> updateHours(@PathVariable Long id, @Valid @RequestBody WorkedHoursDTO dto) {
        return ResponseEntity.ok(workedHoursService.updateHours(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<WorkedHours> getById(@PathVariable Long id) {
        return ResponseEntity.ok(workedHoursService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<WorkedHours>> getAllWorkedHours() {
        return ResponseEntity.ok(workedHoursService.getAllWorkedHours());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Void> deleteWorkedHours(@PathVariable Long id) {
        workedHoursService.deleteWorkedHours(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<List<WorkedHours>> getByTeacherId(@PathVariable Long teacherId) {
        return ResponseEntity.ok(workedHoursService.getByTeacherId(teacherId));
    }

    @GetMapping("/teacher/{teacherId}/range")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<List<WorkedHours>> getByTeacherIdAndDateRange(
            @PathVariable Long teacherId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(workedHoursService.getByTeacherIdAndDateRange(teacherId, start, end));
    }

    @GetMapping("/pending-validation")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<WorkedHours>> getPendingValidation() {
        return ResponseEntity.ok(workedHoursService.getPendingValidation());
    }

    @PostMapping("/{id}/validate")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<WorkedHours> validateHours(
            @PathVariable Long id,
            @RequestParam Long validatedBy) {
        return ResponseEntity.ok(workedHoursService.validateHours(id, validatedBy));
    }

    @PostMapping("/validate-multiple")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Void> validateMultiple(
            @RequestBody List<Long> ids,
            @RequestParam Long validatedBy) {
        workedHoursService.validateMultiple(ids, validatedBy);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/teacher/{teacherId}/month/{month}/year/{year}/total-minutes")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<Integer> getTotalMinutesByTeacherAndMonth(
            @PathVariable Long teacherId,
            @PathVariable Integer month,
            @PathVariable Integer year) {
        return ResponseEntity.ok(workedHoursService.getTotalMinutesByTeacherAndMonth(teacherId, month, year));
    }

    @GetMapping("/teacher/{teacherId}/unpaid")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<WorkedHours>> getUnpaidValidatedByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(workedHoursService.getUnpaidValidatedByTeacher(teacherId));
    }
}

