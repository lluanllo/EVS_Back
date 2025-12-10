package com.evs.MicroserviceRegattaApi.Service.Inter;

import com.evs.MicroserviceRegattaApi.Entities.Boat;

import java.util.List;

/**
 * Interfaz para la gestión de barcos (Single Responsibility)
 */
public interface IBoatService {

    Boat create(Boat boat);

    Boat update(Long id, Boat boat);

    Boat findById(Long id);

    Boat findBySailNumber(String sailNumber);

    List<Boat> findAll();

    List<Boat> findByOwner(Long ownerId);

    List<Boat> findByClass(String boatClass);

    void delete(Long id);

    boolean existsBySailNumber(String sailNumber);
}

