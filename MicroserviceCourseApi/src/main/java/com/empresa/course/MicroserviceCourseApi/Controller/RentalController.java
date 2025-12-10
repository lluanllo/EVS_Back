package com.empresa.course.MicroserviceCourseApi.Controller;

import com.empresa.course.MicroserviceCourseApi.Entities.Equipment;
import com.empresa.course.MicroserviceCourseApi.Entities.Rental;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.EquipmentType;
import com.empresa.course.MicroserviceCourseApi.Service.Inter.IRentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final IRentalService rentalService;

    // ===== Equipment Endpoints =====

    @PostMapping("/equipment")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Equipment> createEquipment(@RequestBody Equipment equipment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentalService.createEquipment(equipment));
    }

    @PutMapping("/equipment/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Equipment> updateEquipment(@PathVariable Long id, @RequestBody Equipment equipment) {
        return ResponseEntity.ok(rentalService.updateEquipment(id, equipment));
    }

    @GetMapping("/equipment/{id}")
    public ResponseEntity<Equipment> getEquipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.getEquipmentById(id));
    }

    @GetMapping("/equipment")
    public ResponseEntity<List<Equipment>> getAllEquipment() {
        return ResponseEntity.ok(rentalService.getAllEquipment());
    }

    @GetMapping("/equipment/available")
    public ResponseEntity<List<Equipment>> getAvailableEquipment() {
        return ResponseEntity.ok(rentalService.getAvailableEquipment());
    }

    @GetMapping("/equipment/available/type/{type}")
    public ResponseEntity<List<Equipment>> getAvailableEquipmentByType(@PathVariable EquipmentType type) {
        return ResponseEntity.ok(rentalService.getAvailableEquipmentByType(type));
    }

    @DeleteMapping("/equipment/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        rentalService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/equipment/{id}/maintenance")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<Equipment> setEquipmentMaintenance(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.setEquipmentMaintenance(id));
    }

    // ===== Rental Endpoints =====

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<Rental> createRental(
            @RequestParam Long equipmentId,
            @RequestParam Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam Long registeredBy) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rentalService.createRental(equipmentId, studentId, startTime, endTime, registeredBy));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<Rental> completeRental(
            @PathVariable Long id,
            @RequestParam(required = false) String returnCondition,
            @RequestParam(required = false) String damageReported) {
        return ResponseEntity.ok(rentalService.completeRental(id, returnCondition, damageReported));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<Rental> cancelRental(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.cancelRental(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRentalById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.getRentalById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<List<Rental>> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<List<Rental>> getActiveRentals() {
        return ResponseEntity.ok(rentalService.getActiveRentals());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Rental>> getRentalsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(rentalService.getRentalsByStudent(studentId));
    }

    @GetMapping("/student/{studentId}/active")
    public ResponseEntity<List<Rental>> getActiveRentalsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(rentalService.getActiveRentalsByStudent(studentId));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<List<Rental>> getOverdueRentals() {
        return ResponseEntity.ok(rentalService.getOverdueRentals());
    }

    @GetMapping("/unpaid")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<Rental>> getUnpaidCompletedRentals() {
        return ResponseEntity.ok(rentalService.getUnpaidCompletedRentals());
    }

    // ===== Aptitude Verification =====

    @GetMapping("/verify-aptitude")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<Map<String, Object>> verifyStudentAptitude(
            @RequestParam Long studentId,
            @RequestParam Long equipmentId) {
        boolean apt = rentalService.verifyStudentAptitude(studentId, equipmentId);
        return ResponseEntity.ok(Map.of(
                "studentId", studentId,
                "equipmentId", equipmentId,
                "apt", apt,
                "message", apt ? "El estudiante tiene aptitud para alquilar" : "El estudiante no cumple requisitos"
        ));
    }

    @PostMapping("/{id}/verify-aptitude")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<Rental> markAptitudeVerified(
            @PathVariable Long id,
            @RequestParam Long verifiedBy) {
        return ResponseEntity.ok(rentalService.markAptitudeVerified(id, verifiedBy));
    }
}

