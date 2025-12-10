package com.evs.Contability.MicroserviceContabilityApi.Service.Inter;

import com.evs.Contability.MicroserviceContabilityApi.DTO.PaymentDTO;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Payment;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Enums.PaymentMethod;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Enums.PaymentStatus;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IPaymentService {

    Payment createPayment(PaymentDTO paymentDTO);

    Payment updatePayment(Long id, PaymentDTO paymentDTO);

    Payment getPaymentById(Long id);

    List<Payment> getAllPayments();

    void deletePayment(Long id);

    Payment confirmPayment(Long id, Long confirmedBy);

    Payment cancelPayment(Long id);

    Payment refundPayment(Long id);

    List<Payment> getPaymentsByStudent(Long studentId);

    List<Payment> getPaymentsByCourse(Long courseId);

    List<Payment> getPaymentsByStatus(PaymentStatus status);

    List<Payment> getPaymentsByType(PaymentType type);

    List<Payment> getPaymentsByMethod(PaymentMethod method);

    List<Payment> getPaymentsByDate(LocalDate date);

    List<Payment> getPendingPaymentsByStudent(Long studentId);

    BigDecimal getTotalByDateAndMethod(LocalDate date, PaymentMethod method);

    BigDecimal getTotalByDate(LocalDate date);
}

