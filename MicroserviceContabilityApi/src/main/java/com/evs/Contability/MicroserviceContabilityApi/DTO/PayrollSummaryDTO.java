package com.evs.Contability.MicroserviceContabilityApi.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayrollSummaryDTO {

    private Integer month;
    private Integer year;
    private Integer totalTeachers;
    private BigDecimal totalHours;
    private BigDecimal totalGrossSalary;
    private BigDecimal totalDeductions;
    private BigDecimal totalNetSalary;
    private Integer pendingCount;
    private Integer approvedCount;
    private Integer paidCount;
    private List<TeacherPayrollSummary> teacherSummaries;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeacherPayrollSummary {
        private Long teacherId;
        private String teacherName;
        private BigDecimal hours;
        private BigDecimal netSalary;
        private String status;
    }
}

