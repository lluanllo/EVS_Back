package com.evs.MicroserviceRegattaApi.Service.Impl;

import com.evs.MicroserviceRegattaApi.Entities.Boat;
import com.evs.MicroserviceRegattaApi.Repository.BoatRepository;
import com.evs.MicroserviceRegattaApi.Service.Inter.IBoatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoatServiceImpl implements IBoatService {

    private final BoatRepository boatRepository;

    @Override
    @Transactional
    public Boat create(Boat boat) {
        if (boatRepository.existsBySailNumber(boat.getSailNumber())) {
            throw new RuntimeException("Ya existe un barco con número de vela: " + boat.getSailNumber());
        }
        Boat saved = boatRepository.save(boat);
        log.info("Barco registrado: {} - {}", saved.getSailNumber(), saved.getName());
        return saved;
    }

    @Override
    @Transactional
    public Boat update(Long id, Boat boat) {
        Boat existing = findById(id);
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
    public Boat findById(Long id) {
        return boatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Barco no encontrado: " + id));
    }

    @Override
    public Boat findBySailNumber(String sailNumber) {
        return boatRepository.findBySailNumber(sailNumber)
                .orElseThrow(() -> new RuntimeException("Barco no encontrado: " + sailNumber));
    }

    @Override
    public List<Boat> findAll() {
        return boatRepository.findAll();
    }

    @Override
    public List<Boat> findByOwner(Long ownerId) {
        return boatRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Boat> findByClass(String boatClass) {
        return boatRepository.findByBoatClass(boatClass);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        boatRepository.deleteById(id);
        log.info("Barco eliminado: {}", id);
    }

    @Override
    public boolean existsBySailNumber(String sailNumber) {
        return boatRepository.existsBySailNumber(sailNumber);
    }
}

