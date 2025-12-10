package com.empresa.students.MicroserviceStudentsAPI.Service.Inter;

import com.empresa.students.MicroserviceStudentsAPI.Entities.Student;
import com.empresa.students.MicroserviceStudentsAPI.Entities.Enums.SkillLevel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interfaz para la gestión de estudiantes (Single Responsibility)
 */
public interface IStudentService {

    Student create(Student student);

    Student update(Long id, Student student);

    Student findById(Long id);

    Optional<Student> findByIdOptional(Long id);

    Optional<Student> findByEmail(String email);

    List<Student> findAll();

    List<Student> findBySkillLevel(SkillLevel skillLevel);

    List<Student> findByCourseId(Long courseId);

    List<Student> findMembers();

    List<Student> searchByName(String name);

    void delete(Long id);

    Student enrollInCourse(Long studentId, Long courseId);

    Student unenrollFromCourse(Long studentId, Long courseId);

    Student updateSkillLevel(Long studentId, SkillLevel newLevel);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);
}

