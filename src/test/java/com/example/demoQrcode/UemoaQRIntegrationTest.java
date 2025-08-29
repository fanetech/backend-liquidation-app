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
 * Tests d'intégration pour le module UEMOA QR Code
 */
@SpringBootTest
@ActiveProfiles("test")
class UemoaQRIntegrationTest {
    
    @Autowired
    private UemoaQRService uemoaQRService;
    
    @Autowired
    private UemoaQRIntegrationService uemoaQRIntegrationService;
    
    @Autowired
    private UemoaConfig uemoaConfig;
    
    @Test
    void testUemoaQRServiceInjection() {
        // Vérifier que le service UEMOA est injecté
        assertNotNull(uemoaQRService, "Le service UEMOA QR doit être injecté");
        assertNotNull(uemoaQRIntegrationService, "Le service d'intégration UEMOA QR doit être injecté");
        assertNotNull(uemoaConfig, "La configuration UEMOA doit être injectée");
    }
    
    @Test
    void testUemoaConfigValues() {
        // Vérifier les valeurs de configuration par défaut
        assertEquals("CI", uemoaConfig.getCountryCode(), "Le code pays doit être CI");
        assertEquals("XOF", uemoaConfig.getCurrency(), "La devise doit être XOF");
        assertEquals("LIQUIDATION APP", uemoaConfig.getMerchantName(), "Le nom du marchand doit être LIQUIDATION APP");
        assertEquals("Abidjan", uemoaConfig.getMerchantCity(), "La ville du marchand doit être Abidjan");
    }
    
    @Test
    void testGenerateStaticQR() {
        // Test de génération d'un QR code statique
        int amount = 1000; // 10 FCFA en centimes
        String merchantName = "TEST MERCHANT";
        
        Map<String, Object> qrData = uemoaQRIntegrationService.generateStaticQR(amount, merchantName);
        
        assertNotNull(qrData, "Les données du QR code ne doivent pas être nulles");
        assertEquals(merchantName, qrData.get("merchantName"), "Le nom du marchand doit correspondre");
        assertEquals("CI", qrData.get("countryCode"), "Le code pays doit être CI");
        assertEquals("XOF", qrData.get("currency"), "La devise doit être XOF");
        assertNotNull(qrData.get("qrCode"), "Le QR code doit être généré");
    }
    
    @Test
    void testGenerateDynamicQR() {
        // Test de génération d'un QR code dynamique
        int amount = 2000; // 20 FCFA en centimes
        String merchantName = "TEST MERCHANT";
        String reference = "REF-123456";
        
        Map<String, Object> qrData = uemoaQRIntegrationService.generateDynamicQR(amount, merchantName, reference);
        
        assertNotNull(qrData, "Les données du QR code ne doivent pas être nulles");
        assertEquals(merchantName, qrData.get("merchantName"), "Le nom du marchand doit correspondre");
        assertEquals("CI", qrData.get("countryCode"), "Le code pays doit être CI");
        assertEquals("XOF", qrData.get("currency"), "La devise doit être XOF");
        assertEquals(reference, qrData.get("reference"), "La référence doit correspondre");
        assertNotNull(qrData.get("qrCode"), "Le QR code doit être généré");
    }
    
    @Test
    void testGenerateTestQR() {
        // Test de génération d'un QR code de test
        Map<String, Object> qrData = uemoaQRIntegrationService.generateTestQR();
        
        assertNotNull(qrData, "Les données du QR code de test ne doivent pas être nulles");
        assertEquals("TEST MERCHANT", qrData.get("merchantName"), "Le nom du marchand de test doit correspondre");
        assertEquals("CI", qrData.get("countryCode"), "Le code pays doit être CI");
        assertEquals("XOF", qrData.get("currency"), "La devise doit être XOF");
        assertNotNull(qrData.get("qrCode"), "Le QR code de test doit être généré");
    }
    
    @Test
    void testAmountValidation() {
        // Test de validation des montants
        int invalidAmount = 0;
        
        assertThrows(IllegalArgumentException.class, () -> {
            uemoaQRIntegrationService.generateStaticQR(invalidAmount, "TEST");
        }, "Une exception doit être levée pour un montant invalide");
    }
    
    @Test
    void testParseQRCode() {
        // Test de parsing d'un QR code (utilise un QR code de test)
        Map<String, Object> originalQR = uemoaQRIntegrationService.generateTestQR();
        String qrCodeData = (String) originalQR.get("qrCode");
        
        Map<String, Object> parsedQR = uemoaQRIntegrationService.parseQRCode(qrCodeData);
        
        assertNotNull(parsedQR, "Les données parsées ne doivent pas être nulles");
        assertEquals(originalQR.get("merchantName"), parsedQR.get("merchantName"), "Le nom du marchand doit correspondre");
        assertEquals(originalQR.get("countryCode"), parsedQR.get("countryCode"), "Le code pays doit correspondre");
        assertEquals(originalQR.get("currency"), parsedQR.get("currency"), "La devise doit correspondre");
    }
}
