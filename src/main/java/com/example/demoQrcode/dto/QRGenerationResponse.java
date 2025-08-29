package com.example.demoQrcode.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour les réponses de génération de QR codes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QRGenerationResponse {

    private boolean success;
    private String message;
    private String error;

    // Données du QR code
    private String qrCode;
    private String qrImageBase64;
    private String qrType;
    private String transactionId;
    private String merchantChannel;
    private LocalDateTime generatedAt;

    // Données de la liquidation
    private Long liquidationId;
    private String customerName;
    private BigDecimal amount;
    private BigDecimal penaltyAmount;
    private BigDecimal totalAmount;
    private String currency;
    private String taxType;

    // Méthodes de construction pour différents scénarios
    public static QRGenerationResponse success(String qrCode, String qrType, Long liquidationId, String customerName, BigDecimal amount) {
        return QRGenerationResponse.builder()
                .success(true)
                .message("QR code généré avec succès")
                .qrCode(qrCode)
                .qrType(qrType)
                .liquidationId(liquidationId)
                .customerName(customerName)
                .amount(amount)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    public static QRGenerationResponse error(String error) {
        return QRGenerationResponse.builder()
                .success(false)
                .message("Erreur lors de la génération du QR code")
                .error(error)
                .build();
    }

    public static QRGenerationResponse notFound() {
        return QRGenerationResponse.builder()
                .success(false)
                .message("Liquidation non trouvée")
                .error("Liquidation introuvable")
                .build();
    }

    public static QRGenerationResponse validationError(String error) {
        return QRGenerationResponse.builder()
                .success(false)
                .message("Erreur de validation")
                .error(error)
                .build();
    }
}
