# Guide de Tests Manuels - Nouveaux Endpoints QR du LiquidationController

## Vue d'ensemble

Ce guide détaille les tests manuels pour les nouveaux endpoints QR ajoutés au `LiquidationController` :

- `POST /api/liquidations/{id}/generate-qr` - Génération de QR codes
- `GET /api/liquidations/{id}/qr-image` - Récupération d'image QR
- `PUT /api/liquidations/{id}/regenerate-qr` - Régénération de QR codes

## Prérequis

1. **Application démarrée** : `mvn spring-boot:run`
2. **Token JWT** : Obtenir un token d'authentification
3. **Liquidation existante** : Créer une liquidation de test

## 1. Test de Génération de QR Code Statique

### Requête
```bash
curl -X POST "http://localhost:8080/api/liquidations/1/generate-qr" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "qrType": "STATIC"
  }'
```

### Réponse attendue
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
  "generatedAt": "2025-08-28T16:09:50"
}
```

## 2. Test de Génération de QR Code Dynamique

### Requête
```bash
curl -X POST "http://localhost:8080/api/liquidations/1/generate-qr" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "qrType": "DYNAMIC",
    "transactionReference": "REF123456"
  }'
```

### Réponse attendue
```json
{
  "success": true,
  "message": "QR code DYNAMIC généré avec succès",
  "qrCode": "00020101021236360012int.bceao.pi...",
  "qrType": "DYNAMIC",
  "liquidationId": 1,
  "customerName": "John Doe",
  "amount": 50000.00,
  "currency": "XOF",
  "taxType": "TVA",
  "transactionId": "REF123456",
  "merchantChannel": "default",
  "generatedAt": "2025-08-28T16:09:50"
}
```

## 3. Test de Génération de QR Code P2P

### Requête
```bash
curl -X POST "http://localhost:8080/api/liquidations/1/generate-qr" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "qrType": "P2P"
  }'
```

### Réponse attendue
```json
{
  "success": true,
  "message": "QR code P2P généré avec succès",
  "qrCode": "00020101021236360012int.bceao.pi...",
  "qrType": "P2P",
  "liquidationId": 1,
  "customerName": "John Doe",
  "amount": 50000.00,
  "currency": "XOF",
  "taxType": "TVA",
  "transactionId": "P2P-1-+22512345678",
  "merchantChannel": "default",
  "generatedAt": "2025-08-28T16:09:50"
}
```

## 4. Test de Génération de QR Code avec Pénalités

### Requête
```bash
curl -X POST "http://localhost:8080/api/liquidations/1/generate-qr" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "qrType": "PENALTY",
    "penaltyAmount": 5000.00
  }'
```

### Réponse attendue
```json
{
  "success": true,
  "message": "QR code PENALTY généré avec succès",
  "qrCode": "00020101021236360012int.bceao.pi...",
  "qrType": "PENALTY",
  "liquidationId": 1,
  "customerName": "John Doe",
  "amount": 50000.00,
  "penaltyAmount": 5000.00,
  "totalAmount": 55000.00,
  "currency": "XOF",
  "taxType": "TVA",
  "transactionId": "PENALTY-1-1234567890",
  "merchantChannel": "default",
  "generatedAt": "2025-08-28T16:09:50"
}
```

## 5. Test de Récupération d'Image QR

### Requête
```bash
curl -X GET "http://localhost:8080/api/liquidations/1/qr-image" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Réponse attendue
```json
{
  "success": true,
  "message": "Image QR récupérée avec succès",
  "qrImageBase64": "iVBORw0KGgoAAAANSUhEUgAA...",
  "contentType": "image/png",
  "imageSize": 1234,
  "qrType": "STATIC",
  "generatedAt": "2025-08-28T16:09:50",
  "liquidationId": 1,
  "transactionId": "STATIC-1-1234567890"
}
```

## 6. Test de Régénération de QR Code

### Requête
```bash
curl -X PUT "http://localhost:8080/api/liquidations/1/regenerate-qr" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "qrType": "DYNAMIC",
    "transactionReference": "NEW_REF"
  }'
```

### Réponse attendue
```json
{
  "success": true,
  "message": "QR code DYNAMIC régénéré avec succès",
  "qrCode": "00020101021236360012int.bceao.pi...",
  "qrType": "DYNAMIC",
  "liquidationId": 1,
  "customerName": "John Doe",
  "amount": 50000.00,
  "currency": "XOF",
  "taxType": "TVA",
  "transactionId": "NEW_REF",
  "merchantChannel": "default",
  "generatedAt": "2025-08-28T16:09:50"
}
```

## 7. Tests d'Erreurs

### Liquidation inexistante
```bash
curl -X POST "http://localhost:8080/api/liquidations/99999/generate-qr" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"qrType": "STATIC"}'
```

### Réponse attendue
```json
{
  "success": false,
  "message": "Liquidation non trouvée",
  "error": "Liquidation introuvable"
}
```

### Type de QR invalide
```bash
curl -X POST "http://localhost:8080/api/liquidations/1/generate-qr" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"qrType": "INVALID"}'
```

### Réponse attendue
```json
{
  "success": false,
  "message": "Erreur de validation",
  "error": "Type de QR code non supporté: INVALID"
}
```

### Image QR inexistante
```bash
curl -X GET "http://localhost:8080/api/liquidations/1/qr-image" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Réponse attendue
```json
{
  "success": false,
  "message": "Aucun QR code généré",
  "error": "Cette liquidation n'a pas de QR code généré"
}
```

## 8. Tests avec Postman

### Collection Postman
1. **Environnement** : Créer un environnement avec :
   - `base_url`: `http://localhost:8080`
   - `jwt_token`: Token d'authentification

2. **Variables** :
   - `liquidation_id`: ID de la liquidation de test

### Tests automatisés
```javascript
// Test de succès
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has success field", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('success');
});

pm.test("Success is true", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.success).to.eql(true);
});

pm.test("QR code is generated", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.qrCode).to.be.a('string');
    pm.expect(jsonData.qrCode.length).to.be.greaterThan(0);
});
```

## 9. Validation des QR Codes

### Vérification EMVCo
Les QR codes générés respectent le standard EMVCo :
- Format : `0002010102...`
- CRC : Vérification de l'intégrité
- Champs obligatoires : Merchant Account Info, Currency, Amount

### Vérification BCEAO
- Code pays : `CI` (Côte d'Ivoire)
- Devise : `XOF` (Franc CFA)
- Format marchand : `int.bceao.pi`

## 10. Tests de Performance

### Test de charge
```bash
# Test avec Apache Bench
ab -n 100 -c 10 -H "Authorization: Bearer YOUR_JWT_TOKEN" \
   -H "Content-Type: application/json" \
   -p qr_request.json \
   http://localhost:8080/api/liquidations/1/generate-qr
```

### Métriques attendues
- Temps de réponse : < 500ms
- Taux de succès : > 99%
- Débit : > 100 req/s

## Conclusion

Ces tests couvrent les fonctionnalités principales des nouveaux endpoints QR :
- ✅ Génération de tous les types de QR
- ✅ Récupération d'images
- ✅ Régénération
- ✅ Gestion d'erreurs
- ✅ Validation des données
- ✅ Conformité EMVCo/BCEAO

Les endpoints sont prêts pour la production avec une gestion complète des erreurs et une validation robuste.
