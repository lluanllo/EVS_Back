package com.evs.Contability.MicroserviceContabilityApi.Service.Impl;

import com.evs.Contability.MicroserviceContabilityApi.DTO.CashDiscrepancyReportDTO;
import com.evs.Contability.MicroserviceContabilityApi.DTO.CashRegisterDTO;
import com.evs.Contability.MicroserviceContabilityApi.Entities.CashRegister;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Enums.PaymentMethod;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Payment;
import com.evs.Contability.MicroserviceContabilityApi.Repository.CashRegisterRepository;
import com.evs.Contability.MicroserviceContabilityApi.Repository.PaymentRepository;
import com.evs.Contability.MicroserviceContabilityApi.Service.Inter.ICashRegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashRegisterServiceImpl implements ICashRegisterService {

    private final CashRegisterRepository cashRegisterRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public CashRegister openCashRegister(LocalDate date) {
        if (cashRegisterRepository.existsByDate(date)) {
            throw new RuntimeException("Ya existe un registro de caja para la fecha: " + date);
        }

        CashRegister cashRegister = CashRegister.builder()
                .date(date)
                .expectedCash(BigDecimal.ZERO)
                .actualCash(BigDecimal.ZERO)
                .totalTransfers(BigDecimal.ZERO)
                .totalCard(BigDecimal.ZERO)
                .totalBizum(BigDecimal.ZERO)
                .status("ABIERTO")
                .build();

        CashRegister saved = cashRegisterRepository.save(cashRegister);
        log.info("Caja abierta para el día: {}", date);

        return saved;
    }

    @Override
    @Transactional
    public CashRegister closeCashRegister(CashRegisterDTO dto, Long closedBy) {
        CashRegister cashRegister = cashRegisterRepository.findByDate(dto.getDate())
                .orElseGet(() -> openCashRegister(dto.getDate()));

        // Calcular efectivo esperado de los pagos del día
        BigDecimal expectedCash = paymentRepository.sumByDateAndMethod(dto.getDate(), PaymentMethod.EFECTIVO);
        expectedCash = expectedCash != null ? expectedCash : BigDecimal.ZERO;

        BigDecimal expectedTransfers = paymentRepository.sumByDateAndMethod(dto.getDate(), PaymentMethod.TRANSFERENCIA);
        BigDecimal expectedCard = paymentRepository.sumByDateAndMethod(dto.getDate(), PaymentMethod.TARJETA);
        BigDecimal expectedBizum = paymentRepository.sumByDateAndMethod(dto.getDate(), PaymentMethod.BIZUM);

        cashRegister.setExpectedCash(expectedCash);
        cashRegister.setActualCash(dto.getActualCash());
        cashRegister.setTotalTransfers(dto.getTotalTransfers() != null ? dto.getTotalTransfers() :
                (expectedTransfers != null ? expectedTransfers : BigDecimal.ZERO));
        cashRegister.setTotalCard(dto.getTotalCard() != null ? dto.getTotalCard() :
                (expectedCard != null ? expectedCard : BigDecimal.ZERO));
        cashRegister.setTotalBizum(dto.getTotalBizum() != null ? dto.getTotalBizum() :
                (expectedBizum != null ? expectedBizum : BigDecimal.ZERO));
        cashRegister.setDiscrepancyNotes(dto.getDiscrepancyNotes());
        cashRegister.setClosedBy(closedBy);
        cashRegister.setClosedAt(LocalDateTime.now());
        cashRegister.setStatus("CERRADO");

        CashRegister saved = cashRegisterRepository.save(cashRegister);
        log.info("Caja cerrada para el día: {} - Diferencia: {}", dto.getDate(), saved.getDifference());

        if (saved.getHasDiscrepancy()) {
            log.warn("¡DESCUADRE DETECTADO! Fecha: {} - Diferencia: {}", dto.getDate(), saved.getDifference());
        }

        return saved;
    }

    @Override
    public CashRegister getCashRegisterById(Long id) {
        return cashRegisterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de caja no encontrado: " + id));
    }

    @Override
    public CashRegister getCashRegisterByDate(LocalDate date) {
        return cashRegisterRepository.findByDate(date)
                .orElseThrow(() -> new RuntimeException("No hay registro de caja para la fecha: " + date));
    }

    @Override
    public List<CashRegister> getAllCashRegisters() {
        return cashRegisterRepository.findAll();
    }

    @Override
    public List<CashRegister> getCashRegistersByDateRange(LocalDate start, LocalDate end) {
        return cashRegisterRepository.findByDateRange(start, end);
    }

    @Override
    public List<CashRegister> getDiscrepancies() {
        return cashRegisterRepository.findPendingDiscrepancies();
    }

    @Override
    @Transactional
    public CashRegister markAsReviewed(Long id, String notes) {
        CashRegister cashRegister = getCashRegisterById(id);
        cashRegister.setStatus("REVISADO");
        cashRegister.setDiscrepancyNotes(cashRegister.getDiscrepancyNotes() + "\n[REVISIÓN]: " + notes);

        CashRegister saved = cashRegisterRepository.save(cashRegister);
        log.info("Registro de caja marcado como revisado: {}", id);

        return saved;
    }

    @Override
    public CashDiscrepancyReportDTO generateDiscrepancyReport(LocalDate date) {
        CashRegister cashRegister = getCashRegisterByDate(date);
        List<Payment> payments = paymentRepository.findByDate(date);

        List<CashDiscrepancyReportDTO.PaymentDetailDTO> paymentDetails = payments.stream()
                .map(p -> CashDiscrepancyReportDTO.PaymentDetailDTO.builder()
                        .paymentId(p.getId())
                        .description(p.getDescription())
                        .amount(p.getAmount())
                        .paymentMethod(p.getPaymentMethod().name())
                        .status(p.getStatus().name())
                        .build())
                .collect(Collectors.toList());

        // Análisis de posibles razones del descuadre
        List<String> possibleReasons = new ArrayList<>();
        if (cashRegister.getDifference().compareTo(BigDecimal.ZERO) < 0) {
            possibleReasons.add("Falta efectivo - posible pago no registrado o error al dar cambio");
            possibleReasons.add("Verificar si hubo devoluciones no registradas");
        } else if (cashRegister.getDifference().compareTo(BigDecimal.ZERO) > 0) {
            possibleReasons.add("Sobra efectivo - posible cobro no registrado en el sistema");
            possibleReasons.add("Verificar si hubo propinas o aportaciones extras");
        }

        return CashDiscrepancyReportDTO.builder()
                .date(date)
                .expectedCash(cashRegister.getExpectedCash())
                .actualCash(cashRegister.getActualCash())
                .difference(cashRegister.getDifference())
                .expectedPayments(paymentDetails)
                .totalEfectivo(getTotalByMethod(payments, PaymentMethod.EFECTIVO))
                .totalTransferencia(getTotalByMethod(payments, PaymentMethod.TRANSFERENCIA))
                .totalTarjeta(getTotalByMethod(payments, PaymentMethod.TARJETA))
                .totalBizum(getTotalByMethod(payments, PaymentMethod.BIZUM))
                .possibleReasons(possibleReasons)
                .build();
    }

    @Override
    @Transactional
    public CashRegister calculateExpectedCash(LocalDate date) {
        CashRegister cashRegister = cashRegisterRepository.findByDate(date)
                .orElseGet(() -> openCashRegister(date));

        BigDecimal expectedCash = paymentRepository.sumByDateAndMethod(date, PaymentMethod.EFECTIVO);
        cashRegister.setExpectedCash(expectedCash != null ? expectedCash : BigDecimal.ZERO);

        return cashRegisterRepository.save(cashRegister);
    }

    private BigDecimal getTotalByMethod(List<Payment> payments, PaymentMethod method) {
        return payments.stream()
                .filter(p -> p.getPaymentMethod() == method)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

