package com.evs.MicroserviceRegattaApi.Service.Impl;

import com.evs.MicroserviceRegattaApi.Entities.Race;
import com.evs.MicroserviceRegattaApi.Entities.RaceResult;
import com.evs.MicroserviceRegattaApi.Entities.RegattaParticipant;
import com.evs.MicroserviceRegattaApi.Repository.RaceRepository;
import com.evs.MicroserviceRegattaApi.Repository.RaceResultRepository;
import com.evs.MicroserviceRegattaApi.Service.Inter.IParticipantService;
import com.evs.MicroserviceRegattaApi.Service.Inter.IRaceResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RaceResultServiceImpl implements IRaceResultService {

    private final RaceResultRepository resultRepository;
    private final RaceRepository raceRepository;
    private final IParticipantService participantService;

    @Override
    @Transactional
    public RaceResult recordFinish(Long raceId, Long participantId, Long elapsedSeconds) {
        Race race = findRace(raceId);
        RegattaParticipant participant = participantService.findById(participantId);

        RaceResult result = RaceResult.builder()
                .race(race)
                .participant(participant)
                .elapsedSeconds(elapsedSeconds)
                .status("FINISH")
                .penaltyPoints(0)
                .build();

        result.calculateCorrectedTime(participant.getBoat().getRating());

        RaceResult saved = resultRepository.save(result);
        log.info("Llegada registrada: Participante {} en manga {} - {} segundos", 
                participantId, raceId, elapsedSeconds);
        return saved;
    }

    @Override
    @Transactional
    public RaceResult recordDNF(Long raceId, Long participantId, String reason) {
        return recordNonFinish(raceId, participantId, "DNF", reason);
    }

    @Override
    @Transactional
    public RaceResult recordDNS(Long raceId, Long participantId, String reason) {
        return recordNonFinish(raceId, participantId, "DNS", reason);
    }

    @Override
    @Transactional
    public RaceResult recordDSQ(Long raceId, Long participantId, String reason) {
        return recordNonFinish(raceId, participantId, "DSQ", reason);
    }

    @Override
    @Transactional
    public RaceResult recordOCS(Long raceId, Long participantId) {
        return recordNonFinish(raceId, participantId, "OCS", "On Course Side - Salida anticipada");
    }

    private RaceResult recordNonFinish(Long raceId, Long participantId, String status, String reason) {
        Race race = findRace(raceId);
        RegattaParticipant participant = participantService.findById(participantId);

        RaceResult result = RaceResult.builder()
                .race(race)
                .participant(participant)
                .status(status)
                .notes(reason)
                .penaltyPoints(0)
                .build();

        RaceResult saved = resultRepository.save(result);
        log.info("{} registrado: Participante {} en manga {} - {}", status, participantId, raceId, reason);
        return saved;
    }

    @Override
    public RaceResult findById(Long id) {
        return resultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resultado no encontrado: " + id));
    }

    @Override
    public List<RaceResult> findByRace(Long raceId) {
        return resultRepository.findByRaceIdOrderByPositionAsc(raceId);
    }

    @Override
    public List<RaceResult> findByParticipant(Long participantId) {
        return resultRepository.findByParticipantId(participantId);
    }

    @Override
    @Transactional
    public void calculatePositions(Long raceId) {
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
        
        log.info("Posiciones calculadas para manga {}", raceId);
    }

    private Race findRace(Long raceId) {
        return raceRepository.findById(raceId)
                .orElseThrow(() -> new RuntimeException("Manga no encontrada: " + raceId));
    }
}

