package com.evs.MicroserviceRegattaApi.Service.Impl;

import com.evs.MicroserviceRegattaApi.Entities.Boat;
import com.evs.MicroserviceRegattaApi.Entities.Regatta;
import com.evs.MicroserviceRegattaApi.Entities.RegattaParticipant;
import com.evs.MicroserviceRegattaApi.Repository.RegattaParticipantRepository;
import com.evs.MicroserviceRegattaApi.Service.Inter.IBoatService;
import com.evs.MicroserviceRegattaApi.Service.Inter.IParticipantService;
import com.evs.MicroserviceRegattaApi.Service.Inter.IRegattaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements IParticipantService {

    private final RegattaParticipantRepository participantRepository;
    private final IBoatService boatService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Lazy
    private final IRegattaService regattaService;

    @Override
    @Transactional
    public RegattaParticipant register(Long regattaId, Long boatId, Long skipperId,
                                        List<Long> crewIds, List<String> crewNames) {
        Regatta regatta = regattaService.findById(regattaId);

        if (!"INSCRIPCIONES_ABIERTAS".equals(regatta.getStatus())) {
            throw new RuntimeException("Las inscripciones no están abiertas");
        }

        if (regatta.getMaxParticipants() != null) {
            long currentCount = participantRepository.countByRegattaId(regattaId);
            if (currentCount >= regatta.getMaxParticipants()) {
                throw new RuntimeException("Se ha alcanzado el máximo de participantes");
            }
        }

        if (participantRepository.findByRegattaIdAndBoatId(regattaId, boatId).isPresent()) {
            throw new RuntimeException("Este barco ya está inscrito en la regata");
        }

        Boat boat = boatService.findById(boatId);

        RegattaParticipant participant = RegattaParticipant.builder()
                .regatta(regatta)
                .boat(boat)
                .skipperId(skipperId)
                .crewIds(crewIds != null ? crewIds : new ArrayList<>())
                .crewNames(crewNames != null ? crewNames : new ArrayList<>())
                .registrationDate(LocalDateTime.now())
                .status("INSCRITO")
                .feePaid(false)
                .totalPoints(0)
                .build();

        RegattaParticipant saved = participantRepository.save(participant);
        log.info("Participante inscrito en regata {}: barco {} patrón {}", regattaId, boatId, skipperId);

        if (regatta.getRegistrationFee() != null && regatta.getRegistrationFee() > 0) {
            kafkaTemplate.send("regatta-registration-events", "registration-created", Map.of(
                    "regattaId", regattaId,
                    "participantId", saved.getId(),
                    "skipperId", skipperId,
                    "fee", regatta.getRegistrationFee()
            ));
        }

        return saved;
    }

    @Override
    @Transactional
    public RegattaParticipant confirm(Long participantId) {
        RegattaParticipant participant = findById(participantId);
        participant.setStatus("CONFIRMADO");
        participant.setFeePaid(true);
        log.info("Participante confirmado: {}", participantId);
        return participantRepository.save(participant);
    }

    @Override
    @Transactional
    public RegattaParticipant withdraw(Long participantId) {
        RegattaParticipant participant = findById(participantId);
        participant.setStatus("RETIRADO");
        log.info("Participante retirado: {}", participantId);
        return participantRepository.save(participant);
    }

    @Override
    public RegattaParticipant findById(Long id) {
        return participantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participante no encontrado: " + id));
    }

    @Override
    public List<RegattaParticipant> findByRegatta(Long regattaId) {
        return participantRepository.findByRegattaId(regattaId);
    }

    @Override
    public List<RegattaParticipant> findBySkipper(Long skipperId) {
        return participantRepository.findBySkipperId(skipperId);
    }

    @Override
    public List<RegattaParticipant> getClassification(Long regattaId) {
        return participantRepository.findByRegattaIdOrderByTotalPoints(regattaId);
    }

    @Override
    @Transactional
    public void updateTotalPoints(Long participantId, Integer points) {
        RegattaParticipant participant = findById(participantId);
        participant.setTotalPoints(points);
        participantRepository.save(participant);
    }

    @Override
    @Transactional
    public void setFinalPosition(Long participantId, Integer position) {
        RegattaParticipant participant = findById(participantId);
        participant.setFinalPosition(position);
        participantRepository.save(participant);
    }
}

