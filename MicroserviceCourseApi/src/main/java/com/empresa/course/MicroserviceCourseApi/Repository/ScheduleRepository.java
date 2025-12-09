package com.empresa.course.MicroserviceCourseApi.Repository;

import com.empresa.course.MicroserviceCourseApi.Entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByCourseId(Long courseId);

    List<Schedule> findByTeacherId(Long teacherId);

    List<Schedule> findByDayOfWeek(DayOfWeek dayOfWeek);

    List<Schedule> findByTeacherIdAndConfirmedFalse(Long teacherId);

    List<Schedule> findByConfirmedFalse();
}

