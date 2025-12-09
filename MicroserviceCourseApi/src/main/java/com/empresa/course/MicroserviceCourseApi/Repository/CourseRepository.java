package com.empresa.course.MicroserviceCourseApi.Repository;

import com.empresa.course.MicroserviceCourseApi.Entities.Enums.CourseType;
import com.empresa.course.MicroserviceCourseApi.Entities.Course;
import com.empresa.course.MicroserviceCourseApi.Entities.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Buscar por tipo de curso
    List<Course> findByCourseType(CourseType courseType);

    // Buscar por turno
    List<Course> findByTurno(Turno turno);

    // Buscar cursos activos
    List<Course> findByActiveTrue();

    // Buscar cursos por rango de fechas
    List<Course> findByStartDateBetween(LocalDate start, LocalDate end);

    // Buscar cursos que empiezan hoy o después
    List<Course> findByStartDateGreaterThanEqual(LocalDate date);

    // Buscar cursos con profesor asignado
    @Query("SELECT c FROM Course c WHERE :teacherId MEMBER OF c.teacherIds")
    List<Course> findByTeacherId(@Param("teacherId") Long teacherId);

    // Buscar cursos con estudiante inscrito
    @Query("SELECT c FROM Course c WHERE :studentId MEMBER OF c.studentIds")
    List<Course> findByStudentId(@Param("studentId") Long studentId);

    // Buscar cursos activos por tipo y turno
    List<Course> findByCourseTypeAndTurnoAndActiveTrue(CourseType courseType, Turno turno);

    // Contar estudiantes en un curso
    @Query("SELECT SIZE(c.studentIds) FROM Course c WHERE c.id = :courseId")
    Integer countStudentsByCourseId(@Param("courseId") Long courseId);
}

