package com.evs.MicroserviceRegattaApi.Controller;

import com.evs.MicroserviceRegattaApi.Entities.Boat;
import com.evs.MicroserviceRegattaApi.Service.Inter.IBoatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boats")
@RequiredArgsConstructor
public class BoatController {

    private final IBoatService boatService;

    @PostMapping
    public ResponseEntity<Boat> create(@RequestBody Boat boat) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boatService.create(boat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boat> update(@PathVariable Long id, @RequestBody Boat boat) {
        return ResponseEntity.ok(boatService.update(id, boat));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Boat> findById(@PathVariable Long id) {
        return ResponseEntity.ok(boatService.findById(id));
    }

    @GetMapping("/sail/{sailNumber}")
    public ResponseEntity<Boat> findBySailNumber(@PathVariable String sailNumber) {
        return ResponseEntity.ok(boatService.findBySailNumber(sailNumber));
    }

    @GetMapping
    public ResponseEntity<List<Boat>> findAll() {
        return ResponseEntity.ok(boatService.findAll());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Boat>> findByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(boatService.findByOwner(ownerId));
    }

    @GetMapping("/class/{boatClass}")
    public ResponseEntity<List<Boat>> findByClass(@PathVariable String boatClass) {
        return ResponseEntity.ok(boatService.findByClass(boatClass));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boatService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

