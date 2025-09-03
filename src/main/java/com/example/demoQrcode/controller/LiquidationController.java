package com.example.demoQrcode.controller;

import com.example.demoQrcode.dto.QRGenerationRequest;
import com.example.demoQrcode.dto.QRGenerationResponse;
import com.example.demoQrcode.dto.QRImageResponse;
import com.example.demoQrcode.entity.Liquidation;
import com.example.demoQrcode.entity.LiquidationStatus;
import com.example.demoQrcode.service.LiquidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/liquidations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LiquidationController {

    private final LiquidationService liquidationService;

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
        return liquidationService.searchWithFilters(customerId, status, startDate, endDate, pageable);
    }

    // GET /api/liquidations/search?q={term}
    @GetMapping("/search")
    public Page<Liquidation> searchByTerm(
            @RequestParam(name = "q", required = false, defaultValue = "") String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return liquidationService.searchByTerm(term, pageable);
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

    // ========================================
    // NOUVEAUX ENDPOINTS QR CODES
    // ========================================
    // ENDPOINTS QR CODES SUPPRIMÉS - REMPLACÉS PAR LE WORKFLOW UEMOA
    // Utilisez /api/uemoa-workflow/generate pour la génération de QR codes
    // ========================================

    /**
     * GET /api/liquidations/{id}/qr-image
     * Récupère l'image QR d'une liquidation
     */
    @GetMapping("/{id}/qr-image")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<QRImageResponse> getQRImage(@PathVariable Long id) {
        try {
            log.info("Demande de récupération de l'image QR pour la liquidation ID: {}", id);

            // Récupération de la liquidation
            Optional<Liquidation> liquidationOpt = liquidationService.get(id);
            if (liquidationOpt.isEmpty()) {
                log.warn("Liquidation non trouvée avec l'ID: {}", id);
                return ResponseEntity.ok(QRImageResponse.notFound());
            }

            Liquidation liquidation = liquidationOpt.get();

            // Vérification de l'existence d'un QR code
            if (!liquidation.hasQrCode()) {
                log.warn("Aucun QR code généré pour la liquidation ID: {}", id);
                return ResponseEntity.ok(QRImageResponse.noQRCode());
            }

            // Construction de la réponse
            QRImageResponse response = QRImageResponse.success(
                    liquidation.getQrImageBase64(),
                    liquidation.getQrType(),
                    id,
                    liquidation.getTransactionId()
            );

            log.info("Image QR récupérée avec succès pour la liquidation ID: {}", id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'image QR pour la liquidation ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.ok(QRImageResponse.error(e.getMessage()));
        }
    }

    // ========================================
    // ENDPOINTS QR CODES SUPPRIMÉS - REMPLACÉS PAR LE WORKFLOW UEMOA
    // Utilisez /api/uemoa-workflow/generate pour la génération de QR codes
    // ========================================
}

