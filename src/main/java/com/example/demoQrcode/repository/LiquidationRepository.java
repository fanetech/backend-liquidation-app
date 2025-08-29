package com.example.demoQrcode.repository;

import com.example.demoQrcode.entity.Liquidation;
import com.example.demoQrcode.entity.LiquidationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour les opérations de liquidation avec support des QR codes
 */
@Repository
public interface LiquidationRepository extends JpaRepository<Liquidation, Long>, JpaSpecificationExecutor<Liquidation> {

    // --- REQUÊTES STANDARD ---
    
    /**
     * Trouve toutes les liquidations par statut
     */
    List<Liquidation> findByStatus(LiquidationStatus status);
    
    /**
     * Trouve toutes les liquidations par type de taxe
     */
    List<Liquidation> findByTaxType(String taxType);
    
    /**
     * Trouve toutes les liquidations par client
     */
    List<Liquidation> findByCustomerId(Long customerId);
    
    /**
     * Trouve toutes les liquidations par client (compatibilité avec l'ancien code)
     */
    List<Liquidation> findByCustomer_Id(Long customerId);
    
    /**
     * Trouve toutes les liquidations par statut avec pagination
     */
    Page<Liquidation> findByStatus(LiquidationStatus status, Pageable pageable);
    
    /**
     * Trouve toutes les liquidations par montant supérieur ou égal
     */
    List<Liquidation> findByAmountGreaterThanEqual(BigDecimal amount);
    
    // --- REQUÊTES SPÉCIFIQUES AUX QR CODES ---
    
    /**
     * Trouve toutes les liquidations qui ont un QR code généré
     */
    @Query("SELECT l FROM Liquidation l WHERE l.qrCodeData IS NOT NULL AND l.qrCodeData != ''")
    List<Liquidation> findLiquidationsWithQrCode();
    
    /**
     * Trouve toutes les liquidations qui n'ont pas de QR code généré
     */
    @Query("SELECT l FROM Liquidation l WHERE l.qrCodeData IS NULL OR l.qrCodeData = ''")
    List<Liquidation> findLiquidationsWithoutQrCode();
    
    /**
     * Trouve toutes les liquidations par type de QR code
     */
    List<Liquidation> findByQrType(String qrType);
    
    /**
     * Trouve toutes les liquidations par canal marchand
     */
    List<Liquidation> findByMerchantChannel(String merchantChannel);
    
    /**
     * Trouve une liquidation par identifiant de transaction
     */
    Optional<Liquidation> findByTransactionId(String transactionId);
    
    /**
     * Trouve toutes les liquidations générées dans une période donnée
     */
    @Query("SELECT l FROM Liquidation l WHERE l.qrGeneratedAt BETWEEN :startDate AND :endDate")
    List<Liquidation> findByQrGeneratedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);
    
    /**
     * Trouve toutes les liquidations avec pénalités
     */
    @Query("SELECT l FROM Liquidation l WHERE l.penaltyAmount IS NOT NULL AND l.penaltyAmount > 0")
    List<Liquidation> findLiquidationsWithPenalties();
    
    /**
     * Trouve toutes les liquidations par montant total (base + pénalités)
     */
    @Query("SELECT l FROM Liquidation l WHERE l.totalAmount >= :minAmount AND l.totalAmount <= :maxAmount")
    List<Liquidation> findByTotalAmountBetween(@Param("minAmount") BigDecimal minAmount, 
                                              @Param("maxAmount") BigDecimal maxAmount);
    
    /**
     * Compte le nombre de liquidations avec QR code par type
     */
    @Query("SELECT l.qrType, COUNT(l) FROM Liquidation l WHERE l.qrType IS NOT NULL GROUP BY l.qrType")
    List<Object[]> countLiquidationsByQrType();
    
    /**
     * Trouve toutes les liquidations avec QR code par client
     */
    @Query("SELECT l FROM Liquidation l WHERE l.customer.id = :customerId AND l.qrCodeData IS NOT NULL AND l.qrCodeData != ''")
    List<Liquidation> findLiquidationsWithQrCodeByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * Trouve la liquidation la plus récente avec QR code pour un client donné
     */
    @Query("SELECT l FROM Liquidation l WHERE l.customer.id = :customerId AND l.qrCodeData IS NOT NULL ORDER BY l.qrGeneratedAt DESC")
    List<Liquidation> findRecentLiquidationsWithQrCodeByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * Trouve toutes les liquidations avec QR code par statut
     */
    @Query("SELECT l FROM Liquidation l WHERE l.status = :status AND l.qrCodeData IS NOT NULL AND l.qrCodeData != ''")
    List<Liquidation> findLiquidationsWithQrCodeByStatus(@Param("status") LiquidationStatus status);
    
    /**
     * Trouve toutes les liquidations avec QR code par type de taxe
     */
    @Query("SELECT l FROM Liquidation l WHERE l.taxType = :taxType AND l.qrCodeData IS NOT NULL AND l.qrCodeData != ''")
    List<Liquidation> findLiquidationsWithQrCodeByTaxType(@Param("taxType") String taxType);
    
    /**
     * Trouve toutes les liquidations avec QR code généré aujourd'hui
     */
    @Query("SELECT l FROM Liquidation l WHERE l.qrGeneratedAt >= :startOfDay AND l.qrGeneratedAt < :endOfDay")
    List<Liquidation> findLiquidationsWithQrCodeGeneratedToday(@Param("startOfDay") LocalDateTime startOfDay, 
                                                              @Param("endOfDay") LocalDateTime endOfDay);
    
    /**
     * Trouve toutes les liquidations avec QR code généré cette semaine
     */
    @Query("SELECT l FROM Liquidation l WHERE l.qrGeneratedAt >= :weekStart AND l.qrGeneratedAt <= :weekEnd")
    List<Liquidation> findLiquidationsWithQrCodeGeneratedThisWeek(@Param("weekStart") LocalDateTime weekStart, 
                                                                 @Param("weekEnd") LocalDateTime weekEnd);
    
    /**
     * Trouve toutes les liquidations avec QR code généré ce mois
     */
    @Query("SELECT l FROM Liquidation l WHERE l.qrGeneratedAt >= :startOfMonth AND l.qrGeneratedAt < :endOfMonth")
    List<Liquidation> findLiquidationsWithQrCodeGeneratedThisMonth(@Param("startOfMonth") LocalDateTime startOfMonth, 
                                                                  @Param("endOfMonth") LocalDateTime endOfMonth);
    
    /**
     * Trouve toutes les liquidations avec QR code par canal marchand et type
     */
    @Query("SELECT l FROM Liquidation l WHERE l.merchantChannel = :merchantChannel AND l.qrType = :qrType")
    List<Liquidation> findByMerchantChannelAndQrType(@Param("merchantChannel") String merchantChannel, 
                                                    @Param("qrType") String qrType);
    
    /**
     * Trouve toutes les liquidations avec QR code par montant de pénalités
     */
    @Query("SELECT l FROM Liquidation l WHERE l.penaltyAmount >= :minPenalty AND l.penaltyAmount <= :maxPenalty")
    List<Liquidation> findByPenaltyAmountBetween(@Param("minPenalty") BigDecimal minPenalty, 
                                                @Param("maxPenalty") BigDecimal maxPenalty);
}


