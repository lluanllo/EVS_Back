package com.empresa.students.MicroserviceStudentsAPI.Service;

import com.empresa.students.MicroserviceStudentsAPI.Entities.Student;
import com.empresa.students.MicroserviceStudentsAPI.Repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class StudentsServiceImpl implements IStudentService {

    @Autowired
    private StudentRepository studentsRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Student> findAll() {
        return (List<Student>) studentsRepository.findAll();
    }

    @Override
    public Student findById(Long id) {
        return studentsRepository.findById(id).orElseThrow();
    }

    @Override
    public Student save(Student student) {
        return studentsRepository.save(student);
    }

    @Override
    public Student updateById(Long id, Student student) {
        return studentsRepository.save(student);
    }

    @Override
    public void delete(Long id) {
        studentsRepository.deleteById(id);
    }

    @Override
    public List<Student> findByIdCourse(Long idCourse) {
        return studentsRepository.findByIdCourse(idCourse);
    }

    // === Nuevas funcionalidades para alumnos ===

    @Override
    public List<Map<String, Object>> getClassHistory(Long studentId) {
        Student student = findById(studentId);
        List<Map<String, Object>> history = new ArrayList<>();

        // Por cada curso del estudiante, obtener información del curso
        for (Long courseId : student.getCourseIds()) {
            try {
                // Consultar al microservicio de cursos via Kafka o REST
                Map<String, Object> classInfo = new HashMap<>();
                classInfo.put("courseId", courseId);
                classInfo.put("studentId", studentId);
                classInfo.put("enrolledDate", LocalDateTime.now()); // TODO: Obtener fecha real
                history.add(classInfo);
            } catch (Exception e) {
                log.warn("No se pudo obtener info del curso {}: {}", courseId, e.getMessage());
            }
        }

        log.info("Historial de clases obtenido para estudiante {}: {} cursos", studentId, history.size());
        return history;
    }

    @Override
    public List<Map<String, Object>> getExercisesCompleted(Long studentId) {
        // Ejercicios típicos según nivel
        Student student = findById(studentId);
        List<Map<String, Object>> exercises = new ArrayList<>();

        String level = student.getSkillLevel() != null ? student.getSkillLevel().name() : "BEGINNER";

        switch (level.toUpperCase()) {
            case "BEGINNER" -> {
                exercises.add(Map.of("name", "Posición básica", "completed", true, "date", LocalDateTime.now().minusDays(7)));
                exercises.add(Map.of("name", "Manejo del timón", "completed", true, "date", LocalDateTime.now().minusDays(5)));
                exercises.add(Map.of("name", "Orzar y arribar", "completed", false, "date", "Pendiente"));
            }
            case "INTERMEDIATE" -> {
                exercises.add(Map.of("name", "Virada por avante", "completed", true, "date", LocalDateTime.now().minusDays(14)));
                exercises.add(Map.of("name", "Trasluchada", "completed", true, "date", LocalDateTime.now().minusDays(10)));
                exercises.add(Map.of("name", "Navegación en ceñida", "completed", false, "date", "Pendiente"));
            }
            case "ADVANCED" -> {
                exercises.add(Map.of("name", "Navegación en planeo", "completed", true, "date", LocalDateTime.now().minusDays(3)));
                exercises.add(Map.of("name", "Uso del arnés", "completed", true, "date", LocalDateTime.now().minusDays(2)));
                exercises.add(Map.of("name", "Water start", "completed", false, "date", "En progreso"));
            }
        }

        return exercises;
    }

    @Override
    public Map<String, Object> getWeatherPrediction() {
        try {
            // Llamar al microservicio de cursos para obtener predicción
            // En producción usar Eureka discovery
            String weatherUrl = "http://localhost:8082/api/weather/prediction";
            return restTemplate.getForObject(weatherUrl, Map.class);
        } catch (Exception e) {
            log.warn("No se pudo obtener predicción del viento: {}", e.getMessage());
            return Map.of(
                    "windTrend", "UNKNOWN",
                    "recommendation", "No hay datos disponibles",
                    "suitableForBeginners", false,
                    "suitableForAdvanced", false,
                    "message", "Servicio de meteorología no disponible"
            );
        }
    }

    @Override
    public Map<String, Object> getStudentStats(Long studentId) {
        Student student = findById(studentId);

        int totalCourses = student.getCourseIds() != null ? student.getCourseIds().size() : 0;

        return Map.of(
                "studentId", studentId,
                "name", student.getName() + " " + (student.getLastName() != null ? student.getLastName() : ""),
                "skillLevel", student.getSkillLevel() != null ? student.getSkillLevel().name() : "BEGINNER",
                "totalCourses", totalCourses,
                "isMember", student.getSocio() != null ? student.getSocio() : false,
                "memberSince", student.getBirthDate() // TODO: Añadir fecha de alta
        );
    }

    @Override
    public Student addCourseToHistory(Long studentId, Long courseId) {
        Student student = findById(studentId);

        if (student.getCourseIds() == null) {
            student.setCourseIds(new HashSet<Long>());
        }

        student.getCourseIds().add(courseId);

        Student saved = studentsRepository.save(student);
        log.info("Curso {} añadido al historial del estudiante {}", courseId, studentId);

        return saved;
    }

    @Override
    public int getTotalClassesAttended(Long studentId) {
        Student student = findById(studentId);
        return student.getCourseIds() != null ? student.getCourseIds().size() : 0;
    }
}
