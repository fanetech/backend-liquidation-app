package com.example.demoQrcode.service;

import com.aveplus.uemoa.qr.model.QRPaymentData;
import com.aveplus.uemoa.qr.service.UemoaQRService;
import com.example.demoQrcode.config.UemoaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service d'intégration pour le module UEMOA QR Code
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UemoaQRIntegrationService {
    
    private final UemoaQRService uemoaQRService;
    private final UemoaConfig uemoaConfig;
    
    /**
     * Génère un QR code statique pour un montant donné
     * 
     * @param amount Montant en centimes
     * @param merchantName Nom du marchand (optionnel)
     * @return Données du QR code généré
     */
    public Map<String, Object> generateStaticQR(int amount, String merchantName) {
        try {
            log.info("Génération d'un QR code statique pour le montant: {} centimes", amount);
            
            // Validation du montant
            if (amount < uemoaConfig.getAmount().getMinAmount()) {
                throw new IllegalArgumentException("Le montant est trop faible");
            }
            if (amount > uemoaConfig.getAmount().getMaxAmount()) {
                throw new IllegalArgumentException("Le montant est trop élevé");
            }
            
            // Utilisation du nom du marchand fourni ou de celui par défaut
            String merchant = Optional.ofNullable(merchantName)
                    .filter(name -> !name.trim().isEmpty())
                    .orElse(uemoaConfig.getMerchantName());
            
            // Création des données de paiement
            QRPaymentData paymentData = QRPaymentData.builder()
                    .merchantInfo(com.aveplus.uemoa.qr.model.MerchantInfo.builder()
                            .name(merchant)
                            .city(uemoaConfig.getMerchantCity())
                            .countryCode(uemoaConfig.getCountryCode())
                            .categoryCode(uemoaConfig.getMerchantCategoryCode())
                            .alias(uemoaConfig.getTest().getMerchantId())
                            .build())
                    .amount(new BigDecimal(amount).divide(new BigDecimal(100))) // Conversion centimes -> unités
                    .build();
            
            // Génération du QR code
            String qrData = uemoaQRService.generateStaticQR(paymentData);
            
            // Création de la réponse
            Map<String, Object> result = new HashMap<>();
            result.put("qrCode", qrData);
            result.put("merchantName", merchant);
            result.put("amount", amount);
            result.put("currency", uemoaConfig.getCurrency());
            result.put("countryCode", uemoaConfig.getCountryCode());
            result.put("type", "STATIC");
            
            log.info("QR code statique généré avec succès pour: {}", merchant);
            return result;
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code statique: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération du QR code", e);
        }
    }
    
    /**
     * Génère un QR code dynamique pour un montant donné
     * 
     * @param amount Montant en centimes
     * @param merchantName Nom du marchand (optionnel)
     * @param reference Référence de transaction
     * @return Données du QR code généré
     */
    public Map<String, Object> generateDynamicQR(int amount, String merchantName, String reference) {
        try {
            log.info("Génération d'un QR code dynamique pour le montant: {} centimes, référence: {}", amount, reference);
            
            // Validation du montant
            if (amount < uemoaConfig.getAmount().getMinAmount()) {
                throw new IllegalArgumentException("Le montant est trop faible");
            }
            if (amount > uemoaConfig.getAmount().getMaxAmount()) {
                throw new IllegalArgumentException("Le montant est trop élevé");
            }
            
            // Validation de la référence
            if (reference == null || reference.trim().isEmpty()) {
                throw new IllegalArgumentException("La référence de transaction est requise");
            }
            
            // Utilisation du nom du marchand fourni ou de celui par défaut
            String merchant = Optional.ofNullable(merchantName)
                    .filter(name -> !name.trim().isEmpty())
                    .orElse(uemoaConfig.getMerchantName());
            
            // Création des données de paiement
            QRPaymentData paymentData = QRPaymentData.builder()
                    .merchantInfo(com.aveplus.uemoa.qr.model.MerchantInfo.builder()
                            .name(merchant)
                            .city(uemoaConfig.getMerchantCity())
                            .countryCode(uemoaConfig.getCountryCode())
                            .categoryCode(uemoaConfig.getMerchantCategoryCode())
                            .alias(uemoaConfig.getTest().getMerchantId())
                            .build())
                    .amount(new BigDecimal(amount).divide(new BigDecimal(100))) // Conversion centimes -> unités
                    .transactionId(reference)
                    .build();
            
            // Génération du QR code
            String qrData = uemoaQRService.generateDynamicQR(paymentData);
            
            // Création de la réponse
            Map<String, Object> result = new HashMap<>();
            result.put("qrCode", qrData);
            result.put("merchantName", merchant);
            result.put("amount", amount);
            result.put("currency", uemoaConfig.getCurrency());
            result.put("countryCode", uemoaConfig.getCountryCode());
            result.put("reference", reference);
            result.put("type", "DYNAMIC");
            
            log.info("QR code dynamique généré avec succès pour: {}, référence: {}", merchant, reference);
            return result;
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code dynamique: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération du QR code", e);
        }
    }
    
    /**
     * Parse un QR code UEMOA
     * 
     * @param qrData Données du QR code à parser
     * @return Données de paiement parsées
     */
    public Map<String, Object> parseQRCode(String qrData) {
        try {
            log.info("Parsing d'un QR code UEMOA");
            
            QRPaymentData parsedData = uemoaQRService.parseQRCode(qrData);
            
            // Création de la réponse
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("parsedData", parsedData);
            
            if (parsedData.getMerchantInfo() != null) {
                result.put("merchantName", parsedData.getMerchantInfo().getName());
                result.put("merchantCity", parsedData.getMerchantInfo().getCity());
                result.put("countryCode", parsedData.getMerchantInfo().getCountryCode());
            }
            
            result.put("amount", parsedData.getAmount());
            result.put("type", parsedData.getType());
            
            log.info("QR code parsé avec succès");
            return result;
            
        } catch (Exception e) {
            log.error("Erreur lors du parsing du QR code: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors du parsing du QR code", e);
        }
    }
    
    /**
     * Génère un QR code de test
     * 
     * @return Données du QR code de test
     */
    public Map<String, Object> generateTestQR() {
        if (!uemoaConfig.getTest().isEnabled()) {
            throw new IllegalStateException("Les QR codes de test ne sont pas activés");
        }
        
        return generateStaticQR(uemoaConfig.getAmount().getDefaultAmount(), "TEST MERCHANT");
    }
}
