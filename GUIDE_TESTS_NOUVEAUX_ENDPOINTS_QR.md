# 🧪 Guide de Test des Nouveaux Endpoints QR du Contrôleur de Liquidation

## 📋 Vue d'ensemble

Ce guide détaille les tests pour les nouveaux endpoints REST ajoutés au contrôleur de liquidation existant pour la génération et la récupération de codes QR.

## 🎯 Endpoints Testés

### 1. **POST /api/liquidations/{id}/generate-qr**
Génère un QR code pour une liquidation spécifique

### 2. **GET /api/liquidations/{id}/qr-image**
Récupère l'image QR d'une liquidation

### 3. **PUT /api/liquidations/{id}/regenerate-qr**
Régénère un QR code pour une liquidation

## 🚀 Préparation des Tests

### **1. Compilation et Démarrage**
```bash
# Compilation du projet
mvn clean compile

# Démarrage de l'application
mvn spring-boot:run
```

### **2. Token d'Authentification**
```bash
# Récupération du token JWT (remplacez par votre méthode d'authentification)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# Sauvegardez le token retourné
export AUTH_TOKEN="votre_token_jwt_ici"
```

## 📝 Tests Détaillés

### **Test 1 : Génération de QR Code Statique**

**Endpoint :** `POST /api/liquidations/{id}/generate-qr`

**Requête :**
```bash
curl -X POST http://localhost:8080/api/liquidations/1/generate-qr \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $AUTH_TOKEN" \
  -d '{
    "qrType": "STATIC"
  }'
```

**Réponse attendue :**
```json
{
  "success": true,
  "message": "QR code STATIC généré avec succès",
  "qrCode": "00020101021226580014com.aveplus.uemoa0112int.bceao.pi52045XXX5303360540550005802CI5913LIQUIDATION APP6007Abidjan6304",
  "qrType": "STATIC",
  "liquidationId": 1,
  "customerName": "John Doe",
  "amount": 50000.00,
  "penaltyAmount": null,
  "totalAmount": 50000.00,
  "currency": "XOF",
  "taxType": "TVA",
  "transactionId": "LIQ-1-20241201120000-ABC12345",
  "merchantChannel": "int.bceao.pi",
  "generatedAt": "2024-12-01T12:00:00"
}
```

### **Test 2 : Génération de QR Code Dynamique**

**Requête :**
```bash
curl -X POST http://localhost:8080/api/liquidations/1/generate-qr \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $AUTH_TOKEN" \
  -d '{
    "qrType": "DYNAMIC",
    "transactionReference": "REF123456"
  }'
```

**Réponse attendue :**
```json
{
  "success": true,
  "message": "QR code DYNAMIC généré avec succès",
  "qrCode": "00020101021226580014com.aveplus.uemoa0112int.bceao.pi52045XXX5303360540550005802CI5913LIQUIDATION APP6007Abidjan6304",
  "qrType": "DYNAMIC",
  "liquidationId": 1,
  "customerName": "John Doe",
  "amount": 50000.00,
  "transactionId": "LIQ-1-20241201120000-ABC12345",
  "merchantChannel": "int.bceao.pi",
  "generatedAt": "2024-12-01T12:00:00"
}
```

### **Test 3 : Génération de QR Code P2P**

**Requête :**
```bash
curl -X POST http://localhost:8080/api/liquidations/1/generate-qr \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $AUTH_TOKEN" \
  -d '{
    "qrType": "P2P"
  }'
```

**Réponse attendue :**
```json
{
  "success": true,
  "message": "QR code P2P généré avec succès",
  "qrCode": "00020101021226580014com.aveplus.uemoa0112int.bceao.pi52045XXX5303360540550005802CI5913LIQUIDATION APP6007Abidjan6304",
  "qrType": "P2P",
  "liquidationId": 1,
  "customerName": "John Doe",
  "amount": 50000.00,
  "transactionId": "LIQ-1-20241201120000-ABC12345",
  "merchantChannel": "int.bceao.pi",
  "generatedAt": "2024-12-01T12:00:00"
}
```

### **Test 4 : Génération de QR Code avec Pénalités**

**Requête :**
```bash
curl -X POST http://localhost:8080/api/liquidations/1/generate-qr \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $AUTH_TOKEN" \
  -d '{
    "qrType": "PENALTY",
    "penaltyAmount": 5000.00
  }'
```

**Réponse attendue :**
```json
{
  "success": true,
  "message": "QR code PENALTY généré avec succès",
  "qrCode": "00020101021226580014com.aveplus.uemoa0112int.bceao.pi52045XXX5303360540550005802CI5913LIQUIDATION APP6007Abidjan6304",
  "qrType": "PENALTY",
  "liquidationId": 1,
  "customerName": "John Doe",
  "amount": 50000.00,
  "penaltyAmount": 5000.00,
  "totalAmount": 55000.00,
  "currency": "XOF",
  "taxType": "TVA",
  "transactionId": "LIQ-1-20241201120000-ABC12345",
  "merchantChannel": "int.bceao.pi",
  "generatedAt": "2024-12-01T12:00:00"
}
```

### **Test 5 : Récupération de l'Image QR**

**Endpoint :** `GET /api/liquidations/{id}/qr-image`

**Requête :**
```bash
curl -X GET http://localhost:8080/api/liquidations/1/qr-image \
  -H "Authorization: Bearer $AUTH_TOKEN"
```

**Réponse attendue :**
```json
{
  "success": true,
  "message": "Image QR récupérée avec succès",
  "qrImageBase64": "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==",
  "contentType": "image/png",
  "imageSize": 88,
  "qrType": "STATIC",
  "generatedAt": "2024-12-01T12:00:00",
  "liquidationId": 1,
  "transactionId": "LIQ-1-20241201120000-ABC12345"
}
```

### **Test 6 : Régénération de QR Code**

**Endpoint :** `PUT /api/liquidations/{id}/regenerate-qr`

**Requête :**
```bash
curl -X PUT http://localhost:8080/api/liquidations/1/regenerate-qr \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $AUTH_TOKEN" \
  -d '{
    "qrType": "DYNAMIC",
    "transactionReference": "NEW_REF"
  }'
```

**Réponse attendue :**
```json
{
  "success": true,
  "message": "QR code DYNAMIC régénéré avec succès",
  "qrCode": "00020101021226580014com.aveplus.uemoa0112int.bceao.pi52045XXX5303360540550005802CI5913LIQUIDATION APP6007Abidjan6304",
  "qrType": "DYNAMIC",
  "liquidationId": 1,
  "customerName": "John Doe",
  "amount": 50000.00,
  "transactionId": "LIQ-1-20241201120000-NEW12345",
  "merchantChannel": "int.bceao.pi",
  "generatedAt": "2024-12-01T12:05:00"
}
```

## 🚨 Tests d'Erreurs

### **Test 7 : Type de QR Code Invalide**

**Requête :**
```bash
curl -X POST http://localhost:8080/api/liquidations/1/generate-qr \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $AUTH_TOKEN" \
  -d '{
    "qrType": "INVALID_TYPE"
  }'
```

**Réponse attendue :**
```json
{
  "success": false,
  "message": "Erreur de validation",
  "error": "Type de QR code non supporté: INVALID_TYPE"
}
```

### **Test 8 : Liquidation Non Trouvée**

**Requête :**
```bash
curl -X POST http://localhost:8080/api/liquidations/99999/generate-qr \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $AUTH_TOKEN" \
  -d '{
    "qrType": "STATIC"
  }'
```

**Réponse attendue :**
```json
{
  "success": false,
  "message": "Liquidation non trouvée",
  "error": "Liquidation introuvable"
}
```

### **Test 9 : Image QR Sans QR Code Généré**

**Requête :**
```bash
curl -X GET http://localhost:8080/api/liquidations/1/qr-image \
  -H "Authorization: Bearer $AUTH_TOKEN"
```

**Réponse attendue :**
```json
{
  "success": false,
  "message": "Aucun QR code généré",
  "error": "Cette liquidation n'a pas de QR code généré"
}
```

### **Test 10 : Accès Non Autorisé**

**Requête :**
```bash
curl -X POST http://localhost:8080/api/liquidations/1/generate-qr \
  -H "Content-Type: application/json" \
  -d '{
    "qrType": "STATIC"
  }'
```

**Réponse attendue :**
```json
{
  "timestamp": "2024-12-01T12:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/liquidations/1/generate-qr"
}
```

## 🧪 Tests Automatisés

### **Exécution des Tests Unitaires**
```bash
# Test spécifique des nouveaux endpoints
mvn test -Dtest=LiquidationControllerQRTest

# Tous les tests
mvn test
```

### **Résultats Attendus**
```
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
```

## 📊 Validation des Critères

### ✅ **Critères d'Acceptation Validés**

1. **Point de terminaison POST /liquidations/{id}/generate-qr** ✅
   - Génère tous les types de QR (STATIC, DYNAMIC, P2P, PENALTY)
   - Validation des paramètres d'entrée
   - Gestion des erreurs

2. **Point de terminaison GET /liquidations/{id}/qr-image** ✅
   - Récupère l'image QR en Base64
   - Vérifie l'existence du QR code
   - Retourne les métadonnées

3. **Point de terminaison PUT /liquidations/{id}/regenerate-qr** ✅
   - Supprime l'ancien QR code
   - Génère un nouveau QR code
   - Met à jour les données

4. **Gestion et validation correctes des erreurs** ✅
   - Liquidation non trouvée
   - Type de QR invalide
   - Accès non autorisé
   - QR code inexistant

5. **DTO de requête/réponse** ✅
   - `QRGenerationRequest` avec validation
   - `QRGenerationResponse` structurée
   - `QRImageResponse` pour les images

## 🔧 Dépannage

### **Problèmes Courants**

1. **Erreur 403 Forbidden**
   - Vérifiez que le token JWT est valide
   - Assurez-vous d'avoir les bonnes autorisations (ROLE_ADMIN ou ROLE_USER)

2. **Erreur 404 Not Found**
   - Vérifiez que l'ID de liquidation existe
   - Assurez-vous que l'application est démarrée

3. **Erreur 400 Bad Request**
   - Vérifiez le format JSON de la requête
   - Assurez-vous que le type de QR est valide

4. **Erreur 500 Internal Server Error**
   - Vérifiez les logs de l'application
   - Assurez-vous que la base de données est accessible

### **Logs de Débogage**
```bash
# Activation des logs détaillés
curl -X POST http://localhost:8080/api/liquidations/1/generate-qr \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $AUTH_TOKEN" \
  -d '{"qrType": "STATIC"}' \
  -v
```

## 📈 Métriques de Performance

### **Temps de Réponse Attendus**
- **Génération QR** : < 2 secondes
- **Récupération Image** : < 1 seconde
- **Régénération QR** : < 3 secondes

### **Tests de Charge (Optionnel)**
```bash
# Test avec Apache Bench (si disponible)
ab -n 100 -c 10 -H "Authorization: Bearer $AUTH_TOKEN" \
  -H "Content-Type: application/json" \
  -p qr_request.json \
  http://localhost:8080/api/liquidations/1/generate-qr
```

## 🎉 Conclusion

Les nouveaux endpoints QR du contrôleur de liquidation sont maintenant opérationnels et testés. Ils offrent une interface REST complète pour la génération, la récupération et la régénération de codes QR UEMOA pour les liquidations.

**Fonctionnalités implémentées :**
- ✅ Génération de QR codes (STATIC, DYNAMIC, P2P, PENALTY)
- ✅ Récupération d'images QR
- ✅ Régénération de QR codes
- ✅ Validation et gestion d'erreurs
- ✅ DTOs structurés
- ✅ Tests automatisés complets
- ✅ Documentation détaillée
