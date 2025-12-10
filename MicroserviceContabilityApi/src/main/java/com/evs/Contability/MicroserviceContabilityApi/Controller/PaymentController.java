package com.evs.Contability.MicroserviceContabilityApi.Controller;

import com.evs.Contability.MicroserviceContabilityApi.DTO.PaymentDTO;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Payment;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Enums.PaymentMethod;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Enums.PaymentStatus;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Enums.PaymentType;
import com.evs.Contability.MicroserviceContabilityApi.Service.Inter.IPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody PaymentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @Valid @RequestBody PaymentDTO dto) {
        return ResponseEntity.ok(paymentService.updatePayment(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Payment> confirmPayment(
            @PathVariable Long id,
            @RequestParam Long confirmedBy) {
        return ResponseEntity.ok(paymentService.confirmPayment(id, confirmedBy));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Payment> cancelPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.cancelPayment(id));
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Payment> refundPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refundPayment(id));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<List<Payment>> getPaymentsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(paymentService.getPaymentsByStudent(studentId));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<List<Payment>> getPaymentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(paymentService.getPaymentsByCourse(courseId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status));
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<Payment>> getPaymentsByType(@PathVariable PaymentType type) {
        return ResponseEntity.ok(paymentService.getPaymentsByType(type));
    }

    @GetMapping("/method/{method}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<Payment>> getPaymentsByMethod(@PathVariable PaymentMethod method) {
        return ResponseEntity.ok(paymentService.getPaymentsByMethod(method));
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<List<Payment>> getPaymentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(paymentService.getPaymentsByDate(date));
    }

    @GetMapping("/student/{studentId}/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<List<Payment>> getPendingPaymentsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(paymentService.getPendingPaymentsByStudent(studentId));
    }

    @GetMapping("/totals/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<BigDecimal> getTotalByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(paymentService.getTotalByDate(date));
    }

    @GetMapping("/totals/date/{date}/method/{method}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<BigDecimal> getTotalByDateAndMethod(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable PaymentMethod method) {
        return ResponseEntity.ok(paymentService.getTotalByDateAndMethod(date, method));
    }
}

