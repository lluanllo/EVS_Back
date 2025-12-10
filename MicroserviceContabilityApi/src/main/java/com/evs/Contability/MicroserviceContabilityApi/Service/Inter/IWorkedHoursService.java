package com.evs.Contability.MicroserviceContabilityApi.Service.Inter;

import com.evs.Contability.MicroserviceContabilityApi.DTO.WorkedHoursDTO;
import com.evs.Contability.MicroserviceContabilityApi.Entities.WorkedHours;

import java.time.LocalDate;
import java.util.List;

public interface IWorkedHoursService {

    WorkedHours registerHours(WorkedHoursDTO dto);

    WorkedHours updateHours(Long id, WorkedHoursDTO dto);

    WorkedHours getById(Long id);

    List<WorkedHours> getAllWorkedHours();

    void deleteWorkedHours(Long id);

    List<WorkedHours> getByTeacherId(Long teacherId);

    List<WorkedHours> getByTeacherIdAndDateRange(Long teacherId, LocalDate start, LocalDate end);

    List<WorkedHours> getPendingValidation();

    WorkedHours validateHours(Long id, Long validatedBy);

    void validateMultiple(List<Long> ids, Long validatedBy);

    Integer getTotalMinutesByTeacherAndMonth(Long teacherId, Integer month, Integer year);

    List<WorkedHours> getUnpaidValidatedByTeacher(Long teacherId);

    void markAsPaid(List<Long> ids, Long payrollId);
}

