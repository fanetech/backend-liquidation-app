package com.example.demoQrcode.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO pour les réponses d'images QR
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QRImageResponse {

    private boolean success;
    private String message;
    private String error;

    // Données de l'image
    private String qrImageBase64;
    private String contentType;
    private int imageSize;
    private String qrType;
    private LocalDateTime generatedAt;

    // Métadonnées
    private Long liquidationId;
    private String transactionId;

    // Méthodes de construction
    public static QRImageResponse success(String qrImageBase64, String qrType, Long liquidationId, String transactionId) {
        return QRImageResponse.builder()
                .success(true)
                .message("Image QR récupérée avec succès")
                .qrImageBase64(qrImageBase64)
                .contentType("image/png")
                .imageSize(qrImageBase64 != null ? qrImageBase64.length() : 0)
                .qrType(qrType)
                .generatedAt(LocalDateTime.now())
                .liquidationId(liquidationId)
                .transactionId(transactionId)
                .build();
    }

    public static QRImageResponse notFound() {
        return QRImageResponse.builder()
                .success(false)
                .message("Image QR non trouvée")
                .error("Aucune image QR disponible pour cette liquidation")
                .build();
    }

    public static QRImageResponse error(String error) {
        return QRImageResponse.builder()
                .success(false)
                .message("Erreur lors de la récupération de l'image QR")
                .error(error)
                .build();
    }

    public static QRImageResponse noQRCode() {
        return QRImageResponse.builder()
                .success(false)
                .message("Aucun QR code généré")
                .error("Cette liquidation n'a pas de QR code généré")
                .build();
    }
}
