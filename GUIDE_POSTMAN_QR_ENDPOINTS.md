# Guide Postman - Tests des Endpoints QR du LiquidationController

## 📋 Prérequis

### 1. Démarrer l'Application
```bash
mvn spring-boot:run
```

### 2. Obtenir un Token JWT
Avant de tester les endpoints QR, vous devez vous authentifier :

**Endpoint d'authentification :**
```
POST http://localhost:8080/api/auth/login
```

**Body (JSON) :**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Réponse attendue :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

### 3. Créer une Liquidation de Test
**Endpoint :**
```
POST http://localhost:8080/api/liquidations
```

**Headers :**
```
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

**Body (JSON) :**
```json
{
  "customer": {
    "firstName": "John",
    "lastName": "Doe",
    "address": "123 Main Street, Abidjan, Côte d'Ivoire",
    "ifu": "IFU123456789",
    "phone": "+22512345678",
    "email": "john.doe@example.com"
  },
  "taxType": "TVA",
  "amount": 50000.00,
  "issueDate": "2024-01-15",
  "dueDate": "2024-02-15"
}
```

**Notez l'ID de la liquidation créée** (ex: `1`)

---

## 🚀 Tests des Endpoints QR

### Configuration Postman

#### 1. Créer un Environnement
1. Cliquez sur l'icône d'engrenage (⚙️) en haut à droite
2. Cliquez sur "Add" pour créer un nouvel environnement
3. Nommez-le "Liquidation QR Tests"
4. Ajoutez ces variables :

| Variable | Valeur Initiale | Description |
|----------|-----------------|-------------|
| `base_url` | `http://localhost:8080` | URL de base de l'API |
| `jwt_token` | (vide) | Token JWT d'authentification |
| `liquidation_id` | `1` | ID de la liquidation de test |

#### 2. Créer une Collection
1. Cliquez sur "New" → "Collection"
2. Nommez-la "Liquidation QR Endpoints"
3. Sélectionnez l'environnement créé

---

## 📝 Tests Détaillés

### Test 1 : Génération de QR Code Statique

#### Configuration de la Requête
- **Méthode :** `POST`
- **URL :** `{{base_url}}/api/liquidations/{{liquidation_id}}/generate-qr`

#### Headers
```
Authorization: Bearer {{jwt_token}}
Content-Type: application/json
```

#### Body (JSON)
```json
{
  "qrType": "STATIC"
}
```

#### Tests Postman (onglet "Tests")
```javascript
// Vérification du statut HTTP
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// Vérification de la structure de la réponse
pm.test("Response has correct structure", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('success');
    pm.expect(jsonData).to.have.property('message');
    pm.expect(jsonData).to.have.property('qrCode');
    pm.expect(jsonData).to.have.property('qrType');
    pm.expect(jsonData).to.have.property('liquidationId');
});

// Vérification du succès
pm.test("Success is true", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.success).to.eql(true);
});

// Vérification du type de QR
pm.test("QR type is STATIC", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.qrType).to.eql("STATIC");
});

// Vérification de la génération du QR code
pm.test("QR code is generated", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.qrCode).to.be.a('string');
    pm.expect(jsonData.qrCode.length).to.be.greaterThan(0);
    pm.expect(jsonData.qrCode).to.include('0002010102');
});

// Sauvegarde du QR code pour les tests suivants
pm.test("Save QR code data", function () {
    var jsonData = pm.response.json();
    pm.environment.set("qr_code_data", jsonData.qrCode);
    pm.environment.set("qr_type", jsonData.qrType);
});
```

#### Réponse Attendue
```json
{
  "success": true,
  "message": "QR code STATIC généré avec succès",
  "qrCode": "00020101021136360012int.bceao.pi...",
  "qrType": "STATIC",
  "liquidationId": 1,
  "customerName": "John Doe",
  "amount": 50000.00,
  "currency": "XOF",
  "taxType": "TVA",
  "transactionId": "STATIC-1-1234567890",
  "merchantChannel": "default",
  "generatedAt": "2024-01-15T10:30:00"
}
```

---

### Test 2 : Génération de QR Code Dynamique

#### Configuration de la Requête
- **Méthode :** `POST`
- **URL :** `{{base_url}}/api/liquidations/{{liquidation_id}}/generate-qr`

#### Headers
```
Authorization: Bearer {{jwt_token}}
Content-Type: application/json
```

#### Body (JSON)
```json
{
  "qrType": "DYNAMIC",
  "transactionReference": "REF123456789"
}
```

#### Tests Postman
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Success is true", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.success).to.eql(true);
});

pm.test("QR type is DYNAMIC", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.qrType).to.eql("DYNAMIC");
});

pm.test("Transaction reference is set", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.transactionId).to.eql("REF123456789");
});

pm.test("QR code is generated", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.qrCode).to.be.a('string');
    pm.expect(jsonData.qrCode.length).to.be.greaterThan(0);
});
```

---

### Test 3 : Génération de QR Code P2P

#### Configuration de la Requête
- **Méthode :** `POST`
- **URL :** `{{base_url}}/api/liquidations/{{liquidation_id}}/generate-qr`

#### Headers
```
Authorization: Bearer {{jwt_token}}
Content-Type: application/json
```

#### Body (JSON)
```json
{
  "qrType": "P2P"
}
```

#### Tests Postman
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Success is true", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.success).to.eql(true);
});

pm.test("QR type is P2P", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.qrType).to.eql("P2P");
});

pm.test("Transaction ID contains P2P prefix", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.transactionId).to.include("P2P");
});
```

---

### Test 4 : Génération de QR Code avec Pénalités

#### Configuration de la Requête
- **Méthode :** `POST`
- **URL :** `{{base_url}}/api/liquidations/{{liquidation_id}}/generate-qr`

#### Headers
```
Authorization: Bearer {{jwt_token}}
Content-Type: application/json
```

#### Body (JSON)
```json
{
  "qrType": "PENALTY",
  "penaltyAmount": 5000.00
}
```

#### Tests Postman
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Success is true", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.success).to.eql(true);
});

pm.test("QR type is PENALTY", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.qrType).to.eql("PENALTY");
});

pm.test("Penalty amount is included", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.penaltyAmount).to.eql(5000.00);
});

pm.test("Total amount is calculated", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.totalAmount).to.eql(55000.00);
});
```

---

### Test 5 : Récupération d'Image QR

#### Configuration de la Requête
- **Méthode :** `GET`
- **URL :** `{{base_url}}/api/liquidations/{{liquidation_id}}/qr-image`

#### Headers
```
Authorization: Bearer {{jwt_token}}
```

#### Tests Postman
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Success is true", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.success).to.eql(true);
});

pm.test("QR image is present", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.qrImageBase64).to.be.a('string');
    pm.expect(jsonData.qrImageBase64.length).to.be.greaterThan(0);
});

pm.test("Content type is image/png", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.contentType).to.eql("image/png");
});

pm.test("Image size is reasonable", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.imageSize).to.be.greaterThan(100);
    pm.expect(jsonData.imageSize).to.be.lessThan(10000);
});
```

#### Réponse Attendue
```json
{
  "success": true,
  "message": "Image QR récupérée avec succès",
  "qrImageBase64": "iVBORw0KGgoAAAANSUhEUgAA...",
  "contentType": "image/png",
  "imageSize": 1234,
  "qrType": "STATIC",
  "generatedAt": "2024-01-15T10:30:00",
  "liquidationId": 1,
  "transactionId": "STATIC-1-1234567890"
}
```

---

### Test 6 : Régénération de QR Code

#### Configuration de la Requête
- **Méthode :** `PUT`
- **URL :** `{{base_url}}/api/liquidations/{{liquidation_id}}/regenerate-qr`

#### Headers
```
Authorization: Bearer {{jwt_token}}
Content-Type: application/json
```

#### Body (JSON)
```json
{
  "qrType": "DYNAMIC",
  "transactionReference": "NEW_REF_789"
}
```

#### Tests Postman
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Success is true", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.success).to.eql(true);
});

pm.test("QR code is regenerated", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.qrCode).to.be.a('string');
    pm.expect(jsonData.qrCode.length).to.be.greaterThan(0);
});

pm.test("New transaction reference is used", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.transactionId).to.eql("NEW_REF_789");
});

// Vérification que le QR code a changé
pm.test("QR code has changed", function () {
    var jsonData = pm.response.json();
    var oldQRCode = pm.environment.get("qr_code_data");
    pm.expect(jsonData.qrCode).to.not.eql(oldQRCode);
});
```

---

## 🚨 Tests d'Erreurs

### Test 7 : Liquidation Inexistante

#### Configuration de la Requête
- **Méthode :** `POST`
- **URL :** `{{base_url}}/api/liquidations/99999/generate-qr`

#### Headers
```
Authorization: Bearer {{jwt_token}}
Content-Type: application/json
```

#### Body (JSON)
```json
{
  "qrType": "STATIC"
}
```

#### Tests Postman
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Success is false", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.success).to.eql(false);
});

pm.test("Error message is correct", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.message).to.eql("Liquidation non trouvée");
    pm.expect(jsonData.error).to.eql("Liquidation introuvable");
});
```

### Test 8 : Type de QR Invalide

#### Configuration de la Requête
- **Méthode :** `POST`
- **URL :** `{{base_url}}/api/liquidations/{{liquidation_id}}/generate-qr`

#### Headers
```
Authorization: Bearer {{jwt_token}}
Content-Type: application/json
```

#### Body (JSON)
```json
{
  "qrType": "INVALID_TYPE"
}
```

#### Tests Postman
```javascript
pm.test("Status code is 400", function () {
    pm.response.to.have.status(400);
});

pm.test("Validation error is returned", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('errors');
});
```

### Test 9 : Image QR Inexistante

#### Configuration de la Requête
- **Méthode :** `GET`
- **URL :** `{{base_url}}/api/liquidations/{{liquidation_id}}/qr-image`

#### Headers
```
Authorization: Bearer {{jwt_token}}
```

#### Tests Postman
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Success is false", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.success).to.eql(false);
});

pm.test("Error message is correct", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.message).to.eql("Aucun QR code généré");
});
```

---

## 🔧 Configuration Avancée Postman

### Variables d'Environnement Dynamiques

Ajoutez ces scripts dans l'onglet "Pre-request Script" de chaque requête :

```javascript
// Génération automatique de références uniques
pm.environment.set("unique_ref", "REF" + Date.now());

// Génération automatique d'ID de liquidation
if (!pm.environment.get("liquidation_id")) {
    pm.environment.set("liquidation_id", "1");
}
```

### Tests Automatisés Globaux

Créez un dossier "Tests Globaux" dans votre collection avec ce script :

```javascript
// Test global pour toutes les requêtes
pm.test("Response time is less than 2000ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(2000);
});

pm.test("Response has JSON content type", function () {
    pm.expect(pm.response.headers.get("Content-Type")).to.include("application/json");
});
```

---

## 📊 Validation des QR Codes

### Vérification EMVCo
Les QR codes générés doivent respecter le standard EMVCo :

```javascript
pm.test("QR code follows EMVCo standard", function () {
    var jsonData = pm.response.json();
    var qrCode = jsonData.qrCode;
    
    // Vérification du préfixe EMVCo
    pm.expect(qrCode).to.match(/^0002010102/);
    
    // Vérification de la longueur minimale
    pm.expect(qrCode.length).to.be.greaterThan(50);
    
    // Vérification de la présence des champs obligatoires
    pm.expect(qrCode).to.include('int.bceao.pi');
    pm.expect(qrCode).to.include('XOF');
});
```

### Vérification BCEAO
```javascript
pm.test("QR code follows BCEAO standard", function () {
    var jsonData = pm.response.json();
    var qrCode = jsonData.qrCode;
    
    // Code pays CI (Côte d'Ivoire)
    pm.expect(qrCode).to.include('CI');
    
    // Devise XOF (Franc CFA)
    pm.expect(qrCode).to.include('XOF');
    
    // Format marchand BCEAO
    pm.expect(qrCode).to.include('int.bceao.pi');
});
```

---

## 🎯 Ordre d'Exécution Recommandé

1. **Authentification** - Obtenir le token JWT
2. **Création de liquidation** - Créer une liquidation de test
3. **Test 1** - Génération QR Statique
4. **Test 5** - Récupération Image QR
5. **Test 2** - Génération QR Dynamique
6. **Test 3** - Génération QR P2P
7. **Test 4** - Génération QR avec Pénalités
8. **Test 6** - Régénération QR
9. **Tests 7-9** - Tests d'erreurs

---

## ✅ Checklist de Validation

- [ ] Application démarrée sur `http://localhost:8080`
- [ ] Token JWT obtenu et configuré dans l'environnement
- [ ] Liquidation de test créée
- [ ] Tous les types de QR générés avec succès
- [ ] Images QR récupérées correctement
- [ ] Régénération fonctionne
- [ ] Gestion d'erreurs testée
- [ ] Validation EMVCo/BCEAO vérifiée
- [ ] Temps de réponse < 2 secondes
- [ ] Tous les tests Postman passent

---

## 🚀 Export de la Collection

Une fois tous les tests configurés, exportez votre collection :

1. Clic droit sur la collection
2. "Export"
3. Sélectionnez le format JSON
4. Sauvegardez le fichier

Vous pouvez ensuite partager cette collection avec votre équipe pour des tests cohérents.
