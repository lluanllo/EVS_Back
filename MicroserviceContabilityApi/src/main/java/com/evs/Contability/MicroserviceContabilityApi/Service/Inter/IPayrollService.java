package com.evs.Contability.MicroserviceContabilityApi.Service.Inter;

import com.evs.Contability.MicroserviceContabilityApi.DTO.PayrollDTO;
import com.evs.Contability.MicroserviceContabilityApi.DTO.PayrollSummaryDTO;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Payroll;

import java.util.List;

public interface IPayrollService {

    Payroll generatePayroll(Long teacherId, Integer month, Integer year);

    Payroll generatePayrollAuto(Long teacherId, Integer month, Integer year);

    Payroll getPayrollById(Long id);

    List<Payroll> getAllPayrolls();

    List<Payroll> getPayrollsByTeacher(Long teacherId);

    Payroll getPayrollByTeacherAndMonth(Long teacherId, Integer month, Integer year);

    List<Payroll> getPayrollsByMonth(Integer month, Integer year);

    List<Payroll> getPendingPayrolls();

    List<Payroll> getApprovedUnpaidPayrolls();

    Payroll approvePayroll(Long id, Long approvedBy);

    Payroll markAsPaid(Long id);

    void deletePayroll(Long id);

    PayrollSummaryDTO getPayrollSummary(Integer month, Integer year);
}

