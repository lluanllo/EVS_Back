package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service.Inter;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Schedule;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz para la gestión de horarios (Single Responsibility)
 */
public interface IScheduleService {

    Schedule create(Schedule schedule);

    Schedule update(Long id, Schedule schedule);

    Schedule findById(Long id);

    List<Schedule> findAll();

    List<Schedule> findByTeacher(Long teacherId);

    List<Schedule> findByDate(LocalDate date);

    List<Schedule> findByTeacherAndDateRange(Long teacherId, LocalDate startDate, LocalDate endDate);

    void delete(Long id);

    Schedule confirm(Long scheduleId, Long teacherId);

    Schedule reject(Long scheduleId, Long teacherId, String reason);

    void reassignIfRejected(Long scheduleId);
}

