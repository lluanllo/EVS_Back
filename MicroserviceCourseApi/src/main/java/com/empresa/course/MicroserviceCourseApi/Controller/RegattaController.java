package com.empresa.course.MicroserviceCourseApi.Controller;

import com.empresa.course.MicroserviceCourseApi.Entities.*;
import com.empresa.course.MicroserviceCourseApi.Service.Inter.IRegattaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regattas")
@RequiredArgsConstructor
public class RegattaController {

    private final IRegattaService regattaService;

    // ===== Regatta Management (BOSS only) =====

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Regatta> createRegatta(@RequestBody Regatta regatta, @RequestParam Long createdBy) {
        return ResponseEntity.status(HttpStatus.CREATED).body(regattaService.createRegatta(regatta, createdBy));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Regatta> updateRegatta(@PathVariable Long id, @RequestBody Regatta regatta) {
        return ResponseEntity.ok(regattaService.updateRegatta(id, regatta));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Regatta> getRegattaById(@PathVariable Long id) {
        return ResponseEntity.ok(regattaService.getRegattaById(id));
    }

    @GetMapping
    public ResponseEntity<List<Regatta>> getAllRegattas() {
        return ResponseEntity.ok(regattaService.getAllRegattas());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Regatta>> getUpcomingRegattas() {
        return ResponseEntity.ok(regattaService.getUpcomingRegattas());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Regatta>> getRegattasByStatus(@PathVariable String status) {
        return ResponseEntity.ok(regattaService.getRegattasByStatus(status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Void> deleteRegatta(@PathVariable Long id) {
        regattaService.deleteRegatta(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/open-registrations")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Regatta> openRegistrations(@PathVariable Long id) {
        return ResponseEntity.ok(regattaService.openRegistrations(id));
    }

    @PostMapping("/{id}/close-registrations")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Regatta> closeRegistrations(@PathVariable Long id) {
        return ResponseEntity.ok(regattaService.closeRegistrations(id));
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Regatta> startRegatta(@PathVariable Long id) {
        return ResponseEntity.ok(regattaService.startRegatta(id));
    }

    @PostMapping("/{id}/finish")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Regatta> finishRegatta(@PathVariable Long id) {
        return ResponseEntity.ok(regattaService.finishRegatta(id));
    }

    @PostMapping("/{id}/staff")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Regatta> assignStaff(@PathVariable Long id, @RequestBody List<Long> staffIds) {
        return ResponseEntity.ok(regattaService.assignStaff(id, staffIds));
    }

    // ===== Boat Management =====

    @PostMapping("/boats")
    public ResponseEntity<Boat> createBoat(@RequestBody Boat boat) {
        return ResponseEntity.status(HttpStatus.CREATED).body(regattaService.createBoat(boat));
    }

    @PutMapping("/boats/{id}")
    public ResponseEntity<Boat> updateBoat(@PathVariable Long id, @RequestBody Boat boat) {
        return ResponseEntity.ok(regattaService.updateBoat(id, boat));
    }

    @GetMapping("/boats/{id}")
    public ResponseEntity<Boat> getBoatById(@PathVariable Long id) {
        return ResponseEntity.ok(regattaService.getBoatById(id));
    }

    @GetMapping("/boats")
    public ResponseEntity<List<Boat>> getAllBoats() {
        return ResponseEntity.ok(regattaService.getAllBoats());
    }

    @GetMapping("/boats/owner/{ownerId}")
    public ResponseEntity<List<Boat>> getBoatsByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(regattaService.getBoatsByOwner(ownerId));
    }

    @DeleteMapping("/boats/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Void> deleteBoat(@PathVariable Long id) {
        regattaService.deleteBoat(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Participant Registration =====

    @PostMapping("/{regattaId}/participants")
    public ResponseEntity<RegattaParticipant> registerParticipant(
            @PathVariable Long regattaId,
            @RequestParam Long boatId,
            @RequestParam Long skipperId,
            @RequestParam(required = false) List<Long> crewIds,
            @RequestParam(required = false) List<String> crewNames) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(regattaService.registerParticipant(regattaId, boatId, skipperId, crewIds, crewNames));
    }

    @PostMapping("/participants/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<RegattaParticipant> confirmParticipant(@PathVariable Long id) {
        return ResponseEntity.ok(regattaService.confirmParticipant(id));
    }

    @PostMapping("/participants/{id}/withdraw")
    public ResponseEntity<RegattaParticipant> withdrawParticipant(@PathVariable Long id) {
        return ResponseEntity.ok(regattaService.withdrawParticipant(id));
    }

    @GetMapping("/{regattaId}/participants")
    public ResponseEntity<List<RegattaParticipant>> getParticipantsByRegatta(@PathVariable Long regattaId) {
        return ResponseEntity.ok(regattaService.getParticipantsByRegatta(regattaId));
    }

    @GetMapping("/{regattaId}/classification")
    public ResponseEntity<List<RegattaParticipant>> getClassification(@PathVariable Long regattaId) {
        return ResponseEntity.ok(regattaService.getClassification(regattaId));
    }

    // ===== Race Management =====

    @PostMapping("/{regattaId}/races")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Race> createRace(
            @PathVariable Long regattaId,
            @RequestParam Integer raceNumber) {
        return ResponseEntity.status(HttpStatus.CREATED).body(regattaService.createRace(regattaId, raceNumber));
    }

    @PostMapping("/races/{raceId}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Race> startRace(
            @PathVariable Long raceId,
            @RequestParam Integer windDirection,
            @RequestParam Double windSpeed) {
        return ResponseEntity.ok(regattaService.startRace(raceId, windDirection, windSpeed));
    }

    @PostMapping("/races/{raceId}/finish")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Race> finishRace(@PathVariable Long raceId) {
        return ResponseEntity.ok(regattaService.finishRace(raceId));
    }

    @GetMapping("/{regattaId}/races")
    public ResponseEntity<List<Race>> getRacesByRegatta(@PathVariable Long regattaId) {
        return ResponseEntity.ok(regattaService.getRacesByRegatta(regattaId));
    }

    // ===== Race Results =====

    @PostMapping("/races/{raceId}/results/finish")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<RaceResult> recordFinish(
            @PathVariable Long raceId,
            @RequestParam Long participantId,
            @RequestParam Long elapsedSeconds) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(regattaService.recordFinish(raceId, participantId, elapsedSeconds));
    }

    @PostMapping("/races/{raceId}/results/dnf")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<RaceResult> recordDNF(
            @PathVariable Long raceId,
            @RequestParam Long participantId,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(regattaService.recordDNF(raceId, participantId, reason));
    }

    @PostMapping("/races/{raceId}/results/dsq")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<RaceResult> recordDSQ(
            @PathVariable Long raceId,
            @RequestParam Long participantId,
            @RequestParam String reason) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(regattaService.recordDSQ(raceId, participantId, reason));
    }

    @GetMapping("/races/{raceId}/results")
    public ResponseEntity<List<RaceResult>> getRaceResults(@PathVariable Long raceId) {
        return ResponseEntity.ok(regattaService.getRaceResults(raceId));
    }
}

