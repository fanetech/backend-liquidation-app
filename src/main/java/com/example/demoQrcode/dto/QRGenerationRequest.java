package com.example.demoQrcode.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO pour les requêtes de génération de QR codes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRGenerationRequest {

    @NotNull(message = "Le type de QR code est obligatoire")
    @Pattern(regexp = "^(STATIC|DYNAMIC|P2P|PENALTY)$", message = "Le type de QR code doit être STATIC, DYNAMIC, P2P ou PENALTY")
    private String qrType;

    private String transactionReference; // Optionnel, utilisé pour les QR dynamiques

    private java.math.BigDecimal penaltyAmount; // Optionnel, utilisé pour les QR avec pénalités

    private String merchantChannel; // Optionnel, canal marchand spécifique

    // Constructeur pour QR statique
    public static QRGenerationRequest forStatic() {
        return new QRGenerationRequest("STATIC", null, null, null);
    }

    // Constructeur pour QR dynamique
    public static QRGenerationRequest forDynamic(String transactionReference) {
        return new QRGenerationRequest("DYNAMIC", transactionReference, null, null);
    }

    // Constructeur pour QR P2P
    public static QRGenerationRequest forP2P() {
        return new QRGenerationRequest("P2P", null, null, null);
    }

    // Constructeur pour QR avec pénalités
    public static QRGenerationRequest forPenalty(java.math.BigDecimal penaltyAmount) {
        return new QRGenerationRequest("PENALTY", null, penaltyAmount, null);
    }
}
