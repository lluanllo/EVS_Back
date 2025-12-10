package com.evs.Contability.MicroserviceContabilityApi.Repository;

import com.evs.Contability.MicroserviceContabilityApi.Entities.WorkedHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkedHoursRepository extends JpaRepository<WorkedHours, Long> {

    List<WorkedHours> findByTeacherId(Long teacherId);

    List<WorkedHours> findByTeacherIdAndValidated(Long teacherId, Boolean validated);

    List<WorkedHours> findByTeacherIdAndPaid(Long teacherId, Boolean paid);

    List<WorkedHours> findByValidated(Boolean validated);

    @Query("SELECT wh FROM WorkedHours wh WHERE wh.teacherId = :teacherId AND wh.date BETWEEN :start AND :end")
    List<WorkedHours> findByTeacherIdAndDateRange(
            @Param("teacherId") Long teacherId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("SELECT wh FROM WorkedHours wh WHERE wh.date BETWEEN :start AND :end")
    List<WorkedHours> findByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT SUM(wh.durationMinutes) FROM WorkedHours wh WHERE wh.teacherId = :teacherId AND wh.date BETWEEN :start AND :end AND wh.validated = true")
    Integer sumValidatedMinutesByTeacherAndDateRange(
            @Param("teacherId") Long teacherId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("SELECT wh FROM WorkedHours wh WHERE wh.teacherId = :teacherId AND wh.validated = true AND wh.paid = false")
    List<WorkedHours> findUnpaidValidatedByTeacher(@Param("teacherId") Long teacherId);

    List<WorkedHours> findByCourseId(Long courseId);
}

