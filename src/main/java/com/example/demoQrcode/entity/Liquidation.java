package com.example.demoQrcode.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "liquidations")
public class Liquidation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Customer customer;

    @NotNull
    @Column(nullable = false, length = 128)
    private String taxType;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(nullable = false)
    private LocalDate issueDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private LiquidationStatus status = LiquidationStatus.PENDING;

    // --- CHAMPS QR CODES UEMOA ---
    
    /**
     * Données du QR code généré (format EMVCo/BCEAO)
     */
    @Column(name = "qr_code_data", columnDefinition = "TEXT")
    private String qrCodeData;
    
    /**
     * Image QR code encodée en Base64
     */
    @Column(name = "qr_image_base64", columnDefinition = "TEXT")
    private String qrImageBase64;
    
    /**
     * Canal marchand UEMOA (ex: "int.bceao.pi")
     */
    @Column(name = "merchant_channel", length = 64)
    private String merchantChannel;
    
    /**
     * Identifiant de transaction unique
     */
    @Column(name = "transaction_id", length = 128)
    private String transactionId;
    
    /**
     * Type de QR code généré (STATIC, DYNAMIC, P2P, PENALTY)
     */
    @Column(name = "qr_type", length = 16)
    private String qrType;
    
    /**
     * Date de génération du QR code
     */
    @Column(name = "qr_generated_at")
    private LocalDateTime qrGeneratedAt;
    
    /**
     * Montant des pénalités (si applicable)
     */
    @Column(name = "penalty_amount", precision = 18, scale = 2)
    private BigDecimal penaltyAmount;
    
    /**
     * Montant total (base + pénalités)
     */
    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    public Liquidation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public String getTaxType() { return taxType; }
    public void setTaxType(String taxType) { this.taxType = taxType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LiquidationStatus getStatus() { return status; }
    public void setStatus(LiquidationStatus status) { this.status = status; }

    // --- GETTERS ET SETTERS POUR LES CHAMPS QR ---

    public String getQrCodeData() { return qrCodeData; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }

    public String getQrImageBase64() { return qrImageBase64; }
    public void setQrImageBase64(String qrImageBase64) { this.qrImageBase64 = qrImageBase64; }

    public String getMerchantChannel() { return merchantChannel; }
    public void setMerchantChannel(String merchantChannel) { this.merchantChannel = merchantChannel; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getQrType() { return qrType; }
    public void setQrType(String qrType) { this.qrType = qrType; }

    public LocalDateTime getQrGeneratedAt() { return qrGeneratedAt; }
    public void setQrGeneratedAt(LocalDateTime qrGeneratedAt) { this.qrGeneratedAt = qrGeneratedAt; }

    public BigDecimal getPenaltyAmount() { return penaltyAmount; }
    public void setPenaltyAmount(BigDecimal penaltyAmount) { this.penaltyAmount = penaltyAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    // --- MÉTHODES UTILITAIRES ---

    /**
     * Vérifie si un QR code a été généré pour cette liquidation
     */
    public boolean hasQrCode() {
        return qrCodeData != null && !qrCodeData.trim().isEmpty();
    }

    /**
     * Calcule le montant total (base + pénalités)
     */
    public BigDecimal calculateTotalAmount() {
        if (penaltyAmount == null || penaltyAmount.compareTo(BigDecimal.ZERO) == 0) {
            return amount;
        }
        return amount.add(penaltyAmount);
    }

    /**
     * Met à jour le montant total automatiquement
     */
    public void updateTotalAmount() {
        this.totalAmount = calculateTotalAmount();
    }
}


