package com.evs.Contability.MicroserviceContabilityApi.Repository;

import com.evs.Contability.MicroserviceContabilityApi.Entities.Payment;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Enums.PaymentMethod;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Enums.PaymentStatus;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Enums.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByStudentId(Long studentId);

    List<Payment> findByCourseId(Long courseId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByPaymentType(PaymentType paymentType);

    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :start AND :end")
    List<Payment> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p FROM Payment p WHERE DATE(p.paymentDate) = :date")
    List<Payment> findByDate(@Param("date") LocalDate date);

    @Query("SELECT p FROM Payment p WHERE DATE(p.paymentDate) = :date AND p.paymentMethod = :method")
    List<Payment> findByDateAndMethod(@Param("date") LocalDate date, @Param("method") PaymentMethod method);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE DATE(p.paymentDate) = :date AND p.paymentMethod = :method AND p.status = 'CONFIRMADO'")
    BigDecimal sumByDateAndMethod(@Param("date") LocalDate date, @Param("method") PaymentMethod method);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE DATE(p.paymentDate) = :date AND p.status = 'CONFIRMADO'")
    BigDecimal sumByDate(@Param("date") LocalDate date);

    List<Payment> findByStudentIdAndStatus(Long studentId, PaymentStatus status);
}

