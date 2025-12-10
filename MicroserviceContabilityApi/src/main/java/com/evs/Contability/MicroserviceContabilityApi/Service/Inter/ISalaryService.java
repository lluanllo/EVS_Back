package com.evs.Contability.MicroserviceContabilityApi.Service.Inter;

import com.evs.Contability.MicroserviceContabilityApi.DTO.SalaryDTO;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Salary;

import java.util.List;

public interface ISalaryService {

    Salary createSalary(SalaryDTO salaryDTO);

    Salary updateSalary(Long id, SalaryDTO salaryDTO);

    Salary getSalaryById(Long id);

    Salary getSalaryByTeacherId(Long teacherId);

    List<Salary> getAllSalaries();

    List<Salary> getActiveSalaries();

    List<Salary> getSalariesByContractType(String contractType);

    void deleteSalary(Long id);

    void deactivateSalary(Long teacherId);
}

