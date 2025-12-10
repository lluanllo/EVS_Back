package com.evs.Contability.MicroserviceContabilityApi.Controller;

import com.evs.Contability.MicroserviceContabilityApi.DTO.CashDiscrepancyReportDTO;
import com.evs.Contability.MicroserviceContabilityApi.DTO.CashRegisterDTO;
import com.evs.Contability.MicroserviceContabilityApi.Entities.CashRegister;
import com.evs.Contability.MicroserviceContabilityApi.Service.Inter.ICashRegisterService;
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
@RequestMapping("/api/cash-register")
@RequiredArgsConstructor
public class CashRegisterController {

    private final ICashRegisterService cashRegisterService;

    @PostMapping("/open")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<CashRegister> openCashRegister(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cashRegisterService.openCashRegister(date));
    }

    @PostMapping("/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<CashRegister> closeCashRegister(
            @Valid @RequestBody CashRegisterDTO dto,
            @RequestParam Long closedBy) {
        return ResponseEntity.ok(cashRegisterService.closeCashRegister(dto, closedBy));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<CashRegister> getCashRegisterById(@PathVariable Long id) {
        return ResponseEntity.ok(cashRegisterService.getCashRegisterById(id));
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<CashRegister> getCashRegisterByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(cashRegisterService.getCashRegisterByDate(date));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<CashRegister>> getAllCashRegisters() {
        return ResponseEntity.ok(cashRegisterService.getAllCashRegisters());
    }

    @GetMapping("/range")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<CashRegister>> getCashRegistersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(cashRegisterService.getCashRegistersByDateRange(start, end));
    }

    @GetMapping("/discrepancies")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<CashRegister>> getDiscrepancies() {
        return ResponseEntity.ok(cashRegisterService.getDiscrepancies());
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<CashRegister> markAsReviewed(
            @PathVariable Long id,
            @RequestParam String notes) {
        return ResponseEntity.ok(cashRegisterService.markAsReviewed(id, notes));
    }

    @GetMapping("/discrepancy-report/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<CashDiscrepancyReportDTO> generateDiscrepancyReport(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(cashRegisterService.generateDiscrepancyReport(date));
    }

    @PostMapping("/calculate-expected/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<CashRegister> calculateExpectedCash(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(cashRegisterService.calculateExpectedCash(date));
    }
}

