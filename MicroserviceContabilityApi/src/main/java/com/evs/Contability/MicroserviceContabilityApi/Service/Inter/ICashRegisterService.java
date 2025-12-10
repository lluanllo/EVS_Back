package com.evs.Contability.MicroserviceContabilityApi.Service.Inter;

import com.evs.Contability.MicroserviceContabilityApi.DTO.CashRegisterDTO;
import com.evs.Contability.MicroserviceContabilityApi.DTO.CashDiscrepancyReportDTO;
import com.evs.Contability.MicroserviceContabilityApi.Entities.CashRegister;

import java.time.LocalDate;
import java.util.List;

public interface ICashRegisterService {

    CashRegister openCashRegister(LocalDate date);

    CashRegister closeCashRegister(CashRegisterDTO dto, Long closedBy);

    CashRegister getCashRegisterById(Long id);

    CashRegister getCashRegisterByDate(LocalDate date);

    List<CashRegister> getAllCashRegisters();

    List<CashRegister> getCashRegistersByDateRange(LocalDate start, LocalDate end);

    List<CashRegister> getDiscrepancies();

    CashRegister markAsReviewed(Long id, String notes);

    CashDiscrepancyReportDTO generateDiscrepancyReport(LocalDate date);

    CashRegister calculateExpectedCash(LocalDate date);
}

