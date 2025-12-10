package com.empresa.students.MicroserviceStudentsAPI.Service;

import com.empresa.students.MicroserviceStudentsAPI.Controller.DTO.StudentRequest;
import com.empresa.students.MicroserviceStudentsAPI.Controller.DTO.StudentResponse;
import com.empresa.students.MicroserviceStudentsAPI.Entities.Enums.SkillLevel;
import com.empresa.students.MicroserviceStudentsAPI.Entities.Student;
import com.empresa.students.MicroserviceStudentsAPI.Repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public List<StudentResponse> findAll() {
        return studentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public StudentResponse findById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado: " + id));
        return toResponse(student);
    }

    @Transactional
    public StudentResponse create(StudentRequest request) {
        if (request.getEmail() != null && studentRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (request.getDni() != null && studentRepository.existsByDni(request.getDni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }

        Student student = Student.builder()
                .name(request.getName())
                .lastName(request.getLastName())
                .dni(request.getDni())
                .email(request.getEmail())
                .password(request.getPassword() != null ? passwordEncoder.encode(request.getPassword()) : null)
                .socio(request.getSocio() != null ? request.getSocio() : false)
                .phone1(request.getPhone1())
                .phone2(request.getPhone2())
                .skillLevel(request.getSkillLevel() != null ? request.getSkillLevel() : SkillLevel.BEGINNER)
                .birthDate(request.getBirthDate())
                .courseIds(request.getCourseIds() != null ? request.getCourseIds() : new HashSet<>())
                .notes(request.getNotes())
                .emergencyContact(request.getEmergencyContact())
                .emergencyPhone(request.getEmergencyPhone())
                .build();

        return toResponse(studentRepository.save(student));
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado: " + id));

        student.setName(request.getName());
        student.setLastName(request.getLastName());
        student.setPhone1(request.getPhone1());
        student.setPhone2(request.getPhone2());
        if (request.getSkillLevel() != null) {
            student.setSkillLevel(request.getSkillLevel());
        }
        if (request.getBirthDate() != null) {
            student.setBirthDate(request.getBirthDate());
        }
        if (request.getSocio() != null) {
            student.setSocio(request.getSocio());
        }
        student.setNotes(request.getNotes());
        student.setEmergencyContact(request.getEmergencyContact());
        student.setEmergencyPhone(request.getEmergencyPhone());

        return toResponse(studentRepository.save(student));
    }

    @Transactional
    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Estudiante no encontrado: " + id);
        }
        studentRepository.deleteById(id);
    }

    public List<StudentResponse> findBySkillLevel(SkillLevel skillLevel) {
        return studentRepository.findBySkillLevel(skillLevel).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<StudentResponse> findByCourseId(Long courseId) {
        return studentRepository.findByCourseId(courseId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<StudentResponse> findSocios() {
        return studentRepository.findBySocioTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<StudentResponse> searchByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void enrollInCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado: " + studentId));
        student.getCourseIds().add(courseId);
        studentRepository.save(student);
    }

    @Transactional
    public void unenrollFromCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado: " + studentId));
        student.getCourseIds().remove(courseId);
        studentRepository.save(student);
    }

    @Transactional
    public void updateSkillLevel(Long studentId, SkillLevel newLevel) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado: " + studentId));
        student.setSkillLevel(newLevel);
        studentRepository.save(student);
        log.info("Nivel de {} actualizado a {}", student.getName(), newLevel);
    }

    // === Nuevos métodos para funcionalidades de alumnos ===

    public List<java.util.Map<String, Object>> getClassHistory(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado: " + studentId));

        List<java.util.Map<String, Object>> history = new java.util.ArrayList<>();
        for (Long courseId : student.getCourseIds()) {
            java.util.Map<String, Object> classInfo = new java.util.HashMap<>();
            classInfo.put("courseId", courseId);
            classInfo.put("studentId", studentId);
            classInfo.put("status", "enrolled");
            history.add(classInfo);
        }
        return history;
    }

    public List<java.util.Map<String, Object>> getExercisesCompleted(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado: " + studentId));

        List<java.util.Map<String, Object>> exercises = new java.util.ArrayList<>();
        String level = student.getSkillLevel() != null ? student.getSkillLevel().name() : "BEGINNER";

        switch (level.toUpperCase()) {
            case "BEGINNER" -> {
                exercises.add(java.util.Map.of("name", "Posición básica", "completed", true));
                exercises.add(java.util.Map.of("name", "Manejo del timón", "completed", true));
                exercises.add(java.util.Map.of("name", "Orzar y arribar", "completed", false));
            }
            case "INTERMEDIATE" -> {
                exercises.add(java.util.Map.of("name", "Virada por avante", "completed", true));
                exercises.add(java.util.Map.of("name", "Trasluchada", "completed", true));
                exercises.add(java.util.Map.of("name", "Navegación en ceñida", "completed", false));
            }
            case "ADVANCED", "EXPERT" -> {
                exercises.add(java.util.Map.of("name", "Navegación en planeo", "completed", true));
                exercises.add(java.util.Map.of("name", "Uso del arnés", "completed", true));
                exercises.add(java.util.Map.of("name", "Water start", "completed", false));
            }
        }
        return exercises;
    }

    public java.util.Map<String, Object> getStudentStats(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado: " + studentId));

        return java.util.Map.of(
                "studentId", studentId,
                "name", student.getName() + " " + (student.getLastName() != null ? student.getLastName() : ""),
                "skillLevel", student.getSkillLevel() != null ? student.getSkillLevel().name() : "BEGINNER",
                "totalCourses", student.getCourseIds() != null ? student.getCourseIds().size() : 0,
                "isMember", student.getSocio() != null ? student.getSocio() : false
        );
    }

    public int getTotalClassesAttended(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado: " + studentId));
        return student.getCourseIds() != null ? student.getCourseIds().size() : 0;
    }

    public java.util.Map<String, Object> getWeatherPrediction() {
        // Retornar predicción básica - en producción se llamaría al microservicio de cursos
        return java.util.Map.of(
                "windTrend", "STABLE",
                "recommendation", "Buenas condiciones para navegación",
                "suitableForBeginners", true,
                "suitableForAdvanced", true,
                "bestActivityType", "CATAMARAN"
        );
    }

    private StudentResponse toResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .name(student.getName())
                .lastName(student.getLastName())
                .dni(student.getDni())
                .email(student.getEmail())
                .socio(student.getSocio())
                .phone1(student.getPhone1())
                .phone2(student.getPhone2())
                .skillLevel(student.getSkillLevel())
                .birthDate(student.getBirthDate())
                .courseIds(student.getCourseIds())
                .notes(student.getNotes())
                .emergencyContact(student.getEmergencyContact())
                .emergencyPhone(student.getEmergencyPhone())
                .build();
    }
}

