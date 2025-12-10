package com.evs.MicroserviceRegattaApi.Service.Inter;

import com.evs.MicroserviceRegattaApi.Entities.Race;

import java.util.List;

/**
 * Interfaz para la gestión de mangas (Single Responsibility)
 */
public interface IRaceService {

    Race create(Long regattaId, Integer raceNumber);

    Race start(Long raceId, Integer windDirection, Double windSpeed);

    Race finish(Long raceId);

    Race abandon(Long raceId, String reason);

    Race findById(Long id);

    List<Race> findByRegatta(Long regattaId);

    List<Race> findByRegattaAndStatus(Long regattaId, String status);
}

