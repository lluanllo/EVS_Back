package com.evs.MicroserviceRegattaApi.Repository;

import com.evs.MicroserviceRegattaApi.Entities.Regatta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegattaRepository extends JpaRepository<Regatta, Long> {

    List<Regatta> findByStatus(String status);

    List<Regatta> findByEventDateAfterOrderByEventDateAsc(LocalDate date);

    List<Regatta> findByEventDateBetween(LocalDate start, LocalDate end);

    List<Regatta> findByCreatedBy(Long userId);
}

