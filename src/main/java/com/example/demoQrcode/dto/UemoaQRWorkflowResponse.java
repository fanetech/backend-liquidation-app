package com.example.demoQrcode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour les réponses du workflow UEMOA QR Code
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UemoaQRWorkflowResponse {
    
    /**
     * Statut de la génération
     */
    private boolean success;
    
    /**
     * Message de statut
     */
    private String message;
    
    /**
     * Données du QR code généré
     */
    private QRCodeData qrCode;
    
    /**
     * Lien généré après scan du QR code
     */
    private String generatedLink;
    
    /**
     * Données du QR code
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QRCodeData {
        /**
         * Données EMVCo du QR code
         */
        private String qrData;
        
        /**
         * Image QR code en Base64
         */
        private String qrImage;
        
        /**
         * Type de QR code
         */
        private String qrType;
        
        /**
         * Montant de la transaction
         */
        private BigDecimal amount;
        
        /**
         * Devise
         */
        private String currency;
        
        /**
         * Nom du marchand
         */
        private String merchantName;
        
        /**
         * Ville du marchand
         */
        private String merchantCity;
        
        /**
         * Code pays
         */
        private String countryCode;
        
        /**
         * Référence de transaction (pour QR dynamique)
         */
        private String transactionReference;
        
        /**
         * Horodatage de génération
         */
        private LocalDateTime generatedAt;
    }
    
    /**
     * Méthode de construction pour succès
     */
    public static UemoaQRWorkflowResponse success(String message, QRCodeData qrCode, String generatedLink) {
        return UemoaQRWorkflowResponse.builder()
                .success(true)
                .message(message)
                .qrCode(qrCode)
                .generatedLink(generatedLink)
                .build();
    }
    
    /**
     * Méthode de construction pour erreur
     */
    public static UemoaQRWorkflowResponse error(String message) {
        return UemoaQRWorkflowResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
