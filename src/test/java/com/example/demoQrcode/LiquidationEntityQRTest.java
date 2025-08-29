package com.example.demoQrcode;

import com.example.demoQrcode.entity.Customer;
import com.example.demoQrcode.entity.Liquidation;
import com.example.demoQrcode.entity.LiquidationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test pour vérifier les nouvelles fonctionnalités QR de l'entité Liquidation
 */
@SpringBootTest
@ActiveProfiles("test")
public class LiquidationEntityQRTest {

    private Customer testCustomer;
    private Liquidation testLiquidation;

    @BeforeEach
    void setUp() {
        // Création d'un client de test
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setAddress("123 Main Street, Abidjan, Côte d'Ivoire");
        testCustomer.setIfu("IFU123456");
        testCustomer.setPhone("+22501234567");
        testCustomer.setEmail("john.doe@example.com");

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
    void testLiquidationQRFieldsInitialization() {
        // Vérification que les champs QR sont initialement null
        assertNull(testLiquidation.getQrCodeData());
        assertNull(testLiquidation.getQrImageBase64());
        assertNull(testLiquidation.getMerchantChannel());
        assertNull(testLiquidation.getTransactionId());
        assertNull(testLiquidation.getQrType());
        assertNull(testLiquidation.getQrGeneratedAt());
        assertNull(testLiquidation.getPenaltyAmount());
        assertNull(testLiquidation.getTotalAmount());
    }

    @Test
    void testSetAndGetQRFields() {
        // Test des setters et getters pour les champs QR
        String qrCodeData = "00020101021226580014com.aveplus.uemoa0112int.bceao.pi52045XXX5303360540550005802CI5913LIQUIDATION APP6007Abidjan6304";
        String qrImageBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";
        String merchantChannel = "int.bceao.pi";
        String transactionId = "LIQ-1-20241201120000-ABC12345";
        String qrType = "STATIC";
        LocalDateTime qrGeneratedAt = LocalDateTime.now();
        BigDecimal penaltyAmount = new BigDecimal("5000.00");
        BigDecimal totalAmount = new BigDecimal("55000.00");

        testLiquidation.setQrCodeData(qrCodeData);
        testLiquidation.setQrImageBase64(qrImageBase64);
        testLiquidation.setMerchantChannel(merchantChannel);
        testLiquidation.setTransactionId(transactionId);
        testLiquidation.setQrType(qrType);
        testLiquidation.setQrGeneratedAt(qrGeneratedAt);
        testLiquidation.setPenaltyAmount(penaltyAmount);
        testLiquidation.setTotalAmount(totalAmount);

        assertEquals(qrCodeData, testLiquidation.getQrCodeData());
        assertEquals(qrImageBase64, testLiquidation.getQrImageBase64());
        assertEquals(merchantChannel, testLiquidation.getMerchantChannel());
        assertEquals(transactionId, testLiquidation.getTransactionId());
        assertEquals(qrType, testLiquidation.getQrType());
        assertEquals(qrGeneratedAt, testLiquidation.getQrGeneratedAt());
        assertEquals(penaltyAmount, testLiquidation.getPenaltyAmount());
        assertEquals(totalAmount, testLiquidation.getTotalAmount());
    }

    @Test
    void testHasQrCode() {
        // Test sans QR code
        assertFalse(testLiquidation.hasQrCode());

        // Test avec QR code vide
        testLiquidation.setQrCodeData("");
        assertFalse(testLiquidation.hasQrCode());

        // Test avec QR code valide
        testLiquidation.setQrCodeData("00020101021226580014com.aveplus.uemoa0112int.bceao.pi52045XXX5303360540550005802CI5913LIQUIDATION APP6007Abidjan6304");
        assertTrue(testLiquidation.hasQrCode());
    }

    @Test
    void testCalculateTotalAmount() {
        // Test sans pénalités
        BigDecimal totalWithoutPenalty = testLiquidation.calculateTotalAmount();
        assertEquals(testLiquidation.getAmount(), totalWithoutPenalty);

        // Test avec pénalités nulles
        testLiquidation.setPenaltyAmount(null);
        BigDecimal totalWithNullPenalty = testLiquidation.calculateTotalAmount();
        assertEquals(testLiquidation.getAmount(), totalWithNullPenalty);

        // Test avec pénalités à zéro
        testLiquidation.setPenaltyAmount(BigDecimal.ZERO);
        BigDecimal totalWithZeroPenalty = testLiquidation.calculateTotalAmount();
        assertEquals(testLiquidation.getAmount(), totalWithZeroPenalty);

        // Test avec pénalités
        BigDecimal penaltyAmount = new BigDecimal("5000.00");
        testLiquidation.setPenaltyAmount(penaltyAmount);
        BigDecimal totalWithPenalty = testLiquidation.calculateTotalAmount();
        assertEquals(testLiquidation.getAmount().add(penaltyAmount), totalWithPenalty);
    }

    @Test
    void testUpdateTotalAmount() {
        // Test sans pénalités
        testLiquidation.updateTotalAmount();
        assertEquals(testLiquidation.getAmount(), testLiquidation.getTotalAmount());

        // Test avec pénalités
        BigDecimal penaltyAmount = new BigDecimal("5000.00");
        testLiquidation.setPenaltyAmount(penaltyAmount);
        testLiquidation.updateTotalAmount();
        assertEquals(testLiquidation.getAmount().add(penaltyAmount), testLiquidation.getTotalAmount());
    }

    @Test
    void testLiquidationWithQRCodeComplete() {
        // Configuration complète d'une liquidation avec QR code
        testLiquidation.setQrCodeData("00020101021226580014com.aveplus.uemoa0112int.bceao.pi52045XXX5303360540550005802CI5913LIQUIDATION APP6007Abidjan6304");
        testLiquidation.setQrImageBase64("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");
        testLiquidation.setMerchantChannel("int.bceao.pi");
        testLiquidation.setTransactionId("LIQ-1-20241201120000-ABC12345");
        testLiquidation.setQrType("STATIC");
        testLiquidation.setQrGeneratedAt(LocalDateTime.now());
        testLiquidation.setPenaltyAmount(new BigDecimal("5000.00"));
        testLiquidation.updateTotalAmount();

        // Vérifications
        assertTrue(testLiquidation.hasQrCode());
        assertEquals("STATIC", testLiquidation.getQrType());
        assertEquals("int.bceao.pi", testLiquidation.getMerchantChannel());
        assertNotNull(testLiquidation.getTransactionId());
        assertNotNull(testLiquidation.getQrGeneratedAt());
        assertEquals(new BigDecimal("55000.00"), testLiquidation.getTotalAmount());
    }
}
