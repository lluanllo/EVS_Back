package com.empresa.students.MicroserviceStudentsAPI.Service.Inter;

import java.util.List;
import java.util.Map;

/**
 * Interfaz para consulta de historial de estudiantes (Single Responsibility)
 */
public interface IStudentHistoryService {

    /**
     * Obtiene el historial de clases de un estudiante
     */
    List<Map<String, Object>> getClassHistory(Long studentId);

    /**
     * Obtiene los ejercicios realizados por un estudiante
     */
    List<Map<String, Object>> getExercisesCompleted(Long studentId);

    /**
     * Obtiene estadísticas del estudiante
     */
    Map<String, Object> getStudentStats(Long studentId);

    /**
     * Obtiene el número total de clases asistidas
     */
    int getTotalClassesAttended(Long studentId);

    /**
     * Añade una clase al historial del estudiante
     */
    void addClassToHistory(Long studentId, Long courseId);
}

