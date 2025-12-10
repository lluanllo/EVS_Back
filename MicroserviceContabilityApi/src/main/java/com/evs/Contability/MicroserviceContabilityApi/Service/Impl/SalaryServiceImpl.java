package com.evs.Contability.MicroserviceContabilityApi.Service.Impl;

import com.evs.Contability.MicroserviceContabilityApi.DTO.SalaryDTO;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Salary;
import com.evs.Contability.MicroserviceContabilityApi.Repository.SalaryRepository;
import com.evs.Contability.MicroserviceContabilityApi.Service.Inter.ISalaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements ISalaryService {

    private final SalaryRepository salaryRepository;

    @Override
    @Transactional
    public Salary createSalary(SalaryDTO dto) {
        if (salaryRepository.existsByTeacherId(dto.getTeacherId())) {
            throw new RuntimeException("Ya existe una configuración salarial para el profesor: " + dto.getTeacherId());
        }

        Salary salary = Salary.builder()
                .teacherId(dto.getTeacherId())
                .hourlyRate(dto.getHourlyRate())
                .contractType(dto.getContractType())
                .monthlyBonus(dto.getMonthlyBonus() != null ? dto.getMonthlyBonus() : BigDecimal.ZERO)
                .specialityBonus(dto.getSpecialityBonus() != null ? dto.getSpecialityBonus() : BigDecimal.ZERO)
                .irpfPercentage(dto.getIrpfPercentage() != null ? dto.getIrpfPercentage() : new BigDecimal("15"))
                .ssPercentage(dto.getSsPercentage() != null ? dto.getSsPercentage() : new BigDecimal("6.35"))
                .bankAccount(dto.getBankAccount())
                .active(true)
                .build();

        Salary saved = salaryRepository.save(salary);
        log.info("Configuración salarial creada para profesor: {}", dto.getTeacherId());

        return saved;
    }

    @Override
    @Transactional
    public Salary updateSalary(Long id, SalaryDTO dto) {
        Salary salary = getSalaryById(id);

        salary.setHourlyRate(dto.getHourlyRate());
        salary.setContractType(dto.getContractType());
        salary.setMonthlyBonus(dto.getMonthlyBonus());
        salary.setSpecialityBonus(dto.getSpecialityBonus());
        salary.setIrpfPercentage(dto.getIrpfPercentage());
        salary.setSsPercentage(dto.getSsPercentage());
        salary.setBankAccount(dto.getBankAccount());

        Salary saved = salaryRepository.save(salary);
        log.info("Configuración salarial actualizada: {}", id);

        return saved;
    }

    @Override
    public Salary getSalaryById(Long id) {
        return salaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuración salarial no encontrada: " + id));
    }

    @Override
    public Salary getSalaryByTeacherId(Long teacherId) {
        return salaryRepository.findByTeacherId(teacherId)
                .orElseThrow(() -> new RuntimeException("No hay configuración salarial para el profesor: " + teacherId));
    }

    @Override
    public List<Salary> getAllSalaries() {
        return salaryRepository.findAll();
    }

    @Override
    public List<Salary> getActiveSalaries() {
        return salaryRepository.findByActive(true);
    }

    @Override
    public List<Salary> getSalariesByContractType(String contractType) {
        return salaryRepository.findByContractType(contractType);
    }

    @Override
    @Transactional
    public void deleteSalary(Long id) {
        if (!salaryRepository.existsById(id)) {
            throw new RuntimeException("Configuración salarial no encontrada: " + id);
        }
        salaryRepository.deleteById(id);
        log.info("Configuración salarial eliminada: {}", id);
    }

    @Override
    @Transactional
    public void deactivateSalary(Long teacherId) {
        Salary salary = getSalaryByTeacherId(teacherId);
        salary.setActive(false);
        salaryRepository.save(salary);
        log.info("Configuración salarial desactivada para profesor: {}", teacherId);
    }
}

