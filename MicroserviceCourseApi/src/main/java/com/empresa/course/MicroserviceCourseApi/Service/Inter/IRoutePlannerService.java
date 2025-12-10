package com.empresa.course.MicroserviceCourseApi.Service.Inter;

import com.empresa.course.MicroserviceCourseApi.Entities.Enums.CourseType;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.WindDirection;
import com.empresa.course.MicroserviceCourseApi.Entities.RoutePlan;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para la planificación de rutas de navegación (Single Responsibility)
 */
public interface IRoutePlannerService {

    RoutePlan generateRoutePlan(Long courseId, CourseType courseType, WindDirection windDirection,
                                 int windSpeedKnots, int classDurationMinutes, int studentLevel);

    Optional<RoutePlan> findById(Long id);

    List<RoutePlan> findByCourse(Long courseId);

    Optional<RoutePlan> findLatestByCourse(Long courseId);

    void delete(Long id);
}

