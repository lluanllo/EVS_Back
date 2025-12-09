package com.empresa.course.MicroserviceCourseApi.Repository;

import com.empresa.course.MicroserviceCourseApi.Entities.NauticalZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NauticalZoneRepository extends JpaRepository<NauticalZone, Long> {

    List<NauticalZone> findByActiveTrue();

    List<NauticalZone> findByZoneType(String zoneType);

    List<NauticalZone> findByMinLevelLessThanEqual(Integer level);
}

