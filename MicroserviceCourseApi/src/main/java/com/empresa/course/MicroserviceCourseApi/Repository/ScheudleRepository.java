package com.empresa.course.MicroserviceCourseApi.Repository;

import com.empresa.course.MicroserviceCourseApi.Entities.ScheudleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheudleRepository extends JpaRepository<ScheudleEntity, Long> {
}
