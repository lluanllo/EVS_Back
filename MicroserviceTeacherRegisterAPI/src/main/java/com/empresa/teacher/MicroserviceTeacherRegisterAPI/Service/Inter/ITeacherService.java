package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service.Inter;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Teacher;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.ContractType;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.Specialty;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para la gestión de profesores (Single Responsibility)
 */
public interface ITeacherService {

    Teacher create(Teacher teacher);

    Teacher update(Long id, Teacher teacher);

    Teacher findById(Long id);

    Optional<Teacher> findByIdOptional(Long id);

    Optional<Teacher> findByEmail(String email);

    List<Teacher> findAll();

    List<Teacher> findActive();

    List<Teacher> findBySpecialty(Specialty specialty);

    List<Teacher> findByContractType(ContractType contractType);

    List<Teacher> findAvailable();

    void delete(Long id);

    Teacher activate(Long id);

    Teacher deactivate(Long id);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);
}

