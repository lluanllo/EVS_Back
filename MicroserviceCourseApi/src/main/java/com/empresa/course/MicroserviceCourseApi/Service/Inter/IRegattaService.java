package com.empresa.course.MicroserviceCourseApi.Service.Inter;

import com.empresa.course.MicroserviceCourseApi.Entities.*;

import java.time.LocalDate;
import java.util.List;

public interface IRegattaService {

    // === Regatta Management (BOSS only) ===
    Regatta createRegatta(Regatta regatta, Long createdBy);
    Regatta updateRegatta(Long id, Regatta regatta);
    Regatta getRegattaById(Long id);
    List<Regatta> getAllRegattas();
    List<Regatta> getUpcomingRegattas();
    List<Regatta> getRegattasByStatus(String status);
    void deleteRegatta(Long id);
    Regatta openRegistrations(Long regattaId);
    Regatta closeRegistrations(Long regattaId);
    Regatta startRegatta(Long regattaId);
    Regatta finishRegatta(Long regattaId);
    Regatta assignStaff(Long regattaId, List<Long> staffIds);

    // === Boat Management ===
    Boat createBoat(Boat boat);
    Boat updateBoat(Long id, Boat boat);
    Boat getBoatById(Long id);
    List<Boat> getAllBoats();
    List<Boat> getBoatsByOwner(Long ownerId);
    void deleteBoat(Long id);

    // === Participant Registration ===
    RegattaParticipant registerParticipant(Long regattaId, Long boatId, Long skipperId,
                                            List<Long> crewIds, List<String> crewNames);
    RegattaParticipant confirmParticipant(Long participantId);
    RegattaParticipant withdrawParticipant(Long participantId);
    List<RegattaParticipant> getParticipantsByRegatta(Long regattaId);
    List<RegattaParticipant> getClassification(Long regattaId);

    // === Race Management ===
    Race createRace(Long regattaId, Integer raceNumber);
    Race startRace(Long raceId, Integer windDirection, Double windSpeed);
    Race finishRace(Long raceId);
    List<Race> getRacesByRegatta(Long regattaId);

    // === Race Results ===
    RaceResult recordFinish(Long raceId, Long participantId, Long elapsedSeconds);
    RaceResult recordDNF(Long raceId, Long participantId, String reason);
    RaceResult recordDSQ(Long raceId, Long participantId, String reason);
    List<RaceResult> getRaceResults(Long raceId);
    void calculateFinalClassification(Long regattaId);
}

