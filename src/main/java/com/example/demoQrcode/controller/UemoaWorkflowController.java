package com.example.demoQrcode.controller;

import com.example.demoQrcode.dto.UemoaQRWorkflowRequest;
import com.example.demoQrcode.dto.UemoaQRWorkflowResponse;
import com.example.demoQrcode.dto.ClientInfoResponse;
import com.example.demoQrcode.service.UemoaWorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * Contrôleur principal pour le workflow UEMOA QR Code
 * Implémente le workflow : QR → Scan → Link → API → Client Info
 */
@Slf4j
@RestController
@RequestMapping("/api/uemoa-workflow")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UemoaWorkflowController {
    
    private final UemoaWorkflowService uemoaWorkflowService;
    
    /**
     * POST /api/uemoa-workflow/generate
     * Génère un QR code UEMOA et prépare le workflow complet
     * 
     * @param request Requête de génération
     * @return Réponse avec QR code et lien généré
     */
    @PostMapping("/generate")
    public ResponseEntity<UemoaQRWorkflowResponse> generateQRCodeAndWorkflow(
            @Valid @RequestBody UemoaQRWorkflowRequest request
    ) {
        try {
            log.info("Demande de génération du workflow UEMOA pour le montant: {} centimes", request.getAmount());
            
            UemoaQRWorkflowResponse response = uemoaWorkflowService.generateQRCodeAndWorkflow(request);
            
            if (response.isSuccess()) {
                log.info("Workflow UEMOA généré avec succès");
                return ResponseEntity.ok(response);
            } else {
                log.warn("Échec de la génération du workflow UEMOA: {}", response.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du workflow UEMOA: {}", e.getMessage(), e);
            
            UemoaQRWorkflowResponse errorResponse = UemoaQRWorkflowResponse.error(
                    "Erreur lors de la génération: " + e.getMessage()
            );
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * GET /api/uemoa-workflow/client-info/{encodedClientInfo}
     * Récupère les informations client à partir du lien généré
     * Cet endpoint est déclenché quand l'utilisateur clique sur le lien
     * 
     * @param encodedClientInfo Informations client encodées dans l'URL
     * @return Réponse avec informations client
     */
    @GetMapping("/client-info/{encodedClientInfo}")
    public ResponseEntity<ClientInfoResponse> getClientInfoFromLink(
            @PathVariable String encodedClientInfo
    ) {
        try {
            log.info("Demande de récupération des informations client depuis le lien");
            
            ClientInfoResponse response = uemoaWorkflowService.getClientInfoFromLink(encodedClientInfo);
            
            if (response.isSuccess()) {
                log.info("Informations client récupérées avec succès");
                return ResponseEntity.ok(response);
            } else {
                log.warn("Échec de la récupération des informations client: {}", response.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des informations client: {}", e.getMessage(), e);
            
            ClientInfoResponse errorResponse = ClientInfoResponse.error(
                    "Erreur lors de la récupération: " + e.getMessage()
            );
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * GET /api/uemoa-workflow/health
     * Endpoint de santé pour vérifier que le workflow UEMOA est opérationnel
     * 
     * @return Statut du workflow
     */
    @GetMapping("/health")
    public ResponseEntity<Object> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "module", "UEMOA QR Code Workflow",
                "version", "1.0.0",
                "workflow", "QR → Scan → Link → API → Client Info",
                "message", "Workflow UEMOA opérationnel"
        ));
    }
    
    /**
     * GET /api/uemoa-workflow/status
     * Endpoint pour vérifier le statut du workflow et des composants UEMOA
     * 
     * @return Statut détaillé du workflow
     */
    @GetMapping("/status")
    public ResponseEntity<Object> status() {
        return ResponseEntity.ok(Map.of(
                "status", "OPERATIONAL",
                "uemoaModule", "LOADED",
                "qrGeneration", "AVAILABLE",
                "workflowSteps", Map.of(
                        "step1", "QR Code Generation",
                        "step2", "QR Code Scanning",
                        "step3", "Link Generation",
                        "step4", "Link Click",
                        "step5", "API Call",
                        "step6", "Client Info Display"
                ),
                "compliance", "UEMOA Standards",
                "timestamp", java.time.LocalDateTime.now()
        ));
    }
}
