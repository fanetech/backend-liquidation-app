package com.example.demoQrcode;

import com.example.demoQrcode.dto.UemoaQRWorkflowRequest;
import com.example.demoQrcode.dto.UemoaQRWorkflowResponse;
import com.example.demoQrcode.dto.ClientInfoResponse;
import com.example.demoQrcode.service.UemoaWorkflowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test d'intégration pour le workflow UEMOA QR Code
 */
@SpringBootTest
@ActiveProfiles("test")
public class UemoaWorkflowIntegrationTest {

    @Autowired
    private UemoaWorkflowService uemoaWorkflowService;

    @Test
    public void testGenerateQRCodeAndWorkflow() {
        // Arrange
        UemoaQRWorkflowRequest request = UemoaQRWorkflowRequest.builder()
                .amount(new BigDecimal("5000")) // 50.00 XOF
                .merchantName("TEST MERCHANT")
                .clientInfo("123") // ID client
                .qrType("STATIC")
                .build();

        // Act
        UemoaQRWorkflowResponse response = uemoaWorkflowService.generateQRCodeAndWorkflow(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getQrCode());
        assertNotNull(response.getGeneratedLink());
        
        // Vérification des données du QR code
        UemoaQRWorkflowResponse.QRCodeData qrCode = response.getQrCode();
        assertEquals("STATIC", qrCode.getQrType());
        assertEquals(new BigDecimal("5000"), qrCode.getAmount());
        assertEquals("XOF", qrCode.getCurrency());
        assertEquals("TEST MERCHANT", qrCode.getMerchantName());
        assertEquals("CI", qrCode.getCountryCode());
        
        // Vérification du lien généré
        assertTrue(response.getGeneratedLink().contains("client-info/"));
    }

    @Test
    public void testGenerateDynamicQRCode() {
        // Arrange
        UemoaQRWorkflowRequest request = UemoaQRWorkflowRequest.builder()
                .amount(new BigDecimal("10000")) // 100.00 XOF
                .merchantName("DYNAMIC MERCHANT")
                .clientInfo("456") // ID client
                .qrType("DYNAMIC")
                .transactionReference("TXN-001")
                .build();

        // Act
        UemoaQRWorkflowResponse response = uemoaWorkflowService.generateQRCodeAndWorkflow(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("DYNAMIC", response.getQrCode().getQrType());
        assertEquals("TXN-001", response.getQrCode().getTransactionReference());
    }

    @Test
    public void testInvalidAmount() {
        // Arrange - Montant trop faible
        UemoaQRWorkflowRequest request = UemoaQRWorkflowRequest.builder()
                .amount(new BigDecimal("0"))
                .clientInfo("123")
                .build();

        // Act
        UemoaQRWorkflowResponse response = uemoaWorkflowService.generateQRCodeAndWorkflow(request);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("montant"));
    }

    @Test
    public void testInvalidQRType() {
        // Arrange - Type de QR non supporté
        UemoaQRWorkflowRequest request = UemoaQRWorkflowRequest.builder()
                .amount(new BigDecimal("5000"))
                .clientInfo("123")
                .qrType("INVALID_TYPE")
                .build();

        // Act
        UemoaQRWorkflowResponse response = uemoaWorkflowService.generateQRCodeAndWorkflow(request);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Type de QR code non supporté"));
    }

    @Test
    public void testWorkflowCompleteness() {
        // Arrange
        UemoaQRWorkflowRequest request = UemoaQRWorkflowRequest.builder()
                .amount(new BigDecimal("7500")) // 75.00 XOF
                .merchantName("COMPLETE WORKFLOW TEST")
                .clientInfo("789")
                .qrType("STATIC")
                .build();

        // Act - Génération du QR code et du workflow
        UemoaQRWorkflowResponse response = uemoaWorkflowService.generateQRCodeAndWorkflow(request);

        // Assert - Vérification que le workflow est complet
        assertNotNull(response);
        assertTrue(response.isSuccess());
        
        // Vérification que tous les composants du workflow sont présents
        assertNotNull(response.getQrCode().getQrData()); // Données EMVCo
        assertNotNull(response.getQrCode().getQrImage()); // Image Base64
        assertNotNull(response.getGeneratedLink()); // Lien généré
        
        // Vérification que le lien contient les informations nécessaires
        String link = response.getGeneratedLink();
        assertTrue(link.contains("client-info/"));
        
        // Simulation du décodage du lien (partie du workflow)
        String encodedPart = link.substring(link.lastIndexOf("/") + 1);
        assertNotNull(encodedPart);
        assertFalse(encodedPart.isEmpty());
    }
}
