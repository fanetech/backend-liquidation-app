# Modifications de l'Entité Liquidation pour l'Intégration des QR Codes UEMOA

## Description

Ce document décrit les modifications apportées à l'entité `Liquidation` existante pour stocker les données du code QR et les intégrer aux champs UEMOA, conformément aux exigences du projet.

## Critères d'Acceptation Réalisés

✅ **Ajouter des champs liés au QR à l'entité de liquidation**  
✅ **Créer un script de migration pour les modifications de la base de données**  
✅ **Mettre à jour les référentiels existants si nécessaire**  
✅ **Maintenir la compatibilité ascendante**  

## Modifications Apportées

### 1. Entité Liquidation (`src/main/java/com/example/demoQrcode/entity/Liquidation.java`)

#### Nouveaux Champs Ajoutés

```java
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
```

#### Nouvelles Méthodes Utilitaires

```java
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
```

### 2. Script de Migration (`src/main/resources/db/migration/V2__Add_QR_Code_Fields_To_Liquidations.sql`)

```sql
-- Migration V2: Ajout des champs QR codes UEMOA à la table liquidations
-- Date: 2025-08-28
-- Description: Ajoute les champs nécessaires pour stocker les données QR codes UEMOA

-- Ajout des colonnes pour les données QR codes
ALTER TABLE liquidations 
ADD COLUMN qr_code_data TEXT,
ADD COLUMN qr_image_base64 TEXT,
ADD COLUMN merchant_channel VARCHAR(64),
ADD COLUMN transaction_id VARCHAR(128),
ADD COLUMN qr_type VARCHAR(16),
ADD COLUMN qr_generated_at TIMESTAMP,
ADD COLUMN penalty_amount DECIMAL(18,2),
ADD COLUMN total_amount DECIMAL(18,2);

-- Ajout d'index pour optimiser les requêtes
CREATE INDEX idx_liquidations_qr_type ON liquidations(qr_type);
CREATE INDEX idx_liquidations_transaction_id ON liquidations(transaction_id);
CREATE INDEX idx_liquidations_qr_generated_at ON liquidations(qr_generated_at);
CREATE INDEX idx_liquidations_merchant_channel ON liquidations(merchant_channel);

-- Ajout de contraintes de validation
ALTER TABLE liquidations 
ADD CONSTRAINT chk_qr_type CHECK (qr_type IN ('STATIC', 'DYNAMIC', 'P2P', 'PENALTY')),
ADD CONSTRAINT chk_penalty_amount CHECK (penalty_amount IS NULL OR penalty_amount >= 0),
ADD CONSTRAINT chk_total_amount CHECK (total_amount IS NULL OR total_amount >= 0);
```

### 3. Repository Mis à Jour (`src/main/java/com/example/demoQrcode/repository/LiquidationRepository.java`)

#### Nouvelles Méthodes de Recherche

```java
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
 * Trouve une liquidation par identifiant de transaction
 */
Optional<Liquidation> findByTransactionId(String transactionId);

/**
 * Trouve toutes les liquidations avec pénalités
 */
@Query("SELECT l FROM Liquidation l WHERE l.penaltyAmount IS NOT NULL AND l.penaltyAmount > 0")
List<Liquidation> findLiquidationsWithPenalties();

/**
 * Compte le nombre de liquidations avec QR code par type
 */
@Query("SELECT l.qrType, COUNT(l) FROM Liquidation l WHERE l.qrType IS NOT NULL GROUP BY l.qrType")
List<Object[]> countLiquidationsByQrType();
```

### 4. Service de Gestion des Données QR (`src/main/java/com/example/demoQrcode/service/LiquidationQRDataService.java`)

Interface définissant les opérations de gestion des données QR :

- **Opérations de recherche** : Trouver les liquidations avec/sans QR, par type, par période
- **Opérations de statistiques** : Compter, calculer les montants totaux
- **Opérations de maintenance** : Supprimer, mettre à jour les données QR
- **Opérations de validation** : Valider les données QR

### 5. Implémentation du Service (`src/main/java/com/example/demoQrcode/service/impl/LiquidationQRDataServiceImpl.java`)

Implémentation complète du service avec :
- Gestion des transactions
- Logging détaillé
- Validation des données
- Calculs automatiques des montants

### 6. Contrôleur pour les Données QR (`src/main/java/com/example/demoQrcode/controller/LiquidationQRDataController.java`)

API REST complète pour :
- **Recherche** : `/api/liquidations/qr-data/with-qr`, `/api/liquidations/qr-data/type/{qrType}`
- **Statistiques** : `/api/liquidations/qr-data/stats/count-by-type`, `/api/liquidations/qr-data/stats/total-amount`
- **Maintenance** : `/api/liquidations/qr-data/{liquidationId}`, `/api/liquidations/qr-data/update-all-totals`
- **Validation** : `/api/liquidations/qr-data/{liquidationId}/validate`

### 7. Service LiquidationQR Mis à Jour (`src/main/java/com/example/demoQrcode/service/impl/LiquidationQRServiceImpl.java`)

Mise à jour pour sauvegarder automatiquement les données QR dans l'entité :

```java
// Sauvegarde des données QR dans l'entité Liquidation
liquidation.setQrCodeData(qrCode);
liquidation.setQrType("STATIC");
liquidation.setQrGeneratedAt(LocalDateTime.now());
liquidation.setMerchantChannel(uemoaConfig.getPaymentSystem().getIdentifier());
liquidation.setTransactionId(generateTransactionReference(liquidation));

// Sauvegarde en base de données
liquidationRepository.save(liquidation);
```

## Compatibilité Ascendante

✅ **Maintenue** : Tous les champs existants restent inchangés  
✅ **Rétrocompatible** : Les anciennes méthodes continuent de fonctionner  
✅ **Migration automatique** : Script Flyway pour ajouter les nouveaux champs  
✅ **Tests existants** : Tous les tests existants continuent de passer  

## Tests

### Test Simple (`src/test/java/com/example/demoQrcode/LiquidationEntityQRSimpleTest.java`)

Tests unitaires pour vérifier :
- Initialisation des champs QR
- Setters et getters
- Méthodes utilitaires (`hasQrCode`, `calculateTotalAmount`, `updateTotalAmount`)
- Configuration complète d'une liquidation avec QR

**Résultat** : ✅ 6 tests passent avec succès

## Avantages des Modifications

### 1. **Stockage Persistant**
- Les données QR sont maintenant stockées en base de données
- Traçabilité complète des QR codes générés
- Historique des transactions

### 2. **Gestion des Pénalités**
- Support intégré des pénalités
- Calcul automatique des montants totaux
- Validation des montants

### 3. **Recherche et Filtrage**
- Recherche par type de QR (STATIC, DYNAMIC, P2P, PENALTY)
- Filtrage par période (aujourd'hui, cette semaine, ce mois)
- Recherche par transaction ID

### 4. **Statistiques et Rapports**
- Comptage par type de QR
- Calcul des montants totaux
- Suivi des liquidations avec pénalités

### 5. **API REST Complète**
- Endpoints pour toutes les opérations
- Gestion des erreurs
- Réponses standardisées

### 6. **Maintenance et Nettoyage**
- Suppression des données QR obsolètes
- Mise à jour des montants totaux
- Validation des données

## Utilisation

### Génération d'un QR Code avec Sauvegarde

```java
// Le service LiquidationQRService sauvegarde automatiquement
Map<String, Object> result = liquidationQRService.generateStaticQRForLiquidation(liquidation);

// Les données sont maintenant stockées dans l'entité
assertTrue(liquidation.hasQrCode());
assertEquals("STATIC", liquidation.getQrType());
assertNotNull(liquidation.getTransactionId());
```

### Recherche de Liquidations avec QR

```java
// Via le service
List<Liquidation> liquidationsWithQR = liquidationQRDataService.findLiquidationWithQrCode();

// Via l'API REST
GET /api/liquidations/qr-data/with-qr
```

### Statistiques

```java
// Comptage par type
Map<String, Long> counts = liquidationQRDataService.countLiquidationsByQrType();

// Montant total
BigDecimal totalAmount = liquidationQRDataService.calculateTotalAmountOfLiquidationsWithQrCode();
```

## Conclusion

Les modifications apportées à l'entité `Liquidation` permettent une intégration complète et robuste des QR codes UEMOA dans le système de liquidation existant. Toutes les exigences ont été satisfaites avec une approche qui maintient la compatibilité ascendante et fournit une base solide pour les fonctionnalités futures.

La solution est prête pour la production avec :
- ✅ Tests unitaires passants
- ✅ Script de migration créé
- ✅ API REST complète
- ✅ Documentation détaillée
- ✅ Gestion d'erreurs robuste
