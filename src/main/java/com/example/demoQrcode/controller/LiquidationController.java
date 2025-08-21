package com.example.demoQrcode.controller;

import com.example.demoQrcode.entity.Liquidation;
import com.example.demoQrcode.entity.LiquidationStatus;
import com.example.demoQrcode.service.LiquidationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/liquidations")
public class LiquidationController {

    @Autowired
    private LiquidationService liquidationService;

    // GET /api/liquidations (with filters)
    @GetMapping
    public Page<Liquidation> list(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) LiquidationStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return liquidationService.search(customerId, status, startDate, endDate, pageable);
    }

    // GET /api/liquidations/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        Optional<Liquidation> l = liquidationService.get(id);
        return l.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/liquidations
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Liquidation liquidation) {
        Liquidation created = liquidationService.create(liquidation);
        return ResponseEntity.created(URI.create("/api/liquidations/" + created.getId())).body(created);
    }

    // PUT /api/liquidations/{id}
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Liquidation liquidation) {
        return liquidationService.update(id, liquidation)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // PUT /api/liquidations/{id}/pay
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}/pay")
    public ResponseEntity<?> pay(@PathVariable Long id) {
        return liquidationService.markAsPaid(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /api/liquidations/customer/{customerId}
    @GetMapping("/customer/{customerId}")
    public List<Liquidation> byCustomer(@PathVariable Long customerId) {
        return liquidationService.findByCustomer(customerId);
    }

    // Helper endpoint: calcul de pénalité pour une liquidation donnée (dailyRate en décimal, ex: 0.01)
    @GetMapping("/{id}/penalty")
    public ResponseEntity<?> penalty(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0.0") BigDecimal dailyRate
    ) {
        Optional<Liquidation> l = liquidationService.get(id);
        if (l.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(liquidationService.calculatePenalty(l.get(), dailyRate));
    }
}


