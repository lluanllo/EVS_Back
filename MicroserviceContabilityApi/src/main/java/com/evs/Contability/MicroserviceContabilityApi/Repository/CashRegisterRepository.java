package com.evs.Contability.MicroserviceContabilityApi.Repository;

import com.evs.Contability.MicroserviceContabilityApi.Entities.CashRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CashRegisterRepository extends JpaRepository<CashRegister, Long> {

    Optional<CashRegister> findByDate(LocalDate date);

    List<CashRegister> findByHasDiscrepancy(Boolean hasDiscrepancy);

    List<CashRegister> findByStatus(String status);

    @Query("SELECT cr FROM CashRegister cr WHERE cr.date BETWEEN :start AND :end ORDER BY cr.date DESC")
    List<CashRegister> findByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT cr FROM CashRegister cr WHERE cr.hasDiscrepancy = true AND cr.status != 'REVISADO' ORDER BY cr.date DESC")
    List<CashRegister> findPendingDiscrepancies();

    boolean existsByDate(LocalDate date);
}

