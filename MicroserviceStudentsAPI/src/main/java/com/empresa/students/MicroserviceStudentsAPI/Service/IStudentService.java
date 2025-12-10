package com.empresa.students.MicroserviceStudentsAPI.Service;

import com.empresa.students.MicroserviceStudentsAPI.Entities.Student;

import java.util.List;
import java.util.Map;

public interface IStudentService {
    List<Student> findAll();
    Student findById(Long id);
    Student save(Student student);
    Student updateById(Long id, Student student);
    void delete(Long id);
    List<Student> findByIdCourse(Long idCourse);

    // Nuevas funcionalidades para alumnos

    /**
     * Obtiene el historial de clases de un estudiante
     */
    List<Map<String, Object>> getClassHistory(Long studentId);

    /**
     * Obtiene los ejercicios realizados por un estudiante
     */
    List<Map<String, Object>> getExercisesCompleted(Long studentId);

    /**
     * Obtiene la predicción del viento actual
     */
    Map<String, Object> getWeatherPrediction();

    /**
     * Obtiene estadísticas del estudiante
     */
    Map<String, Object> getStudentStats(Long studentId);

    /**
     * Añade un curso al historial del estudiante
     */
    Student addCourseToHistory(Long studentId, Long courseId);

    /**
     * Obtiene el número total de clases asistidas
     */
    int getTotalClassesAttended(Long studentId);
}
