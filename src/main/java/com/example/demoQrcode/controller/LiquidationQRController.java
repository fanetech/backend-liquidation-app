package com.example.demoQrcode.controller;

import com.example.demoQrcode.entity.Liquidation;
import com.example.demoQrcode.service.LiquidationQRService;
import com.example.demoQrcode.service.LiquidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur pour les fonctionnalités de QR codes liées aux liquidations
 */
@Slf4j
@RestController
@RequestMapping("/api/liquidations/{liquidationId}/qr")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LiquidationQRController {

    private final LiquidationQRService liquidationQRService;
    private final LiquidationService liquidationService;

    /**
     * Génère un QR code statique pour une liquidation
     * 
     * @param liquidationId ID de la liquidation
     * @return Données du QR code généré
     */
    @PostMapping("/static")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> generateStaticQR(@PathVariable Long liquidationId) {
        try {
            log.info("Demande de génération de QR code statique pour la liquidation ID: {}", liquidationId);
            
            // Récupération de la liquidation
            Optional<Liquidation> liquidationOpt = liquidationService.get(liquidationId);
            if (liquidationOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Liquidation liquidation = liquidationOpt.get();
            
            // Génération du QR code statique
            Map<String, Object> qrData = liquidationQRService.generateStaticQRForLiquidation(liquidation);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("qrData", qrData);
            response.put("message", "QR code statique généré avec succès pour la liquidation");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Erreur de validation pour la liquidation ID: {}: {}", liquidationId, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code statique pour la liquidation ID: {}: {}", 
                     liquidationId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Erreur interne lors de la génération du QR code");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Génère un QR code dynamique pour une liquidation
     * 
     * @param liquidationId ID de la liquidation
     * @param request Requête contenant la référence de transaction
     * @return Données du QR code généré
     */
    @PostMapping("/dynamic")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> generateDynamicQR(
            @PathVariable Long liquidationId,
            @RequestBody Map<String, Object> request) {
        try {
            log.info("Demande de génération de QR code dynamique pour la liquidation ID: {}", liquidationId);
            
            // Extraction de la référence de transaction
            String transactionReference = (String) request.get("transactionReference");
            if (transactionReference == null || transactionReference.trim().isEmpty()) {
                // Génération automatique d'une référence si non fournie
                Optional<Liquidation> liquidationOpt = liquidationService.get(liquidationId);
                if (liquidationOpt.isEmpty()) {
                    return ResponseEntity.notFound().build();
                }
                transactionReference = liquidationQRService.generateTransactionReference(liquidationOpt.get());
            }
            
            // Récupération de la liquidation
            Optional<Liquidation> liquidationOpt = liquidationService.get(liquidationId);
            if (liquidationOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Liquidation liquidation = liquidationOpt.get();
            
            // Génération du QR code dynamique
            Map<String, Object> qrData = liquidationQRService.generateDynamicQRForLiquidation(liquidation, transactionReference);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("qrData", qrData);
            response.put("message", "QR code dynamique généré avec succès pour la liquidation");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Erreur de validation pour la liquidation ID: {}: {}", liquidationId, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code dynamique pour la liquidation ID: {}: {}", 
                     liquidationId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Erreur interne lors de la génération du QR code");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Génère un QR code P2P pour une liquidation
     * 
     * @param liquidationId ID de la liquidation
     * @param request Requête contenant le numéro de téléphone du bénéficiaire
     * @return Données du QR code généré
     */
    @PostMapping("/p2p")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> generateP2PQR(
            @PathVariable Long liquidationId,
            @RequestBody Map<String, Object> request) {
        try {
            log.info("Demande de génération de QR code P2P pour la liquidation ID: {}", liquidationId);
            
            // Extraction du numéro de téléphone du bénéficiaire
            String beneficiaryPhone = (String) request.get("beneficiaryPhone");
            if (beneficiaryPhone == null || beneficiaryPhone.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Le numéro de téléphone du bénéficiaire est requis"));
            }
            
            // Récupération de la liquidation
            Optional<Liquidation> liquidationOpt = liquidationService.get(liquidationId);
            if (liquidationOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Liquidation liquidation = liquidationOpt.get();
            
            // Génération du QR code P2P
            Map<String, Object> qrData = liquidationQRService.generateP2PQRForLiquidation(liquidation, beneficiaryPhone);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("qrData", qrData);
            response.put("message", "QR code P2P généré avec succès pour la liquidation");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Erreur de validation pour la liquidation ID: {}: {}", liquidationId, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code P2P pour la liquidation ID: {}: {}", 
                     liquidationId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Erreur interne lors de la génération du QR code");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Génère un QR code avec pénalités pour une liquidation
     * 
     * @param liquidationId ID de la liquidation
     * @param request Requête contenant le montant des pénalités
     * @return Données du QR code généré
     */
    @PostMapping("/penalty")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> generateQRWithPenalty(
            @PathVariable Long liquidationId,
            @RequestBody Map<String, Object> request) {
        try {
            log.info("Demande de génération de QR code avec pénalités pour la liquidation ID: {}", liquidationId);
            
            // Extraction du montant des pénalités
            Object penaltyAmountObj = request.get("penaltyAmount");
            if (penaltyAmountObj == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Le montant des pénalités est requis"));
            }
            
            BigDecimal penaltyAmount;
            if (penaltyAmountObj instanceof Number) {
                penaltyAmount = new BigDecimal(penaltyAmountObj.toString());
            } else if (penaltyAmountObj instanceof String) {
                penaltyAmount = new BigDecimal((String) penaltyAmountObj);
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Format de montant des pénalités invalide"));
            }
            
            // Récupération de la liquidation
            Optional<Liquidation> liquidationOpt = liquidationService.get(liquidationId);
            if (liquidationOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Liquidation liquidation = liquidationOpt.get();
            
            // Génération du QR code avec pénalités
            Map<String, Object> qrData = liquidationQRService.generateQRWithPenalty(liquidation, penaltyAmount);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("qrData", qrData);
            response.put("message", "QR code avec pénalités généré avec succès pour la liquidation");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Erreur de validation pour la liquidation ID: {}: {}", liquidationId, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code avec pénalités pour la liquidation ID: {}: {}", 
                     liquidationId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Erreur interne lors de la génération du QR code");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Génère une référence de transaction pour une liquidation
     * 
     * @param liquidationId ID de la liquidation
     * @return Référence de transaction générée
     */
    @GetMapping("/reference")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> generateTransactionReference(@PathVariable Long liquidationId) {
        try {
            log.info("Demande de génération de référence de transaction pour la liquidation ID: {}", liquidationId);
            
            // Récupération de la liquidation
            Optional<Liquidation> liquidationOpt = liquidationService.get(liquidationId);
            if (liquidationOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Liquidation liquidation = liquidationOpt.get();
            
            // Génération de la référence de transaction
            String transactionReference = liquidationQRService.generateTransactionReference(liquidation);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transactionReference", transactionReference);
            response.put("liquidationId", liquidationId);
            response.put("message", "Référence de transaction générée avec succès");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération de la référence de transaction pour la liquidation ID: {}: {}", 
                     liquidationId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Erreur interne lors de la génération de la référence");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Valide une liquidation pour la génération de QR code
     * 
     * @param liquidationId ID de la liquidation
     * @return Statut de validation
     */
    @GetMapping("/validate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> validateLiquidationForQR(@PathVariable Long liquidationId) {
        try {
            log.info("Demande de validation pour la liquidation ID: {}", liquidationId);
            
            // Récupération de la liquidation
            Optional<Liquidation> liquidationOpt = liquidationService.get(liquidationId);
            if (liquidationOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Liquidation liquidation = liquidationOpt.get();
            
            // Validation de la liquidation
            boolean isValid = liquidationQRService.validateLiquidationForQR(liquidation);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isValid", isValid);
            response.put("liquidationId", liquidationId);
            response.put("message", isValid ? "Liquidation valide pour la génération de QR code" : "Liquidation invalide pour la génération de QR code");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la validation de la liquidation ID: {}: {}", liquidationId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Erreur interne lors de la validation");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
