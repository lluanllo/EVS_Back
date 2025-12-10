package com.empresa.course.MicroserviceCourseApi.Repository;

import com.empresa.course.MicroserviceCourseApi.Entities.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByStudentId(Long studentId);

    List<Rental> findByEquipmentId(Long equipmentId);

    List<Rental> findByStatus(String status);

    @Query("SELECT r FROM Rental r WHERE r.equipment.id = :equipmentId AND r.status = 'ACTIVO'")
    List<Rental> findActiveByEquipment(@Param("equipmentId") Long equipmentId);

    @Query("SELECT r FROM Rental r WHERE r.studentId = :studentId AND r.status = 'ACTIVO'")
    List<Rental> findActiveByStudent(@Param("studentId") Long studentId);

    @Query("SELECT r FROM Rental r WHERE r.expectedEndTime < :now AND r.actualEndTime IS NULL")
    List<Rental> findOverdueRentals(@Param("now") LocalDateTime now);

    @Query("SELECT r FROM Rental r WHERE r.startTime BETWEEN :start AND :end")
    List<Rental> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Rental> findByPaidFalseAndStatus(String status);
}

