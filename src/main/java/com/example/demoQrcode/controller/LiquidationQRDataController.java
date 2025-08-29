package com.example.demoQrcode.controller;

import com.example.demoQrcode.entity.Liquidation;
import com.example.demoQrcode.entity.LiquidationStatus;
import com.example.demoQrcode.service.LiquidationQRDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des données QR codes des liquidations
 */
@Slf4j
@RestController
@RequestMapping("/api/liquidations/qr-data")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LiquidationQRDataController {

    private final LiquidationQRDataService liquidationQRDataService;

    // --- ENDPOINTS DE RECHERCHE ---

    /**
     * GET /api/liquidations/qr-data/with-qr
     * Trouve toutes les liquidations qui ont un QR code généré
     */
    @GetMapping("/with-qr")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getLiquidationsWithQrCode() {
        try {
            List<Liquidation> liquidations = liquidationQRDataService.findLiquidationsWithQrCode();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", liquidations);
            response.put("count", liquidations.size());
            response.put("message", "Liquidations avec QR code récupérées avec succès");
            
            log.info("Récupération de {} liquidations avec QR code", liquidations.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des liquidations avec QR code: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des liquidations avec QR code");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/liquidations/qr-data/without-qr
     * Trouve toutes les liquidations qui n'ont pas de QR code généré
     */
    @GetMapping("/without-qr")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getLiquidationsWithoutQrCode() {
        try {
            List<Liquidation> liquidations = liquidationQRDataService.findLiquidationsWithoutQrCode();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", liquidations);
            response.put("count", liquidations.size());
            response.put("message", "Liquidations sans QR code récupérées avec succès");
            
            log.info("Récupération de {} liquidations sans QR code", liquidations.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des liquidations sans QR code: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des liquidations sans QR code");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/liquidations/qr-data/type/{qrType}
     * Trouve toutes les liquidations par type de QR code
     */
    @GetMapping("/type/{qrType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getLiquidationsByQrType(@PathVariable String qrType) {
        try {
            List<Liquidation> liquidations = liquidationQRDataService.findByQrType(qrType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", liquidations);
            response.put("count", liquidations.size());
            response.put("qrType", qrType);
            response.put("message", "Liquidations par type de QR récupérées avec succès");
            
            log.info("Récupération de {} liquidations avec type de QR: {}", liquidations.size(), qrType);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des liquidations par type de QR {}: {}", qrType, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des liquidations par type de QR");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/liquidations/qr-data/transaction/{transactionId}
     * Trouve une liquidation par identifiant de transaction
     */
    @GetMapping("/transaction/{transactionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getLiquidationByTransactionId(@PathVariable String transactionId) {
        try {
            Optional<Liquidation> liquidation = liquidationQRDataService.findByTransactionId(transactionId);
            
            Map<String, Object> response = new HashMap<>();
            if (liquidation.isPresent()) {
                response.put("success", true);
                response.put("data", liquidation.get());
                response.put("message", "Liquidation trouvée avec succès");
                log.info("Liquidation trouvée pour le transaction ID: {}", transactionId);
            } else {
                response.put("success", false);
                response.put("message", "Aucune liquidation trouvée pour ce transaction ID");
                log.warn("Aucune liquidation trouvée pour le transaction ID: {}", transactionId);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de liquidation par transaction ID {}: {}", transactionId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la recherche de liquidation par transaction ID");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/liquidations/qr-data/customer/{customerId}
     * Trouve toutes les liquidations avec QR code par client
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getLiquidationsWithQrCodeByCustomerId(@PathVariable Long customerId) {
        try {
            List<Liquidation> liquidations = liquidationQRDataService.findLiquidationsWithQrCodeByCustomerId(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", liquidations);
            response.put("count", liquidations.size());
            response.put("customerId", customerId);
            response.put("message", "Liquidations avec QR code du client récupérées avec succès");
            
            log.info("Récupération de {} liquidations avec QR code pour le client ID: {}", liquidations.size(), customerId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des liquidations avec QR code du client {}: {}", customerId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des liquidations avec QR code du client");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/liquidations/qr-data/status/{status}
     * Trouve toutes les liquidations avec QR code par statut
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getLiquidationsWithQrCodeByStatus(@PathVariable LiquidationStatus status) {
        try {
            List<Liquidation> liquidations = liquidationQRDataService.findLiquidationsWithQrCodeByStatus(status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", liquidations);
            response.put("count", liquidations.size());
            response.put("status", status);
            response.put("message", "Liquidations avec QR code par statut récupérées avec succès");
            
            log.info("Récupération de {} liquidations avec QR code pour le statut: {}", liquidations.size(), status);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des liquidations avec QR code par statut {}: {}", status, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des liquidations avec QR code par statut");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/liquidations/qr-data/today
     * Trouve toutes les liquidations avec QR code généré aujourd'hui
     */
    @GetMapping("/today")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getLiquidationsWithQrCodeGeneratedToday() {
        try {
            List<Liquidation> liquidations = liquidationQRDataService.findLiquidationsWithQrCodeGeneratedToday();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", liquidations);
            response.put("count", liquidations.size());
            response.put("date", LocalDateTime.now().toLocalDate());
            response.put("message", "Liquidations avec QR code généré aujourd'hui récupérées avec succès");
            
            log.info("Récupération de {} liquidations avec QR code généré aujourd'hui", liquidations.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des liquidations avec QR code généré aujourd'hui: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des liquidations avec QR code généré aujourd'hui");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/liquidations/qr-data/this-week
     * Trouve toutes les liquidations avec QR code généré cette semaine
     */
    @GetMapping("/this-week")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getLiquidationsWithQrCodeGeneratedThisWeek() {
        try {
            List<Liquidation> liquidations = liquidationQRDataService.findLiquidationsWithQrCodeGeneratedThisWeek();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", liquidations);
            response.put("count", liquidations.size());
            response.put("period", "this-week");
            response.put("message", "Liquidations avec QR code généré cette semaine récupérées avec succès");
            
            log.info("Récupération de {} liquidations avec QR code généré cette semaine", liquidations.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des liquidations avec QR code généré cette semaine: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des liquidations avec QR code généré cette semaine");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/liquidations/qr-data/this-month
     * Trouve toutes les liquidations avec QR code généré ce mois
     */
    @GetMapping("/this-month")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getLiquidationsWithQrCodeGeneratedThisMonth() {
        try {
            List<Liquidation> liquidations = liquidationQRDataService.findLiquidationsWithQrCodeGeneratedThisMonth();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", liquidations);
            response.put("count", liquidations.size());
            response.put("period", "this-month");
            response.put("message", "Liquidations avec QR code généré ce mois récupérées avec succès");
            
            log.info("Récupération de {} liquidations avec QR code généré ce mois", liquidations.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des liquidations avec QR code généré ce mois: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des liquidations avec QR code généré ce mois");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/liquidations/qr-data/with-penalties
     * Trouve toutes les liquidations avec pénalités
     */
    @GetMapping("/with-penalties")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getLiquidationsWithPenalties() {
        try {
            List<Liquidation> liquidations = liquidationQRDataService.findLiquidationsWithPenalties();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", liquidations);
            response.put("count", liquidations.size());
            response.put("message", "Liquidations avec pénalités récupérées avec succès");
            
            log.info("Récupération de {} liquidations avec pénalités", liquidations.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des liquidations avec pénalités: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des liquidations avec pénalités");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // --- ENDPOINTS DE STATISTIQUES ---

    /**
     * GET /api/liquidations/qr-data/stats/count-by-type
     * Compte le nombre de liquidations avec QR code par type
     */
    @GetMapping("/stats/count-by-type")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getCountByQrType() {
        try {
            Map<String, Long> counts = liquidationQRDataService.countLiquidationsByQrType();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", counts);
            response.put("message", "Statistiques par type de QR récupérées avec succès");
            
            log.info("Statistiques par type de QR récupérées: {}", counts);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques par type de QR: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des statistiques par type de QR");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/liquidations/qr-data/stats/total-amount
     * Calcule le montant total des liquidations avec QR code
     */
    @GetMapping("/stats/total-amount")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getTotalAmountOfLiquidationsWithQrCode() {
        try {
            BigDecimal totalAmount = liquidationQRDataService.calculateTotalAmountOfLiquidationsWithQrCode();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalAmount", totalAmount);
            response.put("message", "Montant total des liquidations avec QR code calculé avec succès");
            
            log.info("Montant total des liquidations avec QR code: {}", totalAmount);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul du montant total des liquidations avec QR code: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors du calcul du montant total des liquidations avec QR code");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/liquidations/qr-data/stats/total-penalties
     * Calcule le montant total des pénalités
     */
    @GetMapping("/stats/total-penalties")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getTotalPenaltyAmount() {
        try {
            BigDecimal totalPenaltyAmount = liquidationQRDataService.calculateTotalPenaltyAmount();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalPenaltyAmount", totalPenaltyAmount);
            response.put("message", "Montant total des pénalités calculé avec succès");
            
            log.info("Montant total des pénalités: {}", totalPenaltyAmount);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul du montant total des pénalités: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors du calcul du montant total des pénalités");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // --- ENDPOINTS DE MAINTENANCE ---

    /**
     * DELETE /api/liquidations/qr-data/{liquidationId}
     * Supprime les données QR d'une liquidation
     */
    @DeleteMapping("/{liquidationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> removeQrCodeData(@PathVariable Long liquidationId) {
        try {
            liquidationQRDataService.removeQrCodeData(liquidationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("liquidationId", liquidationId);
            response.put("message", "Données QR supprimées avec succès");
            
            log.info("Données QR supprimées pour la liquidation ID: {}", liquidationId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la suppression des données QR pour la liquidation {}: {}", liquidationId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la suppression des données QR");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * DELETE /api/liquidations/qr-data/customer/{customerId}
     * Supprime les données QR de toutes les liquidations d'un client
     */
    @DeleteMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> removeQrCodeDataByCustomerId(@PathVariable Long customerId) {
        try {
            liquidationQRDataService.removeQrCodeDataByCustomerId(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customerId", customerId);
            response.put("message", "Données QR supprimées pour toutes les liquidations du client");
            
            log.info("Données QR supprimées pour toutes les liquidations du client ID: {}", customerId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la suppression des données QR pour le client {}: {}", customerId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la suppression des données QR du client");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * PUT /api/liquidations/qr-data/{liquidationId}/update-total
     * Met à jour le montant total d'une liquidation
     */
    @PutMapping("/{liquidationId}/update-total")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> updateTotalAmount(@PathVariable Long liquidationId) {
        try {
            liquidationQRDataService.updateTotalAmount(liquidationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("liquidationId", liquidationId);
            response.put("message", "Montant total mis à jour avec succès");
            
            log.info("Montant total mis à jour pour la liquidation ID: {}", liquidationId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du montant total pour la liquidation {}: {}", liquidationId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la mise à jour du montant total");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * PUT /api/liquidations/qr-data/update-all-totals
     * Met à jour le montant total de toutes les liquidations
     */
    @PutMapping("/update-all-totals")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateAllTotalAmounts() {
        try {
            liquidationQRDataService.updateAllTotalAmounts();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Montants totaux de toutes les liquidations mis à jour avec succès");
            
            log.info("Montants totaux de toutes les liquidations mis à jour");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour des montants totaux: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la mise à jour des montants totaux");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // --- ENDPOINTS DE VALIDATION ---

    /**
     * GET /api/liquidations/qr-data/{liquidationId}/validate
     * Vérifie si une liquidation a un QR code valide
     */
    @GetMapping("/{liquidationId}/validate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> validateQrCode(@PathVariable Long liquidationId) {
        try {
            boolean isValid = liquidationQRDataService.hasValidQrCode(liquidationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("liquidationId", liquidationId);
            response.put("isValid", isValid);
            response.put("message", isValid ? "QR code valide" : "QR code invalide ou manquant");
            
            log.info("Validation du QR code pour la liquidation ID {}: {}", liquidationId, isValid);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la validation du QR code pour la liquidation {}: {}", liquidationId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la validation du QR code");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/liquidations/qr-data/transaction-exists/{transactionId}
     * Vérifie si une transaction ID existe déjà
     */
    @GetMapping("/transaction-exists/{transactionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> checkTransactionIdExists(@PathVariable String transactionId) {
        try {
            boolean exists = liquidationQRDataService.transactionIdExists(transactionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transactionId", transactionId);
            response.put("exists", exists);
            response.put("message", exists ? "Transaction ID existe déjà" : "Transaction ID disponible");
            
            log.info("Vérification de l'existence du transaction ID {}: {}", transactionId, exists);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de l'existence du transaction ID {}: {}", transactionId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la vérification de l'existence du transaction ID");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
