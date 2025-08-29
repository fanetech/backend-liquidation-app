package com.example.demoQrcode.controller;

import com.example.demoQrcode.service.UemoaQRIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour les fonctionnalités UEMOA QR Code
 */
@Slf4j
@RestController
@RequestMapping("/api/uemoa-qr")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UemoaQRController {
    
    private final UemoaQRIntegrationService uemoaQRService;
    
    /**
     * Génère un QR code statique
     * 
     * @param request Requête contenant le montant et le nom du marchand
     * @return Données du QR code généré
     */
    @PostMapping("/generate-static")
    public ResponseEntity<Map<String, Object>> generateStaticQR(@RequestBody Map<String, Object> request) {
        try {
            Integer amount = (Integer) request.get("amount");
            String merchantName = (String) request.get("merchantName");
            
            if (amount == null || amount <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le montant est requis et doit être positif"));
            }
            
            Map<String, Object> qrData = uemoaQRService.generateStaticQR(amount, merchantName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("qrData", qrData);
            response.put("message", "QR code statique généré avec succès");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code statique: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Génère un QR code dynamique
     * 
     * @param request Requête contenant le montant, le nom du marchand et la référence
     * @return Données du QR code généré
     */
    @PostMapping("/generate-dynamic")
    public ResponseEntity<Map<String, Object>> generateDynamicQR(@RequestBody Map<String, Object> request) {
        try {
            Integer amount = (Integer) request.get("amount");
            String merchantName = (String) request.get("merchantName");
            String reference = (String) request.get("reference");
            
            if (amount == null || amount <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le montant est requis et doit être positif"));
            }
            
            if (reference == null || reference.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "La référence de transaction est requise"));
            }
            
            Map<String, Object> qrData = uemoaQRService.generateDynamicQR(amount, merchantName, reference);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("qrData", qrData);
            response.put("message", "QR code dynamique généré avec succès");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code dynamique: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Parse un QR code UEMOA
     * 
     * @param request Requête contenant les données du QR code
     * @return Données de paiement parsées
     */
    @PostMapping("/parse")
    public ResponseEntity<Map<String, Object>> parseQRCode(@RequestBody Map<String, Object> request) {
        try {
            String qrData = (String) request.get("qrData");
            
            if (qrData == null || qrData.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Les données du QR code sont requises"));
            }
            
            Map<String, Object> parsedData = uemoaQRService.parseQRCode(qrData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("parsedData", parsedData);
            response.put("message", "QR code parsé avec succès");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors du parsing du QR code: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Génère un QR code de test
     * 
     * @return Données du QR code de test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> generateTestQR() {
        try {
            Map<String, Object> qrData = uemoaQRService.generateTestQR();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("qrData", qrData);
            response.put("message", "QR code de test généré avec succès");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code de test: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Endpoint de santé pour vérifier que le module UEMOA est chargé
     * 
     * @return Statut du module
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("module", "UEMOA QR Code Payment Module");
        response.put("version", "1.0.0");
        response.put("message", "Module UEMOA QR Code chargé avec succès");
        
        return ResponseEntity.ok(response);
    }
}
