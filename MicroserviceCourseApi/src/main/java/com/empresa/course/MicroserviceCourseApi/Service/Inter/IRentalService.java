package com.empresa.course.MicroserviceCourseApi.Service.Inter;

import com.empresa.course.MicroserviceCourseApi.Entities.Equipment;
import com.empresa.course.MicroserviceCourseApi.Entities.Rental;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.EquipmentType;

import java.time.LocalDateTime;
import java.util.List;

public interface IRentalService {

    // Equipment CRUD
    Equipment createEquipment(Equipment equipment);
    Equipment updateEquipment(Long id, Equipment equipment);
    Equipment getEquipmentById(Long id);
    List<Equipment> getAllEquipment();
    List<Equipment> getAvailableEquipment();
    List<Equipment> getAvailableEquipmentByType(EquipmentType type);
    void deleteEquipment(Long id);
    Equipment setEquipmentMaintenance(Long id);

    // Rental operations
    Rental createRental(Long equipmentId, Long studentId, LocalDateTime startTime, LocalDateTime endTime, Long registeredBy);
    Rental completeRental(Long rentalId, String returnCondition, String damageReported);
    Rental cancelRental(Long rentalId);
    Rental getRentalById(Long id);
    List<Rental> getAllRentals();
    List<Rental> getActiveRentals();
    List<Rental> getRentalsByStudent(Long studentId);
    List<Rental> getActiveRentalsByStudent(Long studentId);
    List<Rental> getOverdueRentals();
    List<Rental> getUnpaidCompletedRentals();

    // Aptitude verification
    boolean verifyStudentAptitude(Long studentId, Long equipmentId);
    Rental markAptitudeVerified(Long rentalId, Long verifiedBy);
}

