package com.empresa.course.MicroserviceCourseApi.Repository;

import com.empresa.course.MicroserviceCourseApi.Entities.RoutePlan;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoutePlanRepository extends JpaRepository<RoutePlan, Long> {

    List<RoutePlan> findByCourseId(Long courseId);

    List<RoutePlan> findByCourseType(CourseType courseType);

    List<RoutePlan> findByCreatedAtAfter(LocalDateTime dateTime);

    // Últimos planes generados
    List<RoutePlan> findTop10ByOrderByCreatedAtDesc();
}

