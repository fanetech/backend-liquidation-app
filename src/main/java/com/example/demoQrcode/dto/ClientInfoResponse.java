package com.example.demoQrcode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour les informations client retournées par l'API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientInfoResponse {
    
    /**
     * Statut de la récupération
     */
    private boolean success;
    
    /**
     * Message de statut
     */
    private String message;
    
    /**
     * Informations du client
     */
    private ClientData client;
    
    /**
     * Données de la transaction
     */
    private TransactionData transaction;
    
    /**
     * Données du client
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientData {
        /**
         * Identifiant unique du client
         */
        private String clientId;
        
        /**
         * Nom du client
         */
        private String name;
        
        /**
         * Prénom du client
         */
        private String firstName;
        
        /**
         * Email du client
         */
        private String email;
        
        /**
         * Téléphone du client
         */
        private String phone;
        
        /**
         * Adresse du client
         */
        private String address;
        
        /**
         * Ville du client
         */
        private String city;
        
        /**
         * Code postal du client
         */
        private String postalCode;
        
        /**
         * Pays du client
         */
        private String country;
        
        /**
         * Type de client (PARTICULIER, ENTREPRISE)
         */
        private String clientType;
        
        /**
         * Date de création du client
         */
        private LocalDateTime createdAt;
    }
    
    /**
     * Données de la transaction
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionData {
        /**
         * Identifiant de la transaction
         */
        private String transactionId;
        
        /**
         * Montant de la transaction
         */
        private BigDecimal amount;
        
        /**
         * Devise
         */
        private String currency;
        
        /**
         * Statut de la transaction
         */
        private String status;
        
        /**
         * Type de transaction
         */
        private String type;
        
        /**
         * Description de la transaction
         */
        private String description;
        
        /**
         * Horodatage de la transaction
         */
        private LocalDateTime timestamp;
        
        /**
         * Référence de paiement
         */
        private String paymentReference;
    }
    
    /**
     * Méthode de construction pour succès
     */
    public static ClientInfoResponse success(String message, ClientData client, TransactionData transaction) {
        return ClientInfoResponse.builder()
                .success(true)
                .message(message)
                .client(client)
                .transaction(transaction)
                .build();
    }
    
    /**
     * Méthode de construction pour erreur
     */
    public static ClientInfoResponse error(String message) {
        return ClientInfoResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
