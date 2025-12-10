package com.evs.MicroserviceRegattaApi.Controller;

import com.evs.MicroserviceRegattaApi.Entities.RegattaParticipant;
import com.evs.MicroserviceRegattaApi.Service.Inter.IParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final IParticipantService participantService;

    @PostMapping("/register")
    public ResponseEntity<RegattaParticipant> register(
            @RequestParam Long regattaId,
            @RequestParam Long boatId,
            @RequestParam Long skipperId,
            @RequestParam(required = false) List<Long> crewIds,
            @RequestParam(required = false) List<String> crewNames) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(participantService.register(regattaId, boatId, skipperId, crewIds, crewNames));
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<RegattaParticipant> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(participantService.confirm(id));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<RegattaParticipant> withdraw(@PathVariable Long id) {
        return ResponseEntity.ok(participantService.withdraw(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegattaParticipant> findById(@PathVariable Long id) {
        return ResponseEntity.ok(participantService.findById(id));
    }

    @GetMapping("/regatta/{regattaId}")
    public ResponseEntity<List<RegattaParticipant>> findByRegatta(@PathVariable Long regattaId) {
        return ResponseEntity.ok(participantService.findByRegatta(regattaId));
    }

    @GetMapping("/skipper/{skipperId}")
    public ResponseEntity<List<RegattaParticipant>> findBySkipper(@PathVariable Long skipperId) {
        return ResponseEntity.ok(participantService.findBySkipper(skipperId));
    }

    @GetMapping("/regatta/{regattaId}/classification")
    public ResponseEntity<List<RegattaParticipant>> getClassification(@PathVariable Long regattaId) {
        return ResponseEntity.ok(participantService.getClassification(regattaId));
    }
}

