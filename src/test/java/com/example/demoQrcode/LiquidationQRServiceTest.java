package com.example.demoQrcode;

import com.aveplus.uemoa.qr.model.QRPaymentData;
import com.aveplus.uemoa.qr.model.MerchantInfo;
import com.aveplus.uemoa.qr.service.UemoaQRService;
import com.example.demoQrcode.config.UemoaConfig;
import com.example.demoQrcode.entity.Customer;
import com.example.demoQrcode.entity.Liquidation;
import com.example.demoQrcode.entity.LiquidationStatus;
import com.example.demoQrcode.service.LiquidationQRService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour le service LiquidationQRService
 */
@SpringBootTest
@ActiveProfiles("test")
class LiquidationQRServiceTest {

    @Autowired
    private LiquidationQRService liquidationQRService;

    @Autowired
    private UemoaQRService uemoaQRService;

    @Autowired
    private UemoaConfig uemoaConfig;

    private Customer testCustomer;
    private Liquidation testLiquidation;

    @BeforeEach
    void setUp() {
        // Création d'un client de test
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFirstName("Jean");
        testCustomer.setLastName("Dupont");
        testCustomer.setAddress("123 Rue de la Paix, Abidjan, Côte d'Ivoire");
        testCustomer.setIfu("IFU123456789");
        testCustomer.setPhone("+2250701234567");
        testCustomer.setEmail("jean.dupont@example.com");

        // Création d'une liquidation de test
        testLiquidation = new Liquidation();
        testLiquidation.setId(1L);
        testLiquidation.setCustomer(testCustomer);
        testLiquidation.setTaxType("TVA");
        testLiquidation.setAmount(new BigDecimal("50000.00"));
        testLiquidation.setIssueDate(LocalDate.now());
        testLiquidation.setDueDate(LocalDate.now().plusDays(30));
        testLiquidation.setStatus(LiquidationStatus.PENDING);
    }

    @Test
    void testLiquidationQRServiceInjection() {
        // Vérifier que le service est injecté
        assertNotNull(liquidationQRService, "Le service LiquidationQRService doit être injecté");
        assertNotNull(uemoaQRService, "Le service UEMOA QR doit être injecté");
        assertNotNull(uemoaConfig, "La configuration UEMOA doit être injectée");
        
        System.out.println("✅ Service LiquidationQRService injecté avec succès");
    }

    @Test
    void testValidateLiquidationForQR() {
        // Test avec une liquidation valide
        boolean isValid = liquidationQRService.validateLiquidationForQR(testLiquidation);
        assertTrue(isValid, "La liquidation de test doit être valide");
        
        System.out.println("✅ Validation de liquidation réussie");
    }

    @Test
    void testValidateLiquidationForQR_InvalidLiquidation() {
        // Test avec une liquidation invalide (déjà payée)
        testLiquidation.setStatus(LiquidationStatus.PAID);
        boolean isValid = liquidationQRService.validateLiquidationForQR(testLiquidation);
        assertFalse(isValid, "Une liquidation déjà payée doit être invalide");
        
        System.out.println("✅ Validation de liquidation invalide correcte");
    }

    @Test
    void testMapCustomerToMerchantInfo() {
        // Test du mapping Customer vers MerchantInfo
        MerchantInfo merchantInfo = liquidationQRService.mapCustomerToMerchantInfo(testCustomer);
        
        assertNotNull(merchantInfo, "MerchantInfo ne doit pas être null");
        assertEquals("Jean Dupont", merchantInfo.getName(), "Le nom doit correspondre");
        assertEquals("Abidjan", merchantInfo.getCity(), "La ville doit être extraite de l'adresse");
        assertEquals(uemoaConfig.getCountryCode(), merchantInfo.getCountryCode(), "Le code pays doit correspondre");
        assertEquals(testCustomer.getIfu(), merchantInfo.getAlias(), "L'alias doit être l'IFU");
        
        System.out.println("✅ Mapping Customer vers MerchantInfo réussi:");
        System.out.println("   - Nom: " + merchantInfo.getName());
        System.out.println("   - Ville: " + merchantInfo.getCity());
        System.out.println("   - Pays: " + merchantInfo.getCountryCode());
        System.out.println("   - Alias: " + merchantInfo.getAlias());
    }

    @Test
    void testMapLiquidationToQRPaymentData() {
        // Test du mapping Liquidation vers QRPaymentData
        QRPaymentData paymentData = liquidationQRService.mapLiquidationToQRPaymentData(testLiquidation);
        
        assertNotNull(paymentData, "QRPaymentData ne doit pas être null");
        assertNotNull(paymentData.getMerchantInfo(), "MerchantInfo ne doit pas être null");
        assertEquals(testLiquidation.getAmount(), paymentData.getAmount(), "Le montant doit correspondre");
        assertEquals("Jean Dupont", paymentData.getMerchantInfo().getName(), "Le nom du marchand doit correspondre");
        
        System.out.println("✅ Mapping Liquidation vers QRPaymentData réussi:");
        System.out.println("   - Montant: " + paymentData.getAmount());
        System.out.println("   - Marchand: " + paymentData.getMerchantInfo().getName());
    }

    @Test
    void testGenerateTransactionReference() {
        // Test de génération de référence de transaction
        String reference = liquidationQRService.generateTransactionReference(testLiquidation);
        
        assertNotNull(reference, "La référence ne doit pas être null");
        assertTrue(reference.startsWith("LIQ-"), "La référence doit commencer par LIQ-");
        assertTrue(reference.contains(testLiquidation.getId().toString()), "La référence doit contenir l'ID de liquidation");
        
        System.out.println("✅ Génération de référence de transaction réussie:");
        System.out.println("   - Référence: " + reference);
    }

    @Test
    void testGenerateStaticQRForLiquidation() {
        try {
            // Test de génération de QR code statique
            Map<String, Object> qrData = liquidationQRService.generateStaticQRForLiquidation(testLiquidation);
            
            assertNotNull(qrData, "Les données QR ne doivent pas être null");
            assertNotNull(qrData.get("qrCode"), "Le QR code doit être généré");
            assertEquals(testLiquidation.getId(), qrData.get("liquidationId"), "L'ID de liquidation doit correspondre");
            assertEquals("Jean Dupont", qrData.get("customerName"), "Le nom du client doit correspondre");
            assertEquals("STATIC", qrData.get("type"), "Le type doit être STATIC");
            
            System.out.println("✅ Génération de QR code statique réussie:");
            System.out.println("   - Liquidation ID: " + qrData.get("liquidationId"));
            System.out.println("   - Client: " + qrData.get("customerName"));
            System.out.println("   - Montant: " + qrData.get("amount"));
            System.out.println("   - Type: " + qrData.get("type"));
            System.out.println("   - QR Code: " + qrData.get("qrCode").toString().substring(0, 50) + "...");
            
        } catch (Exception e) {
            fail("La génération de QR code statique a échoué: " + e.getMessage());
        }
    }

    @Test
    void testGenerateDynamicQRForLiquidation() {
        try {
            // Test de génération de QR code dynamique
            String transactionReference = "REF-TEST-123456";
            Map<String, Object> qrData = liquidationQRService.generateDynamicQRForLiquidation(testLiquidation, transactionReference);
            
            assertNotNull(qrData, "Les données QR ne doivent pas être null");
            assertNotNull(qrData.get("qrCode"), "Le QR code doit être généré");
            assertEquals(testLiquidation.getId(), qrData.get("liquidationId"), "L'ID de liquidation doit correspondre");
            assertEquals(transactionReference, qrData.get("transactionReference"), "La référence de transaction doit correspondre");
            assertEquals("DYNAMIC", qrData.get("type"), "Le type doit être DYNAMIC");
            
            System.out.println("✅ Génération de QR code dynamique réussie:");
            System.out.println("   - Liquidation ID: " + qrData.get("liquidationId"));
            System.out.println("   - Référence: " + qrData.get("transactionReference"));
            System.out.println("   - Type: " + qrData.get("type"));
            
        } catch (Exception e) {
            fail("La génération de QR code dynamique a échoué: " + e.getMessage());
        }
    }

    @Test
    void testGenerateP2PQRForLiquidation() {
        try {
            // Test de génération de QR code P2P
            String beneficiaryPhone = "+2250701234567";
            Map<String, Object> qrData = liquidationQRService.generateP2PQRForLiquidation(testLiquidation, beneficiaryPhone);
            
            assertNotNull(qrData, "Les données QR ne doivent pas être null");
            assertNotNull(qrData.get("qrCode"), "Le QR code doit être généré");
            assertEquals(testLiquidation.getId(), qrData.get("liquidationId"), "L'ID de liquidation doit correspondre");
            assertEquals(beneficiaryPhone, qrData.get("beneficiaryPhone"), "Le téléphone du bénéficiaire doit correspondre");
            assertEquals("P2P", qrData.get("type"), "Le type doit être P2P");
            
            System.out.println("✅ Génération de QR code P2P réussie:");
            System.out.println("   - Liquidation ID: " + qrData.get("liquidationId"));
            System.out.println("   - Téléphone bénéficiaire: " + qrData.get("beneficiaryPhone"));
            System.out.println("   - Type: " + qrData.get("type"));
            
        } catch (Exception e) {
            fail("La génération de QR code P2P a échoué: " + e.getMessage());
        }
    }

    @Test
    void testGenerateQRWithPenalty() {
        try {
            // Test de génération de QR code avec pénalités
            BigDecimal penaltyAmount = new BigDecimal("5000.00");
            Map<String, Object> qrData = liquidationQRService.generateQRWithPenalty(testLiquidation, penaltyAmount);
            
            assertNotNull(qrData, "Les données QR ne doivent pas être null");
            assertNotNull(qrData.get("qrCode"), "Le QR code doit être généré");
            assertEquals(testLiquidation.getId(), qrData.get("liquidationId"), "L'ID de liquidation doit correspondre");
            assertEquals(penaltyAmount, qrData.get("penaltyAmount"), "Le montant des pénalités doit correspondre");
            assertEquals("PENALTY", qrData.get("type"), "Le type doit être PENALTY");
            
            // Vérification du montant total
            BigDecimal expectedTotal = testLiquidation.getAmount().add(penaltyAmount);
            assertEquals(expectedTotal, qrData.get("totalAmount"), "Le montant total doit être correct");
            
            System.out.println("✅ Génération de QR code avec pénalités réussie:");
            System.out.println("   - Liquidation ID: " + qrData.get("liquidationId"));
            System.out.println("   - Montant de base: " + qrData.get("baseAmount"));
            System.out.println("   - Pénalités: " + qrData.get("penaltyAmount"));
            System.out.println("   - Total: " + qrData.get("totalAmount"));
            System.out.println("   - Type: " + qrData.get("type"));
            
        } catch (Exception e) {
            fail("La génération de QR code avec pénalités a échoué: " + e.getMessage());
        }
    }

    @Test
    void testGenerateStaticQRForLiquidation_InvalidLiquidation() {
        // Test avec une liquidation invalide (null)
        assertThrows(IllegalArgumentException.class, () -> {
            liquidationQRService.generateStaticQRForLiquidation(null);
        }, "Une exception doit être levée pour une liquidation null");
        
        System.out.println("✅ Validation d'erreur pour liquidation invalide réussie");
    }

    @Test
    void testGenerateDynamicQRForLiquidation_InvalidReference() {
        // Test avec une référence de transaction invalide
        assertThrows(IllegalArgumentException.class, () -> {
            liquidationQRService.generateDynamicQRForLiquidation(testLiquidation, null);
        }, "Une exception doit être levée pour une référence null");
        
        assertThrows(IllegalArgumentException.class, () -> {
            liquidationQRService.generateDynamicQRForLiquidation(testLiquidation, "");
        }, "Une exception doit être levée pour une référence vide");
        
        System.out.println("✅ Validation d'erreur pour référence invalide réussie");
    }
}
