package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service.Inter;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Teacher;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.Specialty;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Interfaz para el algoritmo de asignación de profesores (Single Responsibility)
 */
public interface ITeacherAssignmentService {

    /**
     * Obtiene profesores disponibles para una especialidad ordenados por prioridad
     */
    List<Teacher> getAvailableTeachersByPriority(Specialty specialty);

    /**
     * Asigna un profesor a un curso respetando prioridades y distribución equitativa
     */
    Teacher assignTeacherToCourse(Long courseId, Specialty specialty, LocalDate date);

    /**
     * Obtiene las horas trabajadas por profesor en un rango de fechas
     */
    Map<Long, Integer> getHoursWorkedByTeacher(LocalDate startDate, LocalDate endDate);

    /**
     * Balancea la distribución de horas entre profesores
     */
    void balanceWorkload(LocalDate startDate, LocalDate endDate);

    /**
     * Reasigna curso cuando un profesor no está disponible
     */
    Teacher reassignCourse(Long courseId, Long excludeTeacherId);
}
