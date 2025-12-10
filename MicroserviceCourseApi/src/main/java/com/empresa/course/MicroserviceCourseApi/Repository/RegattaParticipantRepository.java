package com.empresa.course.MicroserviceCourseApi.Repository;

import com.empresa.course.MicroserviceCourseApi.Entities.RegattaParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegattaParticipantRepository extends JpaRepository<RegattaParticipant, Long> {

    List<RegattaParticipant> findByRegattaId(Long regattaId);

    List<RegattaParticipant> findBySkipperId(Long skipperId);

    List<RegattaParticipant> findByBoatId(Long boatId);

    Optional<RegattaParticipant> findByRegattaIdAndBoatId(Long regattaId, Long boatId);

    @Query("SELECT p FROM RegattaParticipant p WHERE p.regatta.id = :regattaId ORDER BY p.totalPoints ASC")
    List<RegattaParticipant> findByRegattaIdOrderByTotalPoints(@Param("regattaId") Long regattaId);

    long countByRegattaId(Long regattaId);
}

