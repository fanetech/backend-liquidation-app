package com.example.demoQrcode.service.impl;

import com.aveplus.uemoa.qr.model.QRPaymentData;
import com.aveplus.uemoa.qr.model.MerchantInfo;
import com.aveplus.uemoa.qr.service.UemoaQRService;
import com.example.demoQrcode.config.UemoaConfig;
import com.example.demoQrcode.entity.Liquidation;
import com.example.demoQrcode.entity.Customer;
import com.example.demoQrcode.entity.LiquidationStatus;
import com.example.demoQrcode.service.LiquidationQRService;
import com.example.demoQrcode.repository.LiquidationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implémentation du service de génération de QR codes pour les liquidations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LiquidationQRServiceImpl implements LiquidationQRService {

    private final UemoaQRService uemoaQRService;
    private final UemoaConfig uemoaConfig;
    private final LiquidationRepository liquidationRepository;

    @Override
    @Transactional
    public Map<String, Object> generateStaticQRForLiquidation(Liquidation liquidation) {
        // Validation de la liquidation
        if (liquidation == null) {
            throw new IllegalArgumentException("La liquidation ne peut pas être null");
        }
        
        log.info("Génération d'un QR code statique pour la liquidation ID: {}", liquidation.getId());
        
        if (!validateLiquidationForQR(liquidation)) {
            throw new IllegalArgumentException("Liquidation invalide pour la génération de QR code");
        }

        try {
            // Mapping de la liquidation vers QRPaymentData
            QRPaymentData paymentData = mapLiquidationToQRPaymentData(liquidation);
            
            // Génération du QR code statique
            String qrCode = uemoaQRService.generateStaticQR(paymentData);
            
            // Sauvegarde des données QR dans l'entité Liquidation
            liquidation.setQrCodeData(qrCode);
            liquidation.setQrType("STATIC");
            liquidation.setQrGeneratedAt(LocalDateTime.now());
            liquidation.setMerchantChannel(uemoaConfig.getPaymentSystem().getIdentifier());
            liquidation.setTransactionId(generateTransactionReference(liquidation));
            
            // Sauvegarde en base de données
            liquidationRepository.save(liquidation);
            
            // Création de la réponse
            Map<String, Object> result = new HashMap<>();
            result.put("qrCode", qrCode);
            result.put("liquidationId", liquidation.getId());
            result.put("customerName", liquidation.getCustomer().getFirstName() + " " + liquidation.getCustomer().getLastName());
            result.put("amount", liquidation.getAmount());
            result.put("currency", uemoaConfig.getCurrency());
            result.put("taxType", liquidation.getTaxType());
            result.put("dueDate", liquidation.getDueDate());
            result.put("type", "STATIC");
            result.put("transactionId", liquidation.getTransactionId());
            result.put("merchantChannel", liquidation.getMerchantChannel());
            result.put("generatedAt", liquidation.getQrGeneratedAt());
            
            log.info("QR code statique généré et sauvegardé avec succès pour la liquidation ID: {}", liquidation.getId());
            return result;
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code statique pour la liquidation ID: {}: {}", 
                     liquidation.getId(), e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération du QR code", e);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> generateDynamicQRForLiquidation(Liquidation liquidation, String transactionReference) {
        // Validation des paramètres
        if (liquidation == null) {
            throw new IllegalArgumentException("La liquidation ne peut pas être null");
        }
        
        if (transactionReference == null || transactionReference.trim().isEmpty()) {
            throw new IllegalArgumentException("La référence de transaction est requise");
        }
        
        log.info("Génération d'un QR code dynamique pour la liquidation ID: {} avec référence: {}", 
                liquidation.getId(), transactionReference);
        
        // Validation de la liquidation
        if (!validateLiquidationForQR(liquidation)) {
            throw new IllegalArgumentException("Liquidation invalide pour la génération de QR code");
        }

        try {
            // Mapping de la liquidation vers QRPaymentData avec référence
            QRPaymentData paymentData = mapLiquidationToQRPaymentData(liquidation);
            paymentData.setTransactionId(transactionReference);
            
            // Génération du QR code dynamique
            String qrCode = uemoaQRService.generateDynamicQR(paymentData);
            
            // Sauvegarde des données QR dans l'entité Liquidation
            liquidation.setQrCodeData(qrCode);
            liquidation.setQrType("DYNAMIC");
            liquidation.setQrGeneratedAt(LocalDateTime.now());
            liquidation.setMerchantChannel(uemoaConfig.getPaymentSystem().getIdentifier());
            liquidation.setTransactionId(transactionReference);
            
            // Sauvegarde en base de données
            liquidationRepository.save(liquidation);
            
            // Création de la réponse
            Map<String, Object> result = new HashMap<>();
            result.put("qrCode", qrCode);
            result.put("liquidationId", liquidation.getId());
            result.put("customerName", liquidation.getCustomer().getFirstName() + " " + liquidation.getCustomer().getLastName());
            result.put("amount", liquidation.getAmount());
            result.put("currency", uemoaConfig.getCurrency());
            result.put("taxType", liquidation.getTaxType());
            result.put("dueDate", liquidation.getDueDate());
            result.put("transactionReference", transactionReference);
            result.put("type", "DYNAMIC");
            result.put("transactionId", liquidation.getTransactionId());
            result.put("merchantChannel", liquidation.getMerchantChannel());
            result.put("generatedAt", liquidation.getQrGeneratedAt());
            
            log.info("QR code dynamique généré et sauvegardé avec succès pour la liquidation ID: {} avec référence: {}", 
                    liquidation.getId(), transactionReference);
            return result;
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code dynamique pour la liquidation ID: {}: {}", 
                     liquidation.getId(), e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération du QR code", e);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> generateP2PQRForLiquidation(Liquidation liquidation, String beneficiaryPhone) {
        // Validation des paramètres
        if (liquidation == null) {
            throw new IllegalArgumentException("La liquidation ne peut pas être null");
        }
        
        if (beneficiaryPhone == null || beneficiaryPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de téléphone du bénéficiaire est requis");
        }
        
        log.info("Génération d'un QR code P2P pour la liquidation ID: {} avec téléphone: {}", 
                liquidation.getId(), beneficiaryPhone);
        
        // Validation de la liquidation
        if (!validateLiquidationForQR(liquidation)) {
            throw new IllegalArgumentException("Liquidation invalide pour la génération de QR code");
        }

        try {
            // Mapping de la liquidation vers QRPaymentData
            QRPaymentData paymentData = mapLiquidationToQRPaymentData(liquidation);
            
            // Pour P2P, on utilise le téléphone comme identifiant du bénéficiaire
            // Note: Cette implémentation peut nécessiter des ajustements selon les spécifications UEMOA P2P
            String p2pReference = "P2P-" + liquidation.getId() + "-" + beneficiaryPhone;
            paymentData.setTransactionId(p2pReference);
            
            // Génération du QR code (utilise le service dynamique pour P2P)
            String qrCode = uemoaQRService.generateDynamicQR(paymentData);
            
            // Sauvegarde des données QR dans l'entité Liquidation
            liquidation.setQrCodeData(qrCode);
            liquidation.setQrType("P2P");
            liquidation.setQrGeneratedAt(LocalDateTime.now());
            liquidation.setMerchantChannel(uemoaConfig.getPaymentSystem().getIdentifier());
            liquidation.setTransactionId(p2pReference);
            
            // Sauvegarde en base de données
            liquidationRepository.save(liquidation);
            
            // Création de la réponse
            Map<String, Object> result = new HashMap<>();
            result.put("qrCode", qrCode);
            result.put("liquidationId", liquidation.getId());
            result.put("customerName", liquidation.getCustomer().getFirstName() + " " + liquidation.getCustomer().getLastName());
            result.put("amount", liquidation.getAmount());
            result.put("currency", uemoaConfig.getCurrency());
            result.put("taxType", liquidation.getTaxType());
            result.put("beneficiaryPhone", beneficiaryPhone);
            result.put("p2pReference", p2pReference);
            result.put("type", "P2P");
            result.put("transactionId", liquidation.getTransactionId());
            result.put("merchantChannel", liquidation.getMerchantChannel());
            result.put("generatedAt", liquidation.getQrGeneratedAt());
            
            log.info("QR code P2P généré et sauvegardé avec succès pour la liquidation ID: {} avec téléphone: {}", 
                    liquidation.getId(), beneficiaryPhone);
            return result;
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code P2P pour la liquidation ID: {}: {}", 
                     liquidation.getId(), e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération du QR code P2P", e);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> generateQRWithPenalty(Liquidation liquidation, BigDecimal penaltyAmount) {
        // Validation des paramètres
        if (liquidation == null) {
            throw new IllegalArgumentException("La liquidation ne peut pas être null");
        }
        
        if (penaltyAmount == null || penaltyAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant des pénalités doit être positif");
        }
        
        log.info("Génération d'un QR code avec pénalités pour la liquidation ID: {} - Pénalités: {}", 
                liquidation.getId(), penaltyAmount);
        
        // Validation de la liquidation
        if (!validateLiquidationForQR(liquidation)) {
            throw new IllegalArgumentException("Liquidation invalide pour la génération de QR code");
        }

        try {
            // Calcul du montant total (liquidation + pénalités)
            BigDecimal totalAmount = liquidation.getAmount().add(penaltyAmount);
            
            // Mapping de la liquidation vers QRPaymentData avec montant total
            QRPaymentData paymentData = mapLiquidationToQRPaymentData(liquidation);
            paymentData.setAmount(totalAmount);
            
            // Ajout d'une référence spéciale pour les pénalités
            String penaltyReference = "PENALTY-" + liquidation.getId() + "-" + System.currentTimeMillis();
            paymentData.setTransactionId(penaltyReference);
            
            // Génération du QR code dynamique
            String qrCode = uemoaQRService.generateDynamicQR(paymentData);
            
            // Sauvegarde des données QR dans l'entité Liquidation
            liquidation.setQrCodeData(qrCode);
            liquidation.setQrType("PENALTY");
            liquidation.setQrGeneratedAt(LocalDateTime.now());
            liquidation.setMerchantChannel(uemoaConfig.getPaymentSystem().getIdentifier());
            liquidation.setTransactionId(penaltyReference);
            liquidation.setPenaltyAmount(penaltyAmount);
            liquidation.setTotalAmount(totalAmount);
            
            // Sauvegarde en base de données
            liquidationRepository.save(liquidation);
            
            // Création de la réponse
            Map<String, Object> result = new HashMap<>();
            result.put("qrCode", qrCode);
            result.put("liquidationId", liquidation.getId());
            result.put("customerName", liquidation.getCustomer().getFirstName() + " " + liquidation.getCustomer().getLastName());
            result.put("baseAmount", liquidation.getAmount());
            result.put("penaltyAmount", penaltyAmount);
            result.put("totalAmount", totalAmount);
            result.put("currency", uemoaConfig.getCurrency());
            result.put("taxType", liquidation.getTaxType());
            result.put("dueDate", liquidation.getDueDate());
            result.put("penaltyReference", penaltyReference);
            result.put("type", "PENALTY");
            result.put("transactionId", liquidation.getTransactionId());
            result.put("merchantChannel", liquidation.getMerchantChannel());
            result.put("generatedAt", liquidation.getQrGeneratedAt());
            
            log.info("QR code avec pénalités généré et sauvegardé avec succès pour la liquidation ID: {} - Total: {}", 
                    liquidation.getId(), totalAmount);
            return result;
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code avec pénalités pour la liquidation ID: {}: {}", 
                     liquidation.getId(), e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération du QR code avec pénalités", e);
        }
    }

    @Override
    public boolean validateLiquidationForQR(Liquidation liquidation) {
        if (liquidation == null) {
            log.warn("Liquidation est null");
            return false;
        }
        
        if (liquidation.getId() == null) {
            log.warn("ID de liquidation est null");
            return false;
        }
        
        if (liquidation.getCustomer() == null) {
            log.warn("Client de liquidation est null pour l'ID: {}", liquidation.getId());
            return false;
        }
        
        if (liquidation.getAmount() == null || liquidation.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Montant de liquidation invalide pour l'ID: {}", liquidation.getId());
            return false;
        }
        
        if (liquidation.getStatus() == LiquidationStatus.PAID) {
            log.warn("Liquidation déjà payée pour l'ID: {}", liquidation.getId());
            return false;
        }
        
        if (liquidation.getTaxType() == null || liquidation.getTaxType().trim().isEmpty()) {
            log.warn("Type de taxe manquant pour la liquidation ID: {}", liquidation.getId());
            return false;
        }
        
        log.debug("Liquidation ID: {} validée avec succès", liquidation.getId());
        return true;
    }

    @Override
    public MerchantInfo mapCustomerToMerchantInfo(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Le client ne peut pas être null");
        }
        
        // Construction du nom complet du client
        String fullName = customer.getFirstName() + " " + customer.getLastName();
        
        // Extraction de la ville depuis l'adresse (simplification)
        String city = extractCityFromAddress(customer.getAddress());
        
        return MerchantInfo.builder()
                .name(fullName)
                .city(city)
                .countryCode(uemoaConfig.getCountryCode())
                .categoryCode(uemoaConfig.getMerchantCategoryCode())
                .alias(customer.getIfu()) // Utilisation de l'IFU comme alias
                .build();
    }

    @Override
    public QRPaymentData mapLiquidationToQRPaymentData(Liquidation liquidation) {
        if (liquidation == null) {
            throw new IllegalArgumentException("La liquidation ne peut pas être null");
        }
        
        // Mapping du client vers MerchantInfo
        MerchantInfo merchantInfo = mapCustomerToMerchantInfo(liquidation.getCustomer());
        
        // Conversion du montant en centimes pour UEMOA
        BigDecimal amountInUnits = liquidation.getAmount();
        
        return QRPaymentData.builder()
                .merchantInfo(merchantInfo)
                .amount(amountInUnits)
                .build();
    }

    @Override
    public String generateTransactionReference(Liquidation liquidation) {
        if (liquidation == null || liquidation.getId() == null) {
            throw new IllegalArgumentException("Liquidation invalide pour la génération de référence");
        }
        
        // Format: LIQ-{ID}-{TIMESTAMP}-{UUID}
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        return String.format("LIQ-%d-%s-%s", liquidation.getId(), timestamp, uuid);
    }

    /**
     * Extrait la ville depuis l'adresse du client
     * 
     * @param address L'adresse complète
     * @return La ville extraite ou "Abidjan" par défaut
     */
    private String extractCityFromAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return uemoaConfig.getMerchantCity();
        }
        
        // Logique d'extraction de ville
        // On cherche "Abidjan" dans l'adresse
        if (address.toLowerCase().contains("abidjan")) {
            return "Abidjan";
        }
        
        // Si pas d'Abidjan, on prend le deuxième élément après la virgule (si disponible)
        String[] parts = address.split(",");
        if (parts.length > 1) {
            String secondPart = parts[1].trim();
            if (!secondPart.isEmpty()) {
                return secondPart;
            }
        }
        
        // Sinon, on prend le dernier élément
        if (parts.length > 0) {
            String lastPart = parts[parts.length - 1].trim();
            if (!lastPart.isEmpty()) {
                return lastPart;
            }
        }
        
        return uemoaConfig.getMerchantCity();
    }
}
