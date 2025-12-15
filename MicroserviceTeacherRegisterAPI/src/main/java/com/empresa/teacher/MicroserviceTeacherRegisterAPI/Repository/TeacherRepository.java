package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Repository;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.ContractType;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.Specialty;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByEmail(String email);

    Optional<Teacher> findByDni(String dni);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    // Buscar profesores disponibles
    List<Teacher> findByAvailableTrue();

    // Buscar por tipo de contrato ordenados por horas trabajadas (equidad)
    List<Teacher> findByContractTypeOrderByWorkedHoursAsc(ContractType contractType);

    // Buscar profesores por especialidad
    @Query("SELECT t FROM Teacher t JOIN t.specialities s WHERE s = :speciality AND t.available = true")
    List<Teacher> findBySpecialityAndAvailable(@Param("speciality") Specialty speciality);

    // Buscar profesores disponibles por especialidad ordenados por prioridad de contrato y horas trabajadas
    @Query("SELECT t FROM Teacher t JOIN t.specialities s WHERE s = :speciality AND t.available = true " +
           "ORDER BY t.contractType ASC, t.workedHours ASC")
    List<Teacher> findAvailableBySpecialityOrderByPriorityAndHours(@Param("speciality") Specialty speciality);

    // Buscar profesores que no han confirmado disponibilidad
    List<Teacher> findByConfirmedAvailabilityFalse();

    // Buscar por curso asignado
    List<Teacher> findByIdCourse(Long courseId);
}
