package com.evs.Contability.MicroserviceContabilityApi.Service.Impl;

import com.evs.Contability.MicroserviceContabilityApi.DTO.PaymentDTO;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Enums.PaymentMethod;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Enums.PaymentStatus;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Enums.PaymentType;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Payment;
import com.evs.Contability.MicroserviceContabilityApi.Repository.PaymentRepository;
import com.evs.Contability.MicroserviceContabilityApi.Service.Inter.IPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public Payment createPayment(PaymentDTO dto) {
        Payment payment = Payment.builder()
                .paymentType(dto.getPaymentType())
                .amount(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod())
                .status(PaymentStatus.PENDIENTE)
                .studentId(dto.getStudentId())
                .courseId(dto.getCourseId())
                .rentalId(dto.getRentalId())
                .regattaId(dto.getRegattaId())
                .paymentDate(LocalDateTime.now())
                .description(dto.getDescription())
                .notes(dto.getNotes())
                .receiptNumber(generateReceiptNumber())
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Pago creado: {} - {} - {}", saved.getId(), saved.getPaymentType(), saved.getAmount());

        // Publicar evento de pago creado
        kafkaTemplate.send("payment-events", "payment-created", saved);

        return saved;
    }

    @Override
    @Transactional
    public Payment updatePayment(Long id, PaymentDTO dto) {
        Payment payment = getPaymentById(id);

        payment.setPaymentType(dto.getPaymentType());
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStudentId(dto.getStudentId());
        payment.setCourseId(dto.getCourseId());
        payment.setDescription(dto.getDescription());
        payment.setNotes(dto.getNotes());

        return paymentRepository.save(payment);
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado: " + id));
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("Pago no encontrado: " + id);
        }
        paymentRepository.deleteById(id);
        log.info("Pago eliminado: {}", id);
    }

    @Override
    @Transactional
    public Payment confirmPayment(Long id, Long confirmedBy) {
        Payment payment = getPaymentById(id);
        payment.setStatus(PaymentStatus.CONFIRMADO);
        payment.setConfirmedDate(LocalDateTime.now());
        payment.setRegisteredBy(confirmedBy);

        Payment saved = paymentRepository.save(payment);
        log.info("Pago confirmado: {} por usuario {}", id, confirmedBy);

        kafkaTemplate.send("payment-events", "payment-confirmed", saved);

        return saved;
    }

    @Override
    @Transactional
    public Payment cancelPayment(Long id) {
        Payment payment = getPaymentById(id);
        payment.setStatus(PaymentStatus.CANCELADO);

        Payment saved = paymentRepository.save(payment);
        log.info("Pago cancelado: {}", id);

        return saved;
    }

    @Override
    @Transactional
    public Payment refundPayment(Long id) {
        Payment payment = getPaymentById(id);
        payment.setStatus(PaymentStatus.REEMBOLSADO);

        Payment saved = paymentRepository.save(payment);
        log.info("Pago reembolsado: {}", id);

        kafkaTemplate.send("payment-events", "payment-refunded", saved);

        return saved;
    }

    @Override
    public List<Payment> getPaymentsByStudent(Long studentId) {
        return paymentRepository.findByStudentId(studentId);
    }

    @Override
    public List<Payment> getPaymentsByCourse(Long courseId) {
        return paymentRepository.findByCourseId(courseId);
    }

    @Override
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    @Override
    public List<Payment> getPaymentsByType(PaymentType type) {
        return paymentRepository.findByPaymentType(type);
    }

    @Override
    public List<Payment> getPaymentsByMethod(PaymentMethod method) {
        return paymentRepository.findByPaymentMethod(method);
    }

    @Override
    public List<Payment> getPaymentsByDate(LocalDate date) {
        return paymentRepository.findByDate(date);
    }

    @Override
    public List<Payment> getPendingPaymentsByStudent(Long studentId) {
        return paymentRepository.findByStudentIdAndStatus(studentId, PaymentStatus.PENDIENTE);
    }

    @Override
    public BigDecimal getTotalByDateAndMethod(LocalDate date, PaymentMethod method) {
        BigDecimal total = paymentRepository.sumByDateAndMethod(date, method);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalByDate(LocalDate date) {
        BigDecimal total = paymentRepository.sumByDate(date);
        return total != null ? total : BigDecimal.ZERO;
    }

    private String generateReceiptNumber() {
        return "EVS-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

