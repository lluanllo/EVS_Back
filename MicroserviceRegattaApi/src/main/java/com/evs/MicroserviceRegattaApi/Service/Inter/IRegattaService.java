package com.evs.MicroserviceRegattaApi.Service.Inter;

import com.evs.MicroserviceRegattaApi.Entities.Regatta;

import java.util.List;

/**
 * Interfaz para la gestión de regatas (Single Responsibility)
 */
public interface IRegattaService {

    Regatta create(Regatta regatta, Long createdBy);

    Regatta update(Long id, Regatta regatta);

    Regatta findById(Long id);

    List<Regatta> findAll();

    List<Regatta> findUpcoming();

    List<Regatta> findByStatus(String status);

    void delete(Long id);

    Regatta openRegistrations(Long regattaId);

    Regatta closeRegistrations(Long regattaId);

    Regatta start(Long regattaId);

    Regatta finish(Long regattaId);

    Regatta assignStaff(Long regattaId, List<Long> staffIds);
}

