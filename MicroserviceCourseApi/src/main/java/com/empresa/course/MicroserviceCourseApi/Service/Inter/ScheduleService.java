package com.empresa.course.MicroserviceCourseApi.Service.Inter;

import com.empresa.course.MicroserviceCourseApi.Entities.Schedule;

import java.util.List;

public interface ScheduleService {

    List<Schedule> findAll();

    Schedule findById(Long id);

    List<Schedule> findByCourseId(Long courseId);

    List<Schedule> findByTeacherId(Long teacherId);

    Schedule create(Schedule schedule);

    Schedule update(Long id, Schedule schedule);

    void delete(Long id);

    void confirmTeacherSchedule(Long teacherId, boolean confirmed);

    void handleTeacherRejection(Long teacherId, String reason);

    void reassignTeacher(Long scheduleId, Long originalTeacherId);
}

