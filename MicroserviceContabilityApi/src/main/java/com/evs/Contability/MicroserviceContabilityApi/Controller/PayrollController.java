package com.evs.Contability.MicroserviceContabilityApi.Controller;

import com.evs.Contability.MicroserviceContabilityApi.DTO.PayrollSummaryDTO;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Payroll;
import com.evs.Contability.MicroserviceContabilityApi.Service.Inter.IPayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payrolls")
@RequiredArgsConstructor
public class PayrollController {

    private final IPayrollService payrollService;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Payroll> generatePayroll(
            @RequestParam Long teacherId,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(payrollService.generatePayroll(teacherId, month, year));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<Payroll> getPayrollById(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.getPayrollById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<Payroll>> getAllPayrolls() {
        return ResponseEntity.ok(payrollService.getAllPayrolls());
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<List<Payroll>> getPayrollsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(payrollService.getPayrollsByTeacher(teacherId));
    }

    @GetMapping("/teacher/{teacherId}/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<Payroll> getPayrollByTeacherAndMonth(
            @PathVariable Long teacherId,
            @PathVariable Integer month,
            @PathVariable Integer year) {
        return ResponseEntity.ok(payrollService.getPayrollByTeacherAndMonth(teacherId, month, year));
    }

    @GetMapping("/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<Payroll>> getPayrollsByMonth(
            @PathVariable Integer month,
            @PathVariable Integer year) {
        return ResponseEntity.ok(payrollService.getPayrollsByMonth(month, year));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<Payroll>> getPendingPayrolls() {
        return ResponseEntity.ok(payrollService.getPendingPayrolls());
    }

    @GetMapping("/approved-unpaid")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<Payroll>> getApprovedUnpaidPayrolls() {
        return ResponseEntity.ok(payrollService.getApprovedUnpaidPayrolls());
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Payroll> approvePayroll(
            @PathVariable Long id,
            @RequestParam Long approvedBy) {
        return ResponseEntity.ok(payrollService.approvePayroll(id, approvedBy));
    }

    @PostMapping("/{id}/mark-paid")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Payroll> markAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.markAsPaid(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Void> deletePayroll(@PathVariable Long id) {
        payrollService.deletePayroll(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<PayrollSummaryDTO> getPayrollSummary(
            @PathVariable Integer month,
            @PathVariable Integer year) {
        return ResponseEntity.ok(payrollService.getPayrollSummary(month, year));
    }
}

