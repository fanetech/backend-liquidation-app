package com.example.demoQrcode.service;

import com.example.demoQrcode.entity.Liquidation;
import com.example.demoQrcode.entity.LiquidationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service pour gérer les opérations de base de données liées aux QR codes des liquidations
 */
public interface LiquidationQRDataService {

    // --- OPÉRATIONS DE RECHERCHE ---
    
    /**
     * Trouve toutes les liquidations qui ont un QR code généré
     */
    List<Liquidation> findLiquidationsWithQrCode();
    
    /**
     * Trouve toutes les liquidations qui n'ont pas de QR code généré
     */
    List<Liquidation> findLiquidationsWithoutQrCode();
    
    /**
     * Trouve toutes les liquidations par type de QR code
     */
    List<Liquidation> findByQrType(String qrType);
    
    /**
     * Trouve une liquidation par identifiant de transaction
     */
    Optional<Liquidation> findByTransactionId(String transactionId);
    
    /**
     * Trouve toutes les liquidations avec QR code par client
     */
    List<Liquidation> findLiquidationsWithQrCodeByCustomerId(Long customerId);
    
    /**
     * Trouve toutes les liquidations avec QR code par statut
     */
    List<Liquidation> findLiquidationsWithQrCodeByStatus(LiquidationStatus status);
    
    /**
     * Trouve toutes les liquidations avec QR code par type de taxe
     */
    List<Liquidation> findLiquidationsWithQrCodeByTaxType(String taxType);
    
    /**
     * Trouve toutes les liquidations avec QR code généré aujourd'hui
     */
    List<Liquidation> findLiquidationsWithQrCodeGeneratedToday();
    
    /**
     * Trouve toutes les liquidations avec QR code généré cette semaine
     */
    List<Liquidation> findLiquidationsWithQrCodeGeneratedThisWeek();
    
    /**
     * Trouve toutes les liquidations avec QR code généré ce mois
     */
    List<Liquidation> findLiquidationsWithQrCodeGeneratedThisMonth();
    
    /**
     * Trouve toutes les liquidations avec pénalités
     */
    List<Liquidation> findLiquidationsWithPenalties();
    
    /**
     * Trouve toutes les liquidations par montant total (base + pénalités)
     */
    List<Liquidation> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    /**
     * Trouve toutes les liquidations par montant de pénalités
     */
    List<Liquidation> findByPenaltyAmountBetween(BigDecimal minPenalty, BigDecimal maxPenalty);
    
    // --- OPÉRATIONS DE STATISTIQUES ---
    
    /**
     * Compte le nombre de liquidations avec QR code par type
     */
    Map<String, Long> countLiquidationsByQrType();
    
    /**
     * Compte le nombre total de liquidations avec QR code
     */
    long countLiquidationsWithQrCode();
    
    /**
     * Compte le nombre de liquidations avec QR code générées aujourd'hui
     */
    long countLiquidationsWithQrCodeGeneratedToday();
    
    /**
     * Compte le nombre de liquidations avec QR code générées cette semaine
     */
    long countLiquidationsWithQrCodeGeneratedThisWeek();
    
    /**
     * Compte le nombre de liquidations avec QR code générées ce mois
     */
    long countLiquidationsWithQrCodeGeneratedThisMonth();
    
    /**
     * Calcule le montant total des liquidations avec QR code
     */
    BigDecimal calculateTotalAmountOfLiquidationsWithQrCode();
    
    /**
     * Calcule le montant total des pénalités
     */
    BigDecimal calculateTotalPenaltyAmount();
    
    // --- OPÉRATIONS DE MAINTENANCE ---
    
    /**
     * Supprime les données QR d'une liquidation
     */
    void removeQrCodeData(Long liquidationId);
    
    /**
     * Supprime les données QR de toutes les liquidations d'un client
     */
    void removeQrCodeDataByCustomerId(Long customerId);
    
    /**
     * Supprime les données QR des liquidations plus anciennes qu'une date donnée
     */
    void removeQrCodeDataOlderThan(LocalDateTime date);
    
    /**
     * Met à jour le montant total d'une liquidation (base + pénalités)
     */
    void updateTotalAmount(Long liquidationId);
    
    /**
     * Met à jour le montant total de toutes les liquidations
     */
    void updateAllTotalAmounts();
    
    // --- OPÉRATIONS DE VALIDATION ---
    
    /**
     * Vérifie si une liquidation a un QR code valide
     */
    boolean hasValidQrCode(Long liquidationId);
    
    /**
     * Vérifie si une transaction ID existe déjà
     */
    boolean transactionIdExists(String transactionId);
    
    /**
     * Valide les données QR d'une liquidation
     */
    boolean validateQrCodeData(Liquidation liquidation);
}
