package com.evs.Contability.MicroserviceContabilityApi.Repository;

import com.evs.Contability.MicroserviceContabilityApi.Entities.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    List<Payroll> findByTeacherId(Long teacherId);

    Optional<Payroll> findByTeacherIdAndMonthAndYear(Long teacherId, Integer month, Integer year);

    List<Payroll> findByMonthAndYear(Integer month, Integer year);

    List<Payroll> findByStatus(String status);

    @Query("SELECT p FROM Payroll p WHERE p.status = 'PENDIENTE' ORDER BY p.year DESC, p.month DESC")
    List<Payroll> findPendingPayrolls();

    @Query("SELECT p FROM Payroll p WHERE p.status = 'APROBADO' AND p.paidAt IS NULL ORDER BY p.year DESC, p.month DESC")
    List<Payroll> findApprovedUnpaidPayrolls();

    @Query("SELECT p FROM Payroll p WHERE p.teacherId = :teacherId ORDER BY p.year DESC, p.month DESC")
    List<Payroll> findByTeacherIdOrderByDateDesc(@Param("teacherId") Long teacherId);
}

