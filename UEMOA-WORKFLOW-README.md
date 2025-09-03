# Workflow UEMOA QR Code - Implémentation Complète

## 🎯 Vue d'ensemble

Ce projet implémente un workflow complet de génération et traitement de QR codes UEMOA selon les standards de paiement instantané de la BCEAO. Le workflow suit le processus : **QR → Scan → Link → API → Client Info**.

## 🚀 Workflow Implémenté

### 1. Génération du QR Code UEMOA
- **Endpoint** : `POST /api/uemoa-workflow/generate`
- **Fonctionnalité** : Génère un QR code conforme aux standards UEMOA
- **Utilise** : Le module UEMOA officiel comme source unique de vérité

### 2. Scan du QR Code
- **Processus** : L'utilisateur scanne le QR code avec son application mobile
- **Résultat** : Obtient un lien avec informations client encodées

### 3. Génération du Lien
- **Contenu** : Informations client encodées en Base64
- **Format** : `clientId:amount:timestamp`
- **Déclencheur** : API call automatique

### 4. Clic sur le Lien
- **Action** : L'utilisateur clique sur le lien généré
- **Résultat** : Déclenchement automatique de l'API

### 5. Appel API
- **Endpoint** : `GET /api/uemoa-workflow/client-info/{encodedClientInfo}`
- **Méthode** : GET
- **Format** : JSON

### 6. Affichage des Informations Client
- **Contenu** : Données client complètes + informations transaction
- **Format** : Réponse JSON structurée

## 🔧 Architecture Technique

### Composants Principaux

#### 1. **UemoaWorkflowService**
- Service principal orchestrant le workflow complet
- Intégration avec le module UEMOA officiel
- Gestion de la génération des liens et du décodage

#### 2. **UemoaWorkflowController**
- Contrôleur REST exposant les endpoints du workflow
- Gestion des erreurs et validation
- Endpoints de santé et de statut

#### 3. **DTOs de Workflow**
- `UemoaQRWorkflowRequest` : Requête de génération
- `UemoaQRWorkflowResponse` : Réponse avec QR code et lien
- `ClientInfoResponse` : Informations client retournées

### Intégration UEMOA

```java
// Utilisation exclusive du module UEMOA officiel
@Autowired
private UemoaQRService uemoaQRService;

// Génération selon les standards UEMOA
String qrData = uemoaQRService.generateQRData(paymentData);
String qrImage = uemoaQRService.generateQRImage(paymentData);
```

## 📡 Endpoints API

### Génération du Workflow
```http
POST /api/uemoa-workflow/generate
Content-Type: application/json

{
  "amount": 5000,
  "merchantName": "LIQUIDATION APP",
  "clientInfo": "123",
  "qrType": "STATIC",
  "transactionReference": "TXN-001"
}
```

### Récupération des Informations Client
```http
GET /api/uemoa-workflow/client-info/{encodedClientInfo}
```

### Vérification de Santé
```http
GET /api/uemoa-workflow/health
```

### Statut du Workflow
```http
GET /api/uemoa-workflow/status
```

## 🔄 Exemple de Workflow Complet

### 1. Génération
```bash
curl -X POST http://localhost:8080/api/uemoa-workflow/generate \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 5000,
    "clientInfo": "123",
    "qrType": "STATIC"
  }'
```

**Réponse** :
```json
{
  "success": true,
  "message": "QR code UEMOA généré avec succès et workflow préparé",
  "qrCode": {
    "qrData": "00020101021226580014com.aveplus.uemoa...",
    "qrImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
    "qrType": "STATIC",
    "amount": 5000,
    "currency": "XOF",
    "merchantName": "LIQUIDATION APP",
    "merchantCity": "Abidjan",
    "countryCode": "CI"
  },
  "generatedLink": "test-terminal/client-info/MTIzOjUwMDA6MjAyNC0wOS0wMlQxNToyMzoxMC4xMjM="
}
```

### 2. Scan et Clic
- L'utilisateur scanne le QR code
- Obtient le lien : `test-terminal/client-info/MTIzOjUwMDA6MjAyNC0wOS0wMlQxNToyMzoxMC4xMjM=`
- Clique sur le lien

### 3. Récupération des Informations
```bash
curl http://localhost:8080/api/uemoa-workflow/client-info/MTIzOjUwMDA6MjAyNC0wOS0wMlQxNToyMzoxMC4xMjM=
```

**Réponse** :
```json
{
  "success": true,
  "message": "Informations client récupérées avec succès",
  "client": {
    "clientId": "123",
    "name": "Dupont",
    "firstName": "Jean",
    "email": "jean.dupont@example.com",
    "phone": "+22501234567",
    "address": "123 Rue de la Paix",
    "city": "Abidjan",
    "country": "CI"
  },
  "transaction": {
    "transactionId": "uuid-generated",
    "amount": 5000,
    "currency": "XOF",
    "status": "PENDING",
    "type": "UEMOA_QR_PAYMENT"
  }
}
```

## 🧪 Tests

### Tests d'Intégration
```bash
mvn test -Dtest=UemoaWorkflowIntegrationTest
```

### Tests Inclus
- ✅ Génération de QR codes statiques et dynamiques
- ✅ Validation des montants selon les règles UEMOA
- ✅ Gestion des erreurs et types invalides
- ✅ Complétude du workflow

## 🔒 Sécurité et Validation

### Validation des Données
- Montants selon les limites UEMOA (1-999999 centimes)
- Types de QR codes supportés (STATIC, DYNAMIC)
- Informations client obligatoires

### Conformité UEMOA
- Utilisation exclusive du module officiel
- Respect des standards EMVCo
- Support des pays UEMOA (CI, BF, TG, SN, ML, BJ, GW, NE)

## 🗑️ Nettoyage Effectué

### Services Supprimés
- ❌ `UemoaQRIntegrationService` (ancien)
- ❌ `LiquidationQRService` (ancien)
- ❌ `LiquidationQRDataService` (ancien)

### Contrôleurs Supprimés
- ❌ `UemoaQRController` (ancien)
- ❌ `LiquidationQRController` (ancien)
- ❌ `LiquidationQRDataController` (ancien)

### Endpoints Supprimés
- ❌ `POST /api/liquidations/{id}/generate-qr`
- ❌ `PUT /api/liquidations/{id}/regenerate-qr`
- ❌ `GET /api/liquidations/{id}/qr-image`

## 🚀 Démarrage Rapide

### 1. Vérification de l'Installation
```bash
curl http://localhost:8080/api/uemoa-workflow/health
```

### 2. Test de Génération
```bash
curl -X POST http://localhost:8080/api/uemoa-workflow/generate \
  -H "Content-Type: application/json" \
  -d '{"amount": 1000, "clientInfo": "test", "qrType": "STATIC"}'
```

### 3. Vérification du Statut
```bash
curl http://localhost:8080/api/uemoa-workflow/status
```

## 📋 Checklist de Conformité

- ✅ **Module UEMOA** : Utilisé comme source unique de vérité
- ✅ **Standards UEMOA** : Conformité complète aux normes BCEAO
- ✅ **Workflow Complet** : QR → Scan → Link → API → Client Info
- ✅ **Anciens Endpoints** : Supprimés et remplacés
- ✅ **Tests** : Couverture complète du workflow
- ✅ **Documentation** : API et workflow documentés
- ✅ **Sécurité** : Validation et gestion d'erreurs

## 🔗 Liens Utiles

- **Module UEMOA** : [GitHub Repository](https://github.com/fanetech/UEMOA-QR-code-payment-module)
- **Standards BCEAO** : [Documentation Officielle](https://www.bceao.int)
- **Tests** : `src/test/java/com/example/demoQrcode/UemoaWorkflowIntegrationTest.java`

---

**Note** : Cette implémentation respecte strictement les exigences UEMOA et utilise exclusivement le module officiel fourni pour la génération de QR codes.
