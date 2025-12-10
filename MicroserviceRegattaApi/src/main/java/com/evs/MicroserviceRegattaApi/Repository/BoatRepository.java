package com.evs.MicroserviceRegattaApi.Repository;

import com.evs.MicroserviceRegattaApi.Entities.Boat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoatRepository extends JpaRepository<Boat, Long> {

    Optional<Boat> findBySailNumber(String sailNumber);

    List<Boat> findByOwnerId(Long ownerId);

    List<Boat> findByBoatClass(String boatClass);

    boolean existsBySailNumber(String sailNumber);
}

