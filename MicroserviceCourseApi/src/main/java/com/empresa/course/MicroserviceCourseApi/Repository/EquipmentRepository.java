package com.empresa.course.MicroserviceCourseApi.Repository;

import com.empresa.course.MicroserviceCourseApi.Entities.Equipment;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.EquipmentStatus;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByStatus(EquipmentStatus status);

    List<Equipment> findByEquipmentType(EquipmentType equipmentType);

    List<Equipment> findByEquipmentTypeAndStatus(EquipmentType equipmentType, EquipmentStatus status);

    Optional<Equipment> findBySerialNumber(String serialNumber);

    List<Equipment> findByNextMaintenanceDateBefore(LocalDate date);

    List<Equipment> findByMemberOnlyFalseAndStatus(EquipmentStatus status);
}

