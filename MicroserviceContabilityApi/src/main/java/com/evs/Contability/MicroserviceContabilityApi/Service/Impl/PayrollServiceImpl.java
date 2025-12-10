package com.evs.Contability.MicroserviceContabilityApi.Service.Impl;

import com.evs.Contability.MicroserviceContabilityApi.DTO.PayrollDTO;
import com.evs.Contability.MicroserviceContabilityApi.DTO.PayrollSummaryDTO;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Payroll;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Salary;
import com.evs.Contability.MicroserviceContabilityApi.Entities.WorkedHours;
import com.evs.Contability.MicroserviceContabilityApi.Repository.PayrollRepository;
import com.evs.Contability.MicroserviceContabilityApi.Service.Inter.IPayrollService;
import com.evs.Contability.MicroserviceContabilityApi.Service.Inter.ISalaryService;
import com.evs.Contability.MicroserviceContabilityApi.Service.Inter.IWorkedHoursService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements IPayrollService {

    private final PayrollRepository payrollRepository;
    private final ISalaryService salaryService;
    private final IWorkedHoursService workedHoursService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public Payroll generatePayroll(Long teacherId, Integer month, Integer year) {
        // Verificar si ya existe
        payrollRepository.findByTeacherIdAndMonthAndYear(teacherId, month, year)
                .ifPresent(p -> {
                    throw new RuntimeException("Ya existe una nómina para este profesor en " + month + "/" + year);
                });

        return generatePayrollAuto(teacherId, month, year);
    }

    @Override
    @Transactional
    public Payroll generatePayrollAuto(Long teacherId, Integer month, Integer year) {
        // Obtener configuración salarial
        Salary salary = salaryService.getSalaryByTeacherId(teacherId);

        // Obtener total de minutos trabajados y validados
        Integer totalMinutes = workedHoursService.getTotalMinutesByTeacherAndMonth(teacherId, month, year);
        BigDecimal totalHours = new BigDecimal(totalMinutes).divide(new BigDecimal("60"), 2, RoundingMode.HALF_UP);

        // Calcular salario bruto
        BigDecimal grossSalary = totalHours.multiply(salary.getHourlyRate());

        // Añadir bonuses
        BigDecimal bonuses = salary.getMonthlyBonus().add(salary.getSpecialityBonus());
        grossSalary = grossSalary.add(bonuses);

        // Calcular deducciones
        BigDecimal irpfDeduction = grossSalary.multiply(salary.getIrpfPercentage())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal ssDeduction = grossSalary.multiply(salary.getSsPercentage())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        // Salario neto
        BigDecimal netSalary = grossSalary.subtract(irpfDeduction).subtract(ssDeduction);

        // Obtener IDs de las horas trabajadas
        List<WorkedHours> workedHours = workedHoursService.getUnpaidValidatedByTeacher(teacherId);
        List<Long> workedHoursIds = workedHours.stream()
                .map(WorkedHours::getId)
                .collect(Collectors.toList());

        Payroll payroll = Payroll.builder()
                .teacherId(teacherId)
                .month(month)
                .year(year)
                .totalHours(totalHours)
                .grossSalary(grossSalary)
                .irpfDeduction(irpfDeduction)
                .ssDeduction(ssDeduction)
                .bonuses(bonuses)
                .netSalary(netSalary)
                .status("PENDIENTE")
                .generatedAt(LocalDateTime.now())
                .workedHoursIds(workedHoursIds)
                .build();

        Payroll saved = payrollRepository.save(payroll);
        log.info("Nómina generada: profesor {} - {} horas - {} € neto", teacherId, totalHours, netSalary);

        // Notificar
        kafkaTemplate.send("payroll-events", "payroll-generated", saved);

        return saved;
    }

    @Override
    public Payroll getPayrollById(Long id) {
        return payrollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nómina no encontrada: " + id));
    }

    @Override
    public List<Payroll> getAllPayrolls() {
        return payrollRepository.findAll();
    }

    @Override
    public List<Payroll> getPayrollsByTeacher(Long teacherId) {
        return payrollRepository.findByTeacherIdOrderByDateDesc(teacherId);
    }

    @Override
    public Payroll getPayrollByTeacherAndMonth(Long teacherId, Integer month, Integer year) {
        return payrollRepository.findByTeacherIdAndMonthAndYear(teacherId, month, year)
                .orElseThrow(() -> new RuntimeException("No existe nómina para el profesor " + teacherId + " en " + month + "/" + year));
    }

    @Override
    public List<Payroll> getPayrollsByMonth(Integer month, Integer year) {
        return payrollRepository.findByMonthAndYear(month, year);
    }

    @Override
    public List<Payroll> getPendingPayrolls() {
        return payrollRepository.findPendingPayrolls();
    }

    @Override
    public List<Payroll> getApprovedUnpaidPayrolls() {
        return payrollRepository.findApprovedUnpaidPayrolls();
    }

    @Override
    @Transactional
    public Payroll approvePayroll(Long id, Long approvedBy) {
        Payroll payroll = getPayrollById(id);
        payroll.setStatus("APROBADO");
        payroll.setApprovedBy(approvedBy);

        Payroll saved = payrollRepository.save(payroll);
        log.info("Nómina aprobada: {} por usuario {}", id, approvedBy);

        kafkaTemplate.send("payroll-events", "payroll-approved", saved);

        return saved;
    }

    @Override
    @Transactional
    public Payroll markAsPaid(Long id) {
        Payroll payroll = getPayrollById(id);
        payroll.setStatus("PAGADO");
        payroll.setPaidAt(LocalDateTime.now());

        // Marcar las horas como pagadas
        workedHoursService.markAsPaid(payroll.getWorkedHoursIds(), id);

        Payroll saved = payrollRepository.save(payroll);
        log.info("Nómina marcada como pagada: {}", id);

        kafkaTemplate.send("payroll-events", "payroll-paid", saved);

        return saved;
    }

    @Override
    @Transactional
    public void deletePayroll(Long id) {
        Payroll payroll = getPayrollById(id);
        if (!"PENDIENTE".equals(payroll.getStatus())) {
            throw new RuntimeException("Solo se pueden eliminar nóminas pendientes");
        }
        payrollRepository.deleteById(id);
        log.info("Nómina eliminada: {}", id);
    }

    @Override
    public PayrollSummaryDTO getPayrollSummary(Integer month, Integer year) {
        List<Payroll> payrolls = getPayrollsByMonth(month, year);

        BigDecimal totalHours = payrolls.stream()
                .map(Payroll::getTotalHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGross = payrolls.stream()
                .map(Payroll::getGrossSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDeductions = payrolls.stream()
                .map(p -> p.getIrpfDeduction().add(p.getSsDeduction()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalNet = payrolls.stream()
                .map(Payroll::getNetSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pending = payrolls.stream().filter(p -> "PENDIENTE".equals(p.getStatus())).count();
        long approved = payrolls.stream().filter(p -> "APROBADO".equals(p.getStatus())).count();
        long paid = payrolls.stream().filter(p -> "PAGADO".equals(p.getStatus())).count();

        List<PayrollSummaryDTO.TeacherPayrollSummary> summaries = payrolls.stream()
                .map(p -> PayrollSummaryDTO.TeacherPayrollSummary.builder()
                        .teacherId(p.getTeacherId())
                        .hours(p.getTotalHours())
                        .netSalary(p.getNetSalary())
                        .status(p.getStatus())
                        .build())
                .collect(Collectors.toList());

        return PayrollSummaryDTO.builder()
                .month(month)
                .year(year)
                .totalTeachers(payrolls.size())
                .totalHours(totalHours)
                .totalGrossSalary(totalGross)
                .totalDeductions(totalDeductions)
                .totalNetSalary(totalNet)
                .pendingCount((int) pending)
                .approvedCount((int) approved)
                .paidCount((int) paid)
                .teacherSummaries(summaries)
                .build();
    }
}

