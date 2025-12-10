package com.evs.MicroserviceRegattaApi.Controller;

import com.evs.MicroserviceRegattaApi.Entities.Regatta;
import com.evs.MicroserviceRegattaApi.Service.Inter.IRegattaService;
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

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Regatta> create(@RequestBody Regatta regatta, @RequestParam Long createdBy) {
        return ResponseEntity.status(HttpStatus.CREATED).body(regattaService.create(regatta, createdBy));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Regatta> update(@PathVariable Long id, @RequestBody Regatta regatta) {
        return ResponseEntity.ok(regattaService.update(id, regatta));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Regatta> findById(@PathVariable Long id) {
        return ResponseEntity.ok(regattaService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<Regatta>> findAll() {
        return ResponseEntity.ok(regattaService.findAll());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Regatta>> findUpcoming() {
        return ResponseEntity.ok(regattaService.findUpcoming());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Regatta>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(regattaService.findByStatus(status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        regattaService.delete(id);
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
    public ResponseEntity<Regatta> start(@PathVariable Long id) {
        return ResponseEntity.ok(regattaService.start(id));
    }

    @PostMapping("/{id}/finish")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Regatta> finish(@PathVariable Long id) {
        return ResponseEntity.ok(regattaService.finish(id));
    }

    @PostMapping("/{id}/staff")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    public ResponseEntity<Regatta> assignStaff(@PathVariable Long id, @RequestBody List<Long> staffIds) {
        return ResponseEntity.ok(regattaService.assignStaff(id, staffIds));
    }
}

