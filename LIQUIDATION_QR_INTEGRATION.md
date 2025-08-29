# 🔗 Intégration QR Codes UEMOA - Liquidations

## 📋 Description

Cette fonctionnalité intègre la génération de QR codes UEMOA aux entités de liquidation existantes, permettant de créer des codes QR conformes aux normes EMVCo et BCEAO pour les paiements de taxes et impôts.

## ✅ Critères d'Acceptation Réalisés

### 1. **✅ Service de Mapping des Données**
- [x] Le service mappe les données de liquidation à `QRPaymentData`
- [x] Mapping du bénéficiaire (`Customer`) vers `MerchantInfo`
- [x] Conversion des montants et informations de transaction

### 2. **✅ Génération de QR Codes pour Liquidations**
- [x] QR codes statiques pour paiements simples
- [x] QR codes dynamiques avec références de transaction
- [x] QR codes P2P pour transferts entre particuliers
- [x] QR codes avec pénalités pour retards de paiement

### 3. **✅ Gestion des Erreurs et Validation**
- [x] Validation des liquidations avant génération
- [x] Gestion des paramètres invalides (null, vides)
- [x] Messages d'erreur explicites
- [x] Logs détaillés pour le debugging

## 🏗️ Architecture

### **Services Créés**

#### **LiquidationQRService** (Interface)
```java
public interface LiquidationQRService {
    // Génération de QR codes
    Map<String, Object> generateStaticQRForLiquidation(Liquidation liquidation);
    Map<String, Object> generateDynamicQRForLiquidation(Liquidation liquidation, String transactionReference);
    Map<String, Object> generateP2PQRForLiquidation(Liquidation liquidation, String beneficiaryPhone);
    Map<String, Object> generateQRWithPenalty(Liquidation liquidation, BigDecimal penaltyAmount);
    
    // Validation et mapping
    boolean validateLiquidationForQR(Liquidation liquidation);
    MerchantInfo mapCustomerToMerchantInfo(Customer customer);
    QRPaymentData mapLiquidationToQRPaymentData(Liquidation liquidation);
    String generateTransactionReference(Liquidation liquidation);
}
```

#### **LiquidationQRServiceImpl** (Implémentation)
- **Mapping automatique** : `Customer` → `MerchantInfo`
- **Validation robuste** : Vérification des données de liquidation
- **Génération de références** : Format `LIQ-{ID}-{TIMESTAMP}-{UUID}`
- **Extraction de ville** : Logique intelligente depuis l'adresse

### **Contrôleur REST**

#### **LiquidationQRController**
```java
@RestController
@RequestMapping("/api/liquidations/{liquidationId}/qr")
public class LiquidationQRController {
    // Endpoints disponibles
    POST /static          // QR code statique
    POST /dynamic         // QR code dynamique
    POST /p2p            // QR code P2P
    POST /penalty        // QR code avec pénalités
    GET  /reference      // Génération de référence
    GET  /validate       // Validation de liquidation
}
```

## 🔄 Mapping des Données

### **Customer → MerchantInfo**
```java
Customer customer = liquidation.getCustomer();

MerchantInfo merchantInfo = MerchantInfo.builder()
    .name(customer.getFirstName() + " " + customer.getLastName())
    .city(extractCityFromAddress(customer.getAddress()))
    .countryCode(uemoaConfig.getCountryCode()) // "CI"
    .categoryCode(uemoaConfig.getMerchantCategoryCode()) // "0000"
    .alias(customer.getIfu()) // Identifiant fiscal unique
    .build();
```

### **Liquidation → QRPaymentData**
```java
QRPaymentData paymentData = QRPaymentData.builder()
    .merchantInfo(mapCustomerToMerchantInfo(liquidation.getCustomer()))
    .amount(liquidation.getAmount())
    .transactionId(transactionReference) // Pour QR dynamiques
    .build();
```

## 🚀 API Endpoints

### **1. QR Code Statique**
```http
POST /api/liquidations/{liquidationId}/qr/static
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "success": true,
  "qrData": {
    "qrCode": "00020101021136320012int.bceao.pi...",
    "liquidationId": 1,
    "customerName": "Jean Dupont",
    "amount": 50000.00,
    "currency": "XOF",
    "taxType": "TVA",
    "dueDate": "2025-09-27",
    "type": "STATIC",
    "generatedAt": "2025-08-28T12:40:17.974"
  },
  "message": "QR code statique généré avec succès pour la liquidation"
}
```

### **2. QR Code Dynamique**
```http
POST /api/liquidations/{liquidationId}/qr/dynamic
Authorization: Bearer {token}
Content-Type: application/json

{
  "transactionReference": "REF-123456"
}
```

**Réponse :**
```json
{
  "success": true,
  "qrData": {
    "qrCode": "00020101021236320012int.bceao.pi...",
    "liquidationId": 1,
    "customerName": "Jean Dupont",
    "amount": 50000.00,
    "currency": "XOF",
    "taxType": "TVA",
    "dueDate": "2025-09-27",
    "transactionReference": "REF-123456",
    "type": "DYNAMIC",
    "generatedAt": "2025-08-28T12:40:18.030"
  },
  "message": "QR code dynamique généré avec succès pour la liquidation"
}
```

### **3. QR Code P2P**
```http
POST /api/liquidations/{liquidationId}/qr/p2p
Authorization: Bearer {token}
Content-Type: application/json

{
  "beneficiaryPhone": "+2250701234567"
}
```

**Réponse :**
```json
{
  "success": true,
  "qrData": {
    "qrCode": "00020101021236320012int.bceao.pi...",
    "liquidationId": 1,
    "customerName": "Jean Dupont",
    "amount": 50000.00,
    "currency": "XOF",
    "taxType": "TVA",
    "beneficiaryPhone": "+2250701234567",
    "p2pReference": "P2P-1-+2250701234567",
    "type": "P2P",
    "generatedAt": "2025-08-28T12:40:18.043"
  },
  "message": "QR code P2P généré avec succès pour la liquidation"
}
```

### **4. QR Code avec Pénalités**
```http
POST /api/liquidations/{liquidationId}/qr/penalty
Authorization: Bearer {token}
Content-Type: application/json

{
  "penaltyAmount": 5000.00
}
```

**Réponse :**
```json
{
  "success": true,
  "qrData": {
    "qrCode": "00020101021236320012int.bceao.pi...",
    "liquidationId": 1,
    "customerName": "Jean Dupont",
    "baseAmount": 50000.00,
    "penaltyAmount": 5000.00,
    "totalAmount": 55000.00,
    "currency": "XOF",
    "taxType": "TVA",
    "dueDate": "2025-09-27",
    "penaltyReference": "PENALTY-1-1756384818051",
    "type": "PENALTY",
    "generatedAt": "2025-08-28T12:40:18.060"
  },
  "message": "QR code avec pénalités généré avec succès pour la liquidation"
}
```

### **5. Génération de Référence**
```http
GET /api/liquidations/{liquidationId}/qr/reference
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "success": true,
  "transactionReference": "LIQ-1-20250828124018-c0f3854f",
  "liquidationId": 1,
  "message": "Référence de transaction générée avec succès"
}
```

### **6. Validation de Liquidation**
```http
GET /api/liquidations/{liquidationId}/qr/validate
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "success": true,
  "isValid": true,
  "liquidationId": 1,
  "message": "Liquidation valide pour la génération de QR code"
}
```

## 🔍 Validation des Liquidations

### **Critères de Validation**
- ✅ Liquidation non null
- ✅ ID de liquidation présent
- ✅ Client associé non null
- ✅ Montant positif
- ✅ Statut différent de `PAID`
- ✅ Type de taxe renseigné

### **Exemples de Validation**
```java
// Liquidation valide
Liquidation validLiquidation = new Liquidation();
validLiquidation.setId(1L);
validLiquidation.setCustomer(customer);
validLiquidation.setAmount(new BigDecimal("50000.00"));
validLiquidation.setStatus(LiquidationStatus.PENDING);
validLiquidation.setTaxType("TVA");

boolean isValid = liquidationQRService.validateLiquidationForQR(validLiquidation);
// Résultat: true

// Liquidation invalide (déjà payée)
validLiquidation.setStatus(LiquidationStatus.PAID);
boolean isValid = liquidationQRService.validateLiquidationForQR(validLiquidation);
// Résultat: false
```

## 🧪 Tests

### **Tests d'Intégration**
```bash
# Exécuter tous les tests de liquidation QR
mvn test -Dtest=LiquidationQRServiceTest

# Résultats attendus :
# ✅ Service LiquidationQRService injecté avec succès
# ✅ Validation de liquidation réussie
# ✅ Mapping Customer vers MerchantInfo réussi
# ✅ Mapping Liquidation vers QRPaymentData réussi
# ✅ Génération de référence de transaction réussie
# ✅ Génération de QR code statique réussie
# ✅ Génération de QR code dynamique réussie
# ✅ Génération de QR code P2P réussie
# ✅ Génération de QR code avec pénalités réussie
# ✅ Validation d'erreur pour liquidation invalide réussie
# ✅ Validation d'erreur pour référence invalide réussie
```

### **Tests de Validation**
- ✅ **Liquidation valide** : Génération réussie
- ✅ **Liquidation null** : Exception `IllegalArgumentException`
- ✅ **Liquidation déjà payée** : Validation échoue
- ✅ **Montant négatif** : Validation échoue
- ✅ **Client manquant** : Validation échoue

## 🔧 Configuration

### **Paramètres UEMOA**
```yaml
uemoa:
  qr:
    country-code: CI                    # Côte d'Ivoire
    currency: XOF                       # Franc CFA
    merchant-category-code: 0000        # Code catégorie par défaut
    merchant-name: "LIQUIDATION APP"    # Nom par défaut
    merchant-city: "Abidjan"            # Ville par défaut
```

### **Sécurité**
- **QR Statique/Dynamique** : `ROLE_ADMIN` ou `ROLE_USER`
- **QR avec Pénalités** : `ROLE_ADMIN` uniquement
- **Validation/Référence** : `ROLE_ADMIN` ou `ROLE_USER`

## 📊 Formats de Référence

### **Référence de Transaction**
```
Format: LIQ-{ID}-{TIMESTAMP}-{UUID}
Exemple: LIQ-1-20250828124018-c0f3854f
```

### **Référence P2P**
```
Format: P2P-{LIQUIDATION_ID}-{PHONE}
Exemple: P2P-1-+2250701234567
```

### **Référence Pénalité**
```
Format: PENALTY-{LIQUIDATION_ID}-{TIMESTAMP}
Exemple: PENALTY-1-1756384818051
```

## 🚨 Gestion d'Erreurs

### **Erreurs de Validation**
```json
{
  "success": false,
  "error": "Liquidation invalide pour la génération de QR code"
}
```

### **Erreurs de Paramètres**
```json
{
  "success": false,
  "error": "La liquidation ne peut pas être null"
}
```

### **Erreurs Internes**
```json
{
  "success": false,
  "error": "Erreur interne lors de la génération du QR code"
}
```

## 📈 Métriques et Logs

### **Logs de Génération**
```
INFO  - Génération d'un QR code statique pour la liquidation ID: 1
DEBUG - Liquidation ID: 1 validée avec succès
INFO  - QR code statique généré avec succès pour la liquidation ID: 1
```

### **Logs d'Erreur**
```
WARN  - Liquidation déjà payée pour l'ID: 1
ERROR - Erreur lors de la génération du QR code statique pour la liquidation ID: 1
```

## 🔄 Workflow d'Utilisation

### **1. Création d'une Liquidation**
```java
Liquidation liquidation = new Liquidation();
liquidation.setCustomer(customer);
liquidation.setAmount(new BigDecimal("50000.00"));
liquidation.setTaxType("TVA");
liquidation.setDueDate(LocalDate.now().plusDays(30));
liquidation = liquidationService.create(liquidation);
```

### **2. Validation de la Liquidation**
```http
GET /api/liquidations/{liquidationId}/qr/validate
```

### **3. Génération du QR Code**
```http
POST /api/liquidations/{liquidationId}/qr/static
```

### **4. Utilisation du QR Code**
- Le QR code généré peut être scanné par les applications de paiement UEMOA
- Les données de transaction sont automatiquement remplies
- Le paiement est tracé avec la référence de liquidation

## 🎯 Avantages

### **Pour les Contribuables**
- ✅ Paiement simplifié via QR code
- ✅ Pas de saisie manuelle des données
- ✅ Traçabilité complète des transactions
- ✅ Support des pénalités automatique

### **Pour l'Administration**
- ✅ Intégration transparente avec UEMOA
- ✅ Conformité aux normes EMVCo/BCEAO
- ✅ Gestion centralisée des liquidations
- ✅ Logs détaillés pour l'audit

### **Pour les Développeurs**
- ✅ API REST simple et intuitive
- ✅ Validation automatique des données
- ✅ Gestion d'erreurs robuste
- ✅ Tests complets et automatisés

---

**📅 Date d'Implémentation** : 28 Août 2025  
**✅ Statut** : **INTÉGRATION TERMINÉE ET TESTÉE**  
**🚀 Prêt pour la Production** : **OUI**
