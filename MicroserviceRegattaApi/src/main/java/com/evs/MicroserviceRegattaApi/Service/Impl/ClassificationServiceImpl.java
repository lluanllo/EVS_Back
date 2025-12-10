package com.evs.MicroserviceRegattaApi.Service.Impl;

import com.evs.MicroserviceRegattaApi.Entities.RaceResult;
import com.evs.MicroserviceRegattaApi.Entities.RegattaParticipant;
import com.evs.MicroserviceRegattaApi.Repository.RaceResultRepository;
import com.evs.MicroserviceRegattaApi.Service.Inter.IClassificationService;
import com.evs.MicroserviceRegattaApi.Service.Inter.IParticipantService;
import com.evs.MicroserviceRegattaApi.Service.Inter.IRaceResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassificationServiceImpl implements IClassificationService {

    private final RaceResultRepository resultRepository;
    private final IParticipantService participantService;

    @Override
    @Transactional
    public void calculateRacePositions(Long raceId) {
        List<RaceResult> results = resultRepository.findByRaceIdOrderByCorrectedTime(raceId);
        int position = 1;
        int totalParticipants = results.size();

        for (RaceResult result : results) {
            if ("FINISH".equals(result.getStatus())) {
                result.setPosition(position++);
            }
            result.assignPoints(totalParticipants);
            resultRepository.save(result);
        }

        log.info("Posiciones calculadas para manga {} - {} participantes", raceId, totalParticipants);
    }

    @Override
    @Transactional
    public void updateTotalPoints(Long regattaId) {
        List<RegattaParticipant> participants = participantService.findByRegatta(regattaId);

        for (RegattaParticipant participant : participants) {
            Integer totalPoints = resultRepository.sumPointsByParticipant(participant.getId());
            participantService.updateTotalPoints(participant.getId(), totalPoints != null ? totalPoints : 0);
        }

        log.info("Puntos totales actualizados para regata {}", regattaId);
    }

    @Override
    @Transactional
    public void calculateFinalClassification(Long regattaId) {
        List<RegattaParticipant> participants = participantService.getClassification(regattaId);

        int position = 1;
        for (RegattaParticipant participant : participants) {
            if (!"RETIRADO".equals(participant.getStatus()) && !"DESCALIFICADO".equals(participant.getStatus())) {
                participantService.setFinalPosition(participant.getId(), position++);
            }
        }

        log.info("Clasificación final calculada para regata {} - {} clasificados", regattaId, position - 1);
    }

    @Override
    @Transactional
    public void applyDiscards(Long regattaId, int numberOfDiscards) {
        List<RegattaParticipant> participants = participantService.findByRegatta(regattaId);

        for (RegattaParticipant participant : participants) {
            List<RaceResult> results = resultRepository.findByParticipantId(participant.getId());

            if (results.size() > numberOfDiscards) {
                // Ordenar por puntos descendente para descartar los peores
                results.sort(Comparator.comparingInt(RaceResult::getPoints).reversed());

                int totalPoints = 0;
                int count = 0;
                for (RaceResult result : results) {
                    if (count < results.size() - numberOfDiscards) {
                        totalPoints += result.getPoints();
                    }
                    count++;
                }

                participantService.updateTotalPoints(participant.getId(), totalPoints);
            }
        }

        log.info("Descartes aplicados a regata {}: {} mangas descartadas", regattaId, numberOfDiscards);
    }
}

