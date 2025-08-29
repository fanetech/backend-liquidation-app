package com.example.demoQrcode;

import com.aveplus.uemoa.qr.service.UemoaQRService;
import com.example.demoQrcode.config.UemoaConfig;
import com.example.demoQrcode.service.UemoaQRIntegrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test simple pour vérifier l'intégration du module UEMOA QR Code
 */
@SpringBootTest
@ActiveProfiles("test")
class UemoaQRModuleTest {
    
    @Autowired(required = false)
    private UemoaQRService uemoaQRService;
    
    @Autowired(required = false)
    private UemoaQRIntegrationService uemoaQRIntegrationService;
    
    @Autowired(required = false)
    private UemoaConfig uemoaConfig;
    
    @Test
    void testModuleIntegration() {
        // Vérifier que le module UEMOA est chargé
        assertNotNull(uemoaQRService, "Le service UEMOA QR doit être injecté");
        assertNotNull(uemoaQRIntegrationService, "Le service d'intégration UEMOA QR doit être injecté");
        assertNotNull(uemoaConfig, "La configuration UEMOA doit être injectée");
        
        System.out.println("✅ Module UEMOA QR Code intégré avec succès");
        System.out.println("✅ Service UEMOA QR: " + uemoaQRService.getClass().getName());
        System.out.println("✅ Service d'intégration: " + uemoaQRIntegrationService.getClass().getName());
        System.out.println("✅ Configuration: " + uemoaConfig.getClass().getName());
    }
    
    @Test
    void testConfigurationValues() {
        // Vérifier les valeurs de configuration
        assertEquals("CI", uemoaConfig.getCountryCode(), "Le code pays doit être CI");
        assertEquals("XOF", uemoaConfig.getCurrency(), "La devise doit être XOF");
        assertEquals("LIQUIDATION APP", uemoaConfig.getMerchantName(), "Le nom du marchand doit être LIQUIDATION APP");
        
        System.out.println("✅ Configuration UEMOA correcte:");
        System.out.println("   - Code pays: " + uemoaConfig.getCountryCode());
        System.out.println("   - Devise: " + uemoaConfig.getCurrency());
        System.out.println("   - Nom marchand: " + uemoaConfig.getMerchantName());
        System.out.println("   - Ville: " + uemoaConfig.getMerchantCity());
    }
    
    @Test
    void testQRGeneration() {
        // Test de génération d'un QR code simple
        try {
            Map<String, Object> qrData = uemoaQRIntegrationService.generateTestQR();
            
            assertNotNull(qrData, "Les données du QR code ne doivent pas être nulles");
            assertNotNull(qrData.get("qrCode"), "Le QR code doit être généré");
            assertEquals("TEST MERCHANT", qrData.get("merchantName"), "Le nom du marchand doit correspondre");
            
            System.out.println("✅ Génération de QR code réussie:");
            System.out.println("   - Marchand: " + qrData.get("merchantName"));
            System.out.println("   - Montant: " + qrData.get("amount"));
            System.out.println("   - Devise: " + qrData.get("currency"));
            System.out.println("   - QR Code: " + qrData.get("qrCode").toString().substring(0, 50) + "...");
            
        } catch (Exception e) {
            fail("La génération de QR code a échoué: " + e.getMessage());
        }
    }
}
