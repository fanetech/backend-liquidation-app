package com.example.demoQrcode.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration pour les paramètres UEMOA QR Code
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "uemoa.qr")
public class UemoaConfig {
    
    /**
     * Code pays (ex: CI pour Côte d'Ivoire)
     */
    private String countryCode = "CI";
    
    /**
     * Devise (ex: XOF pour Franc CFA)
     */
    private String currency = "XOF";
    
    /**
     * Code catégorie marchand
     */
    private String merchantCategoryCode = "0000";
    
    /**
     * Nom du marchand
     */
    private String merchantName = "LIQUIDATION APP";
    
    /**
     * Ville du marchand
     */
    private String merchantCity = "Abidjan";
    
    /**
     * Code postal du marchand
     */
    private String merchantPostalCode = "225";
    
    /**
     * Pays du marchand
     */
    private String merchantCountry = "CI";
    
    /**
     * Configuration du système de paiement
     */
    private PaymentSystem paymentSystem = new PaymentSystem();
    
    /**
     * Configuration des montants
     */
    private Amount amount = new Amount();
    
    /**
     * Configuration des montants par défaut
     */
    private int defaultAmount = 100;
    private int minAmount = 1;
    private int maxAmount = 999999;
    
    /**
     * Configuration des QR codes
     */
    private QR qr = new QR();
    
    /**
     * Configuration des tests
     */
    private Test test = new Test();
    
    @Data
    public static class PaymentSystem {
        private String identifier = "int.bceao.pi";
        private String name = "BCEAO Payment Interface";
    }
    
    @Data
    public static class Amount {
        private int defaultAmount = 100;
        private int minAmount = 1;
        private int maxAmount = 999999;
    }
    
    // Getters pour les propriétés de base
    public String getCountryCode() {
        return countryCode;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public String getMerchantName() {
        return merchantName;
    }
    
    public String getMerchantCity() {
        return merchantCity;
    }
    
    public String getMerchantCategoryCode() {
        return merchantCategoryCode;
    }
    
    public Amount getAmount() {
        return amount;
    }
    
    public Test getTest() {
        return test;
    }
    
    @Data
    public static class QR {
        private int size = 300;
        private String format = "PNG";
        private String errorCorrection = "M";
    }
    
    @Data
    public static class Test {
        private boolean enabled = true;
        private String merchantId = "test-123";
        private String terminalId = "test-terminal";
    }
}
