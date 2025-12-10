package com.empresa.course.MicroserviceCourseApi.Service.Impl;

import com.empresa.course.MicroserviceCourseApi.Entities.*;
import com.empresa.course.MicroserviceCourseApi.Repository.*;
import com.empresa.course.MicroserviceCourseApi.Service.Inter.IRegattaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegattaService implements IRegattaService {

    private final RegattaRepository regattaRepository;
    private final BoatRepository boatRepository;
    private final RegattaParticipantRepository participantRepository;
    private final RaceRepository raceRepository;
    private final RaceResultRepository raceResultRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // === Regatta Management ===

    @Override
    @Transactional
    public Regatta createRegatta(Regatta regatta, Long createdBy) {
        regatta.setCreatedBy(createdBy);
        regatta.setCreatedAt(LocalDateTime.now());
        regatta.setStatus("PLANIFICADA");

        Regatta saved = regattaRepository.save(regatta);
        log.info("Regata creada: {} por usuario {}", saved.getId(), createdBy);

        kafkaTemplate.send("regatta-events", "regatta-created", Map.of(
                "regattaId", saved.getId(),
                "name", saved.getName(),
                "date", saved.getEventDate().toString()
        ));

        return saved;
    }

    @Override
    @Transactional
    public Regatta updateRegatta(Long id, Regatta regatta) {
        Regatta existing = getRegattaById(id);
        existing.setName(regatta.getName());
        existing.setDescription(regatta.getDescription());
        existing.setEventDate(regatta.getEventDate());
        existing.setRegistrationDeadline(regatta.getRegistrationDeadline());
        existing.setLocation(regatta.getLocation());
        existing.setMaxParticipants(regatta.getMaxParticipants());
        existing.setRegistrationFee(regatta.getRegistrationFee());
        existing.setPlannedRaces(regatta.getPlannedRaces());
        existing.setNotes(regatta.getNotes());
        return regattaRepository.save(existing);
    }

    @Override
    public Regatta getRegattaById(Long id) {
        return regattaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regata no encontrada: " + id));
    }

    @Override
    public List<Regatta> getAllRegattas() {
        return regattaRepository.findAll();
    }

    @Override
    public List<Regatta> getUpcomingRegattas() {
        return regattaRepository.findByEventDateAfterOrderByEventDateAsc(LocalDate.now());
    }

    @Override
    public List<Regatta> getRegattasByStatus(String status) {
        return regattaRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public void deleteRegatta(Long id) {
        Regatta regatta = getRegattaById(id);
        if (!"PLANIFICADA".equals(regatta.getStatus())) {
            throw new RuntimeException("Solo se pueden eliminar regatas planificadas");
        }
        regattaRepository.deleteById(id);
        log.info("Regata eliminada: {}", id);
    }

    @Override
    @Transactional
    public Regatta openRegistrations(Long regattaId) {
        Regatta regatta = getRegattaById(regattaId);
        regatta.setStatus("INSCRIPCIONES_ABIERTAS");
        return regattaRepository.save(regatta);
    }

    @Override
    @Transactional
    public Regatta closeRegistrations(Long regattaId) {
        Regatta regatta = getRegattaById(regattaId);
        regatta.setStatus("INSCRIPCIONES_CERRADAS");
        return regattaRepository.save(regatta);
    }

    @Override
    @Transactional
    public Regatta startRegatta(Long regattaId) {
        Regatta regatta = getRegattaById(regattaId);
        regatta.setStatus("EN_CURSO");
        return regattaRepository.save(regatta);
    }

    @Override
    @Transactional
    public Regatta finishRegatta(Long regattaId) {
        Regatta regatta = getRegattaById(regattaId);
        calculateFinalClassification(regattaId);
        regatta.setStatus("FINALIZADA");
        return regattaRepository.save(regatta);
    }

    @Override
    @Transactional
    public Regatta assignStaff(Long regattaId, List<Long> staffIds) {
        Regatta regatta = getRegattaById(regattaId);
        regatta.setStaffIds(staffIds);
        return regattaRepository.save(regatta);
    }

    // === Boat Management ===

    @Override
    @Transactional
    public Boat createBoat(Boat boat) {
        if (boatRepository.existsBySailNumber(boat.getSailNumber())) {
            throw new RuntimeException("Ya existe un barco con número de vela: " + boat.getSailNumber());
        }
        Boat saved = boatRepository.save(boat);
        log.info("Barco registrado: {} - {}", saved.getSailNumber(), saved.getName());
        return saved;
    }

    @Override
    @Transactional
    public Boat updateBoat(Long id, Boat boat) {
        Boat existing = getBoatById(id);
        existing.setName(boat.getName());
        existing.setBoatClass(boat.getBoatClass());
        existing.setLength(boat.getLength());
        existing.setBeam(boat.getBeam());
        existing.setSailArea(boat.getSailArea());
        existing.setAutoRating(boat.getAutoRating());
        if (!boat.getAutoRating()) {
            existing.setRating(boat.getRating());
        }
        existing.setNotes(boat.getNotes());
        return boatRepository.save(existing);
    }

    @Override
    public Boat getBoatById(Long id) {
        return boatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Barco no encontrado: " + id));
    }

    @Override
    public List<Boat> getAllBoats() {
        return boatRepository.findAll();
    }

    @Override
    public List<Boat> getBoatsByOwner(Long ownerId) {
        return boatRepository.findByOwnerId(ownerId);
    }

    @Override
    @Transactional
    public void deleteBoat(Long id) {
        boatRepository.deleteById(id);
        log.info("Barco eliminado: {}", id);
    }

    // === Participant Registration ===

    @Override
    @Transactional
    public RegattaParticipant registerParticipant(Long regattaId, Long boatId, Long skipperId,
                                                   List<Long> crewIds, List<String> crewNames) {
        Regatta regatta = getRegattaById(regattaId);

        if (!"INSCRIPCIONES_ABIERTAS".equals(regatta.getStatus())) {
            throw new RuntimeException("Las inscripciones no están abiertas");
        }

        if (regatta.getMaxParticipants() != null) {
            long currentCount = participantRepository.countByRegattaId(regattaId);
            if (currentCount >= regatta.getMaxParticipants()) {
                throw new RuntimeException("Se ha alcanzado el máximo de participantes");
            }
        }

        // Verificar que el barco no está ya inscrito
        if (participantRepository.findByRegattaIdAndBoatId(regattaId, boatId).isPresent()) {
            throw new RuntimeException("Este barco ya está inscrito en la regata");
        }

        Boat boat = getBoatById(boatId);

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

        // Enviar evento para pago
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
    public RegattaParticipant confirmParticipant(Long participantId) {
        RegattaParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participante no encontrado: " + participantId));
        participant.setStatus("CONFIRMADO");
        participant.setFeePaid(true);
        return participantRepository.save(participant);
    }

    @Override
    @Transactional
    public RegattaParticipant withdrawParticipant(Long participantId) {
        RegattaParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participante no encontrado: " + participantId));
        participant.setStatus("RETIRADO");
        return participantRepository.save(participant);
    }

    @Override
    public List<RegattaParticipant> getParticipantsByRegatta(Long regattaId) {
        return participantRepository.findByRegattaId(regattaId);
    }

    @Override
    public List<RegattaParticipant> getClassification(Long regattaId) {
        return participantRepository.findByRegattaIdOrderByTotalPoints(regattaId);
    }

    // === Race Management ===

    @Override
    @Transactional
    public Race createRace(Long regattaId, Integer raceNumber) {
        Regatta regatta = getRegattaById(regattaId);

        Race race = Race.builder()
                .regatta(regatta)
                .raceNumber(raceNumber)
                .status("PLANIFICADA")
                .numberOfLaps(1)
                .build();

        return raceRepository.save(race);
    }

    @Override
    @Transactional
    public Race startRace(Long raceId, Integer windDirection, Double windSpeed) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new RuntimeException("Manga no encontrada: " + raceId));
        race.setStartTime(LocalDateTime.now());
        race.setWindDirection(windDirection);
        race.setWindSpeed(windSpeed);
        race.setStatus("EN_CURSO");
        return raceRepository.save(race);
    }

    @Override
    @Transactional
    public Race finishRace(Long raceId) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new RuntimeException("Manga no encontrada: " + raceId));

        // Calcular posiciones por tiempo corregido
        List<RaceResult> results = raceResultRepository.findByRaceIdOrderByCorrectedTime(raceId);
        int position = 1;
        int totalParticipants = results.size();

        for (RaceResult result : results) {
            if ("FINISH".equals(result.getStatus())) {
                result.setPosition(position++);
            }
            result.assignPoints(totalParticipants);
            raceResultRepository.save(result);

            // Actualizar puntos totales del participante
            Integer totalPoints = raceResultRepository.sumPointsByParticipant(result.getParticipant().getId());
            result.getParticipant().setTotalPoints(totalPoints != null ? totalPoints : 0);
            participantRepository.save(result.getParticipant());
        }

        race.setStatus("FINALIZADA");
        return raceRepository.save(race);
    }

    @Override
    public List<Race> getRacesByRegatta(Long regattaId) {
        return raceRepository.findByRegattaIdOrderByRaceNumberAsc(regattaId);
    }

    // === Race Results ===

    @Override
    @Transactional
    public RaceResult recordFinish(Long raceId, Long participantId, Long elapsedSeconds) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new RuntimeException("Manga no encontrada: " + raceId));
        RegattaParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participante no encontrado: " + participantId));

        RaceResult result = RaceResult.builder()
                .race(race)
                .participant(participant)
                .elapsedSeconds(elapsedSeconds)
                .status("FINISH")
                .penaltyPoints(0)
                .build();

        // Calcular tiempo corregido con rating del barco
        result.calculateCorrectedTime(participant.getBoat().getRating());

        return raceResultRepository.save(result);
    }

    @Override
    @Transactional
    public RaceResult recordDNF(Long raceId, Long participantId, String reason) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new RuntimeException("Manga no encontrada: " + raceId));
        RegattaParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participante no encontrado: " + participantId));

        RaceResult result = RaceResult.builder()
                .race(race)
                .participant(participant)
                .status("DNF")
                .notes(reason)
                .penaltyPoints(0)
                .build();

        return raceResultRepository.save(result);
    }

    @Override
    @Transactional
    public RaceResult recordDSQ(Long raceId, Long participantId, String reason) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new RuntimeException("Manga no encontrada: " + raceId));
        RegattaParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participante no encontrado: " + participantId));

        RaceResult result = RaceResult.builder()
                .race(race)
                .participant(participant)
                .status("DSQ")
                .notes(reason)
                .penaltyPoints(0)
                .build();

        return raceResultRepository.save(result);
    }

    @Override
    public List<RaceResult> getRaceResults(Long raceId) {
        return raceResultRepository.findByRaceIdOrderByPositionAsc(raceId);
    }

    @Override
    @Transactional
    public void calculateFinalClassification(Long regattaId) {
        List<RegattaParticipant> participants = getClassification(regattaId);

        int position = 1;
        for (RegattaParticipant participant : participants) {
            if (!"RETIRADO".equals(participant.getStatus()) && !"DESCALIFICADO".equals(participant.getStatus())) {
                participant.setFinalPosition(position++);
                participantRepository.save(participant);
            }
        }

        log.info("Clasificación final calculada para regata {}", regattaId);
    }
}

