package com.example.demoQrcode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO pour les requêtes du workflow UEMOA QR Code
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UemoaQRWorkflowRequest {
    
    /**
     * Montant de la transaction en centimes
     */
    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "1.0", message = "Le montant doit être supérieur à 0")
    private BigDecimal amount;
    
    /**
     * Nom du marchand (optionnel, utilise la valeur par défaut si non fourni)
     */
    private String merchantName;
    
    /**
     * Informations du client à encoder dans le lien
     */
    @NotBlank(message = "Les informations client sont obligatoires")
    private String clientInfo;
    
    /**
     * Type de QR code à générer
     */
    @Builder.Default
    private String qrType = "STATIC";
    
    /**
     * Référence de transaction (pour QR dynamique)
     */
    private String transactionReference;
}
