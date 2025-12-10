package com.empresa.course.MicroserviceCourseApi.Repository;

import com.empresa.course.MicroserviceCourseApi.Entities.RaceResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaceResultRepository extends JpaRepository<RaceResult, Long> {

    List<RaceResult> findByRaceIdOrderByPositionAsc(Long raceId);

    List<RaceResult> findByParticipantId(Long participantId);

    @Query("SELECT rr FROM RaceResult rr WHERE rr.race.id = :raceId ORDER BY rr.correctedSeconds ASC NULLS LAST")
    List<RaceResult> findByRaceIdOrderByCorrectedTime(@Param("raceId") Long raceId);

    @Query("SELECT SUM(rr.points) FROM RaceResult rr WHERE rr.participant.id = :participantId")
    Integer sumPointsByParticipant(@Param("participantId") Long participantId);
}

