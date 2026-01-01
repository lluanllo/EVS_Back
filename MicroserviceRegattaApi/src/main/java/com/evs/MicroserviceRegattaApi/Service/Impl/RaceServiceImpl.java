package com.evs.MicroserviceRegattaApi.Service.Impl;

import com.evs.MicroserviceRegattaApi.Entities.Race;
import com.evs.MicroserviceRegattaApi.Entities.Regatta;
import com.evs.MicroserviceRegattaApi.Repository.RaceRepository;
import com.evs.MicroserviceRegattaApi.Service.Inter.IClassificationService;
import com.evs.MicroserviceRegattaApi.Service.Inter.IRaceService;
import com.evs.MicroserviceRegattaApi.Service.Inter.IRegattaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RaceServiceImpl implements IRaceService {

    private final RaceRepository raceRepository;
    private final IClassificationService classificationService;
    
    @Lazy
    private final IRegattaService regattaService;

    @Override
    @Transactional
    public Race create(Long regattaId, Integer raceNumber) {
        Regatta regatta = regattaService.findById(regattaId);
        
        Race race = Race.builder()
                .regatta(regatta)
                .raceNumber(raceNumber)
                .status("PLANIFICADA")
                .numberOfLaps(1)
                .build();

        Race saved = raceRepository.save(race);
        log.info("Manga {} creada para regata {}", raceNumber, regattaId);
        return saved;
    }

    @Override
    @Transactional
    public Race start(Long raceId, Integer windDirection, Double windSpeed) {
        Race race = findById(raceId);
        race.setStartTime(LocalDateTime.now());
        race.setWindDirection(windDirection);
        race.setWindSpeed(windSpeed);
        race.setStatus("EN_CURSO");
        log.info("Manga {} iniciada. Viento: {}° a {} kts", raceId, windDirection, windSpeed);
        return raceRepository.save(race);
    }

    @Override
    @Transactional
    public Race finish(Long raceId) {
        Race race = findById(raceId);
        
        // Calcular posiciones
        classificationService.calculateRacePositions(raceId);
        
        // Actualizar puntos totales
        classificationService.updateTotalPoints(race.getRegatta().getId());

        race.setStatus("FINALIZADA");
        log.info("Manga {} finalizada", raceId);
        return raceRepository.save(race);
    }

    @Override
    @Transactional
    public Race abandon(Long raceId, String reason) {
        Race race = findById(raceId);
        race.setStatus("ABANDONADA");
        race.setNotes(reason);
        log.info("Manga {} abandonada: {}", raceId, reason);
        return raceRepository.save(race);
    }

    @Override
    public Race findById(Long id) {
        return raceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manga no encontrada: " + id));
    }

    @Override
    public List<Race> findByRegatta(Long regattaId) {
        return raceRepository.findByRegattaIdOrderByRaceNumberAsc(regattaId);
    }

    @Override
    public List<Race> findByRegattaAndStatus(Long regattaId, String status) {
        return raceRepository.findByRegattaIdAndStatus(regattaId, status);
    }
}

