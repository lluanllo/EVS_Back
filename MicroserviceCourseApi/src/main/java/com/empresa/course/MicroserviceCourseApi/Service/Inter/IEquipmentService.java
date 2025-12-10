package com.empresa.course.MicroserviceCourseApi.Service.Inter;

import com.empresa.course.MicroserviceCourseApi.Entities.Equipment;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.EquipmentType;

import java.util.List;

/**
 * Interfaz para la gestión de equipamiento (Single Responsibility)
 */
public interface IEquipmentService {

    Equipment create(Equipment equipment);

    Equipment update(Long id, Equipment equipment);

    Equipment findById(Long id);

    List<Equipment> findAll();

    List<Equipment> findAvailable();

    List<Equipment> findAvailableByType(EquipmentType type);

    void delete(Long id);

    Equipment setMaintenance(Long id);

    Equipment setAvailable(Long id);
}

