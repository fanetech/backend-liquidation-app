package com.example.demoQrcode.service.impl;

import com.example.demoQrcode.entity.Liquidation;
import com.example.demoQrcode.entity.LiquidationStatus;
import com.example.demoQrcode.repository.LiquidationRepository;
import com.example.demoQrcode.service.LiquidationQRDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implémentation du service de gestion des données QR codes des liquidations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LiquidationQRDataServiceImpl implements LiquidationQRDataService {

    private final LiquidationRepository liquidationRepository;

    // --- OPÉRATIONS DE RECHERCHE ---

    @Override
    public List<Liquidation> findLiquidationsWithQrCode() {
        log.debug("Recherche de toutes les liquidations avec QR code");
        return liquidationRepository.findLiquidationsWithQrCode();
    }

    @Override
    public List<Liquidation> findLiquidationsWithoutQrCode() {
        log.debug("Recherche de toutes les liquidations sans QR code");
        return liquidationRepository.findLiquidationsWithoutQrCode();
    }

    @Override
    public List<Liquidation> findByQrType(String qrType) {
        log.debug("Recherche de liquidations par type de QR: {}", qrType);
        return liquidationRepository.findByQrType(qrType);
    }

    @Override
    public Optional<Liquidation> findByTransactionId(String transactionId) {
        log.debug("Recherche de liquidation par transaction ID: {}", transactionId);
        return liquidationRepository.findByTransactionId(transactionId);
    }

    @Override
    public List<Liquidation> findLiquidationsWithQrCodeByCustomerId(Long customerId) {
        log.debug("Recherche de liquidations avec QR code par client ID: {}", customerId);
        return liquidationRepository.findLiquidationsWithQrCodeByCustomerId(customerId);
    }

    @Override
    public List<Liquidation> findLiquidationsWithQrCodeByStatus(LiquidationStatus status) {
        log.debug("Recherche de liquidations avec QR code par statut: {}", status);
        return liquidationRepository.findLiquidationsWithQrCodeByStatus(status);
    }

    @Override
    public List<Liquidation> findLiquidationsWithQrCodeByTaxType(String taxType) {
        log.debug("Recherche de liquidations avec QR code par type de taxe: {}", taxType);
        return liquidationRepository.findLiquidationsWithQrCodeByTaxType(taxType);
    }

    @Override
    public List<Liquidation> findLiquidationsWithQrCodeGeneratedToday() {
        log.debug("Recherche de liquidations avec QR code généré aujourd'hui");
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return liquidationRepository.findLiquidationsWithQrCodeGeneratedToday(startOfDay, endOfDay);
    }

    @Override
    public List<Liquidation> findLiquidationsWithQrCodeGeneratedThisWeek() {
        log.debug("Recherche de liquidations avec QR code généré cette semaine");
        LocalDateTime weekStart = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime weekEnd = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).withHour(23).withMinute(59).withSecond(59);
        return liquidationRepository.findLiquidationsWithQrCodeGeneratedThisWeek(weekStart, weekEnd);
    }

    @Override
    public List<Liquidation> findLiquidationsWithQrCodeGeneratedThisMonth() {
        log.debug("Recherche de liquidations avec QR code généré ce mois");
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        return liquidationRepository.findLiquidationsWithQrCodeGeneratedThisMonth(startOfMonth, endOfMonth);
    }

    @Override
    public List<Liquidation> findLiquidationsWithPenalties() {
        log.debug("Recherche de liquidations avec pénalités");
        return liquidationRepository.findLiquidationsWithPenalties();
    }

    @Override
    public List<Liquidation> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        log.debug("Recherche de liquidations par montant total entre {} et {}", minAmount, maxAmount);
        return liquidationRepository.findByTotalAmountBetween(minAmount, maxAmount);
    }

    @Override
    public List<Liquidation> findByPenaltyAmountBetween(BigDecimal minPenalty, BigDecimal maxPenalty) {
        log.debug("Recherche de liquidations par montant de pénalités entre {} et {}", minPenalty, maxPenalty);
        return liquidationRepository.findByPenaltyAmountBetween(minPenalty, maxPenalty);
    }

    // --- OPÉRATIONS DE STATISTIQUES ---

    @Override
    public Map<String, Long> countLiquidationsByQrType() {
        log.debug("Comptage des liquidations par type de QR");
        List<Object[]> results = liquidationRepository.countLiquidationsByQrType();
        Map<String, Long> counts = new HashMap<>();
        
        for (Object[] result : results) {
            String qrType = (String) result[0];
            Long count = (Long) result[1];
            counts.put(qrType, count);
        }
        
        return counts;
    }

    @Override
    public long countLiquidationsWithQrCode() {
        log.debug("Comptage du nombre total de liquidations avec QR code");
        return liquidationRepository.findLiquidationsWithQrCode().size();
    }

    @Override
    public long countLiquidationsWithQrCodeGeneratedToday() {
        log.debug("Comptage des liquidations avec QR code généré aujourd'hui");
        return findLiquidationsWithQrCodeGeneratedToday().size();
    }

    @Override
    public long countLiquidationsWithQrCodeGeneratedThisWeek() {
        log.debug("Comptage des liquidations avec QR code généré cette semaine");
        return findLiquidationsWithQrCodeGeneratedThisWeek().size();
    }

    @Override
    public long countLiquidationsWithQrCodeGeneratedThisMonth() {
        log.debug("Comptage des liquidations avec QR code généré ce mois");
        return findLiquidationsWithQrCodeGeneratedThisMonth().size();
    }

    @Override
    public BigDecimal calculateTotalAmountOfLiquidationsWithQrCode() {
        log.debug("Calcul du montant total des liquidations avec QR code");
        return liquidationRepository.findLiquidationsWithQrCode()
                .stream()
                .map(liquidation -> liquidation.getTotalAmount() != null ? liquidation.getTotalAmount() : liquidation.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateTotalPenaltyAmount() {
        log.debug("Calcul du montant total des pénalités");
        return liquidationRepository.findLiquidationsWithPenalties()
                .stream()
                .map(liquidation -> liquidation.getPenaltyAmount() != null ? liquidation.getPenaltyAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // --- OPÉRATIONS DE MAINTENANCE ---

    @Override
    @Transactional
    public void removeQrCodeData(Long liquidationId) {
        log.info("Suppression des données QR de la liquidation ID: {}", liquidationId);
        Optional<Liquidation> liquidationOpt = liquidationRepository.findById(liquidationId);
        
        if (liquidationOpt.isPresent()) {
            Liquidation liquidation = liquidationOpt.get();
            liquidation.setQrCodeData(null);
            liquidation.setQrImageBase64(null);
            liquidation.setQrType(null);
            liquidation.setQrGeneratedAt(null);
            liquidation.setMerchantChannel(null);
            liquidation.setTransactionId(null);
            liquidation.setPenaltyAmount(null);
            liquidation.setTotalAmount(null);
            
            liquidationRepository.save(liquidation);
            log.info("Données QR supprimées avec succès pour la liquidation ID: {}", liquidationId);
        } else {
            log.warn("Liquidation non trouvée pour la suppression des données QR, ID: {}", liquidationId);
        }
    }

    @Override
    @Transactional
    public void removeQrCodeDataByCustomerId(Long customerId) {
        log.info("Suppression des données QR de toutes les liquidations du client ID: {}", customerId);
        List<Liquidation> liquidations = liquidationRepository.findLiquidationsWithQrCodeByCustomerId(customerId);
        
        for (Liquidation liquidation : liquidations) {
            liquidation.setQrCodeData(null);
            liquidation.setQrImageBase64(null);
            liquidation.setQrType(null);
            liquidation.setQrGeneratedAt(null);
            liquidation.setMerchantChannel(null);
            liquidation.setTransactionId(null);
            liquidation.setPenaltyAmount(null);
            liquidation.setTotalAmount(null);
        }
        
        liquidationRepository.saveAll(liquidations);
        log.info("Données QR supprimées pour {} liquidations du client ID: {}", liquidations.size(), customerId);
    }

    @Override
    @Transactional
    public void removeQrCodeDataOlderThan(LocalDateTime date) {
        log.info("Suppression des données QR des liquidations plus anciennes que: {}", date);
        List<Liquidation> liquidations = liquidationRepository.findLiquidationsWithQrCode();
        
        int count = 0;
        for (Liquidation liquidation : liquidations) {
            if (liquidation.getQrGeneratedAt() != null && liquidation.getQrGeneratedAt().isBefore(date)) {
                liquidation.setQrCodeData(null);
                liquidation.setQrImageBase64(null);
                liquidation.setQrType(null);
                liquidation.setQrGeneratedAt(null);
                liquidation.setMerchantChannel(null);
                liquidation.setTransactionId(null);
                liquidation.setPenaltyAmount(null);
                liquidation.setTotalAmount(null);
                count++;
            }
        }
        
        if (count > 0) {
            liquidationRepository.saveAll(liquidations);
            log.info("Données QR supprimées pour {} liquidations plus anciennes que {}", count, date);
        } else {
            log.info("Aucune liquidation trouvée plus ancienne que {}", date);
        }
    }

    @Override
    @Transactional
    public void updateTotalAmount(Long liquidationId) {
        log.debug("Mise à jour du montant total de la liquidation ID: {}", liquidationId);
        Optional<Liquidation> liquidationOpt = liquidationRepository.findById(liquidationId);
        
        if (liquidationOpt.isPresent()) {
            Liquidation liquidation = liquidationOpt.get();
            liquidation.updateTotalAmount();
            liquidationRepository.save(liquidation);
            log.debug("Montant total mis à jour pour la liquidation ID: {}", liquidationId);
        } else {
            log.warn("Liquidation non trouvée pour la mise à jour du montant total, ID: {}", liquidationId);
        }
    }

    @Override
    @Transactional
    public void updateAllTotalAmounts() {
        log.info("Mise à jour du montant total de toutes les liquidations");
        List<Liquidation> liquidations = liquidationRepository.findAll();
        
        for (Liquidation liquidation : liquidations) {
            liquidation.updateTotalAmount();
        }
        
        liquidationRepository.saveAll(liquidations);
        log.info("Montant total mis à jour pour {} liquidations", liquidations.size());
    }

    // --- OPÉRATIONS DE VALIDATION ---

    @Override
    public boolean hasValidQrCode(Long liquidationId) {
        log.debug("Vérification de la validité du QR code pour la liquidation ID: {}", liquidationId);
        Optional<Liquidation> liquidationOpt = liquidationRepository.findById(liquidationId);
        
        if (liquidationOpt.isPresent()) {
            Liquidation liquidation = liquidationOpt.get();
            return validateQrCodeData(liquidation);
        }
        
        return false;
    }

    @Override
    public boolean transactionIdExists(String transactionId) {
        log.debug("Vérification de l'existence du transaction ID: {}", transactionId);
        return liquidationRepository.findByTransactionId(transactionId).isPresent();
    }

    @Override
    public boolean validateQrCodeData(Liquidation liquidation) {
        if (liquidation == null) {
            return false;
        }
        
        // Vérification de la présence des données QR
        if (liquidation.getQrCodeData() == null || liquidation.getQrCodeData().trim().isEmpty()) {
            return false;
        }
        
        // Vérification du type de QR
        if (liquidation.getQrType() == null || liquidation.getQrType().trim().isEmpty()) {
            return false;
        }
        
        // Vérification de la date de génération
        if (liquidation.getQrGeneratedAt() == null) {
            return false;
        }
        
        // Vérification du canal marchand
        if (liquidation.getMerchantChannel() == null || liquidation.getMerchantChannel().trim().isEmpty()) {
            return false;
        }
        
        // Vérification de l'identifiant de transaction
        if (liquidation.getTransactionId() == null || liquidation.getTransactionId().trim().isEmpty()) {
            return false;
        }
        
        // Vérification des montants pour les liquidations avec pénalités
        if ("PENALTY".equals(liquidation.getQrType())) {
            if (liquidation.getPenaltyAmount() == null || liquidation.getPenaltyAmount().compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }
            if (liquidation.getTotalAmount() == null || liquidation.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }
        }
        
        return true;
    }
}
