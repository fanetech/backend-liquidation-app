package com.example.demoQrcode.service;

import com.aveplus.uemoa.qr.model.QRPaymentData;
import com.aveplus.uemoa.qr.model.MerchantInfo;
import com.example.demoQrcode.entity.Liquidation;
import com.example.demoQrcode.entity.Customer;
import com.example.demoQrcode.entity.LiquidationStatus;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * Service pour intégrer la génération de QR codes UEMOA aux entités de liquidation
 */
public interface LiquidationQRService {

    /**
     * Génère un QR code statique pour une liquidation
     * 
     * @param liquidation La liquidation pour laquelle générer le QR code
     * @return Données du QR code généré
     * @throws IllegalArgumentException si la liquidation est invalide
     */
    Map<String, Object> generateStaticQRForLiquidation(Liquidation liquidation);

    /**
     * Génère un QR code dynamique pour une liquidation avec référence de transaction
     * 
     * @param liquidation La liquidation pour laquelle générer le QR code
     * @param transactionReference Référence de transaction unique
     * @return Données du QR code généré
     * @throws IllegalArgumentException si la liquidation ou la référence est invalide
     */
    Map<String, Object> generateDynamicQRForLiquidation(Liquidation liquidation, String transactionReference);

    /**
     * Génère un QR code P2P (Peer-to-Peer) pour une liquidation
     * 
     * @param liquidation La liquidation pour laquelle générer le QR code
     * @param beneficiaryPhone Numéro de téléphone du bénéficiaire
     * @return Données du QR code généré
     * @throws IllegalArgumentException si la liquidation ou le téléphone est invalide
     */
    Map<String, Object> generateP2PQRForLiquidation(Liquidation liquidation, String beneficiaryPhone);

    /**
     * Génère un QR code pour une liquidation avec pénalités
     * 
     * @param liquidation La liquidation pour laquelle générer le QR code
     * @param penaltyAmount Montant des pénalités à ajouter
     * @return Données du QR code généré
     * @throws IllegalArgumentException si la liquidation ou les pénalités sont invalides
     */
    Map<String, Object> generateQRWithPenalty(Liquidation liquidation, BigDecimal penaltyAmount);

    /**
     * Valide une liquidation pour la génération de QR code
     * 
     * @param liquidation La liquidation à valider
     * @return true si la liquidation est valide pour la génération de QR
     */
    boolean validateLiquidationForQR(Liquidation liquidation);

    /**
     * Mappe un client (Customer) vers MerchantInfo pour UEMOA
     * 
     * @param customer Le client à mapper
     * @return MerchantInfo correspondant
     */
    MerchantInfo mapCustomerToMerchantInfo(Customer customer);

    /**
     * Mappe une liquidation vers QRPaymentData pour UEMOA
     * 
     * @param liquidation La liquidation à mapper
     * @return QRPaymentData correspondant
     */
    QRPaymentData mapLiquidationToQRPaymentData(Liquidation liquidation);

    /**
     * Génère une référence de transaction unique pour une liquidation
     * 
     * @param liquidation La liquidation
     * @return Référence de transaction unique
     */
    String generateTransactionReference(Liquidation liquidation);
}
