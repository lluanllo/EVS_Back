package com.evs.Contability.MicroserviceContabilityApi.Controller;

import com.evs.Contability.MicroserviceContabilityApi.DTO.SalaryDTO;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Salary;
import com.evs.Contability.MicroserviceContabilityApi.Service.Inter.ISalaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final ISalaryService salaryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Salary> createSalary(@Valid @RequestBody SalaryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salaryService.createSalary(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Salary> updateSalary(@PathVariable Long id, @Valid @RequestBody SalaryDTO dto) {
        return ResponseEntity.ok(salaryService.updateSalary(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Salary> getSalaryById(@PathVariable Long id) {
        return ResponseEntity.ok(salaryService.getSalaryById(id));
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<Salary> getSalaryByTeacherId(@PathVariable Long teacherId) {
        return ResponseEntity.ok(salaryService.getSalaryByTeacherId(teacherId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<Salary>> getAllSalaries() {
        return ResponseEntity.ok(salaryService.getAllSalaries());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<Salary>> getActiveSalaries() {
        return ResponseEntity.ok(salaryService.getActiveSalaries());
    }

    @GetMapping("/contract-type/{contractType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<Salary>> getSalariesByContractType(@PathVariable String contractType) {
        return ResponseEntity.ok(salaryService.getSalariesByContractType(contractType));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Void> deleteSalary(@PathVariable Long id) {
        salaryService.deleteSalary(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/teacher/{teacherId}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Void> deactivateSalary(@PathVariable Long teacherId) {
        salaryService.deactivateSalary(teacherId);
        return ResponseEntity.ok().build();
    }
}

