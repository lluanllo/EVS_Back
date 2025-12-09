package com.empresa.students.MicroserviceStudentsAPI.Repository;

import com.empresa.students.MicroserviceStudentsAPI.Entities.Enums.SkillLevel;
import com.empresa.students.MicroserviceStudentsAPI.Entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);

    Optional<Student> findByDni(String dni);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    // Buscar por nivel de habilidad
    List<Student> findBySkillLevel(SkillLevel skillLevel);

    // Buscar estudiantes inscritos en un curso
    @Query("SELECT s FROM Student s WHERE :courseId MEMBER OF s.courseIds")
    List<Student> findByCourseId(@Param("courseId") Long courseId);

    // Buscar socios
    List<Student> findBySocioTrue();

    // Buscar por nombre (búsqueda parcial)
    List<Student> findByNameContainingIgnoreCase(String name);
}

