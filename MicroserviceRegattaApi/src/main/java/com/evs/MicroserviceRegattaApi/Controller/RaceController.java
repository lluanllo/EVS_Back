package com.evs.MicroserviceRegattaApi.Controller;

import com.evs.MicroserviceRegattaApi.Entities.Race;
import com.evs.MicroserviceRegattaApi.Entities.RaceResult;
import com.evs.MicroserviceRegattaApi.Service.Inter.IRaceResultService;
import com.evs.MicroserviceRegattaApi.Service.Inter.IRaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/races")
@RequiredArgsConstructor
public class RaceController {

    private final IRaceService raceService;
    private final IRaceResultService resultService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Race> create(@RequestParam Long regattaId, @RequestParam Integer raceNumber) {
        return ResponseEntity.status(HttpStatus.CREATED).body(raceService.create(regattaId, raceNumber));
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Race> start(
            @PathVariable Long id,
            @RequestParam Integer windDirection,
            @RequestParam Double windSpeed) {
        return ResponseEntity.ok(raceService.start(id, windDirection, windSpeed));
    }

    @PostMapping("/{id}/finish")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Race> finish(@PathVariable Long id) {
        return ResponseEntity.ok(raceService.finish(id));
    }

    @PostMapping("/{id}/abandon")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Race> abandon(@PathVariable Long id, @RequestParam String reason) {
        return ResponseEntity.ok(raceService.abandon(id, reason));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Race> findById(@PathVariable Long id) {
        return ResponseEntity.ok(raceService.findById(id));
    }

    @GetMapping("/regatta/{regattaId}")
    public ResponseEntity<List<Race>> findByRegatta(@PathVariable Long regattaId) {
        return ResponseEntity.ok(raceService.findByRegatta(regattaId));
    }

    // === Results Endpoints ===

    @PostMapping("/{raceId}/results/finish")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<RaceResult> recordFinish(
            @PathVariable Long raceId,
            @RequestParam Long participantId,
            @RequestParam Long elapsedSeconds) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resultService.recordFinish(raceId, participantId, elapsedSeconds));
    }

    @PostMapping("/{raceId}/results/dnf")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<RaceResult> recordDNF(
            @PathVariable Long raceId,
            @RequestParam Long participantId,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resultService.recordDNF(raceId, participantId, reason));
    }

    @PostMapping("/{raceId}/results/dns")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<RaceResult> recordDNS(
            @PathVariable Long raceId,
            @RequestParam Long participantId,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resultService.recordDNS(raceId, participantId, reason));
    }

    @PostMapping("/{raceId}/results/dsq")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<RaceResult> recordDSQ(
            @PathVariable Long raceId,
            @RequestParam Long participantId,
            @RequestParam String reason) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resultService.recordDSQ(raceId, participantId, reason));
    }

    @PostMapping("/{raceId}/results/ocs")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<RaceResult> recordOCS(
            @PathVariable Long raceId,
            @RequestParam Long participantId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resultService.recordOCS(raceId, participantId));
    }

    @GetMapping("/{raceId}/results")
    public ResponseEntity<List<RaceResult>> getResults(@PathVariable Long raceId) {
        return ResponseEntity.ok(resultService.findByRace(raceId));
    }
}

