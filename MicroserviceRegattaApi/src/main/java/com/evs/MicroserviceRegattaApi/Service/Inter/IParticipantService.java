package com.evs.MicroserviceRegattaApi.Service.Inter;

import com.evs.MicroserviceRegattaApi.Entities.RegattaParticipant;

import java.util.List;

/**
 * Interfaz para la gestión de participantes en regatas (Single Responsibility)
 */
public interface IParticipantService {

    RegattaParticipant register(Long regattaId, Long boatId, Long skipperId,
                                 List<Long> crewIds, List<String> crewNames);

    RegattaParticipant confirm(Long participantId);

    RegattaParticipant withdraw(Long participantId);

    RegattaParticipant findById(Long id);

    List<RegattaParticipant> findByRegatta(Long regattaId);

    List<RegattaParticipant> findBySkipper(Long skipperId);

    List<RegattaParticipant> getClassification(Long regattaId);

    void updateTotalPoints(Long participantId, Integer points);

    void setFinalPosition(Long participantId, Integer position);
}

