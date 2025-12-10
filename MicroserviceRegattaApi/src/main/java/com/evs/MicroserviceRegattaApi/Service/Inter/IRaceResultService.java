package com.evs.MicroserviceRegattaApi.Service.Inter;

import com.evs.MicroserviceRegattaApi.Entities.RaceResult;

import java.util.List;

/**
 * Interfaz para la gestión de resultados de mangas (Single Responsibility)
 */
public interface IRaceResultService {

    RaceResult recordFinish(Long raceId, Long participantId, Long elapsedSeconds);

    RaceResult recordDNF(Long raceId, Long participantId, String reason);

    RaceResult recordDNS(Long raceId, Long participantId, String reason);

    RaceResult recordDSQ(Long raceId, Long participantId, String reason);

    RaceResult recordOCS(Long raceId, Long participantId);

    RaceResult findById(Long id);

    List<RaceResult> findByRace(Long raceId);

    List<RaceResult> findByParticipant(Long participantId);

    void calculatePositions(Long raceId);
}

