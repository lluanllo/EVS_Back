package com.empresa.course.MicroserviceCourseApi.Repository;

import com.empresa.course.MicroserviceCourseApi.Entities.Race;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaceRepository extends JpaRepository<Race, Long> {

    List<Race> findByRegattaIdOrderByRaceNumberAsc(Long regattaId);

    List<Race> findByRegattaIdAndStatus(Long regattaId, String status);
}

