# Intégration du Module UEMOA QR Code

## Description

Ce projet intègre le module de paiement par code QR UEMOA pour générer et parser des QR codes conformes aux normes EMVCo et BCEAO pour l'Union Économique et Monétaire Ouest-Africaine (UEMOA).

## Fonctionnalités Intégrées

### ✅ Critères d'acceptation réalisés

1. **✅ Ajout de la dépendance Maven**
   - Module `uemoa-qrcode-module` v1.0.0 ajouté au `pom.xml`
   - Installation locale du module depuis le repository GitHub

2. **✅ Configuration des propriétés UEMOA**
   - Fichier `application.yml` créé avec configuration complète
   - Paramètres par défaut pour l'UEMOA (CI, XOF, etc.)
   - Configuration des montants, marchands et tests

3. **✅ Vérification du chargement du module**
   - Tests d'intégration passent avec succès
   - Services UEMOA correctement injectés
   - Configuration chargée et validée

4. **✅ Classe de configuration UEMOA**
   - `UemoaConfig.java` créée avec tous les paramètres
   - Support des propriétés personnalisables
   - Getters et setters pour tous les paramètres

## Structure du Projet

### Fichiers Ajoutés/Modifiés

```
src/main/java/com/example/demoQrcode/
├── config/
│   ├── UemoaConfig.java                    # Configuration UEMOA
│   └── UemoaAutoConfiguration.java         # Auto-configuration Spring Boot
├── controller/
│   └── UemoaQRController.java              # API REST pour QR codes
└── service/
    └── UemoaQRIntegrationService.java      # Service d'intégration

src/main/resources/
└── application.yml                         # Configuration YAML

src/test/java/com/example/demoQrcode/
└── UemoaQRModuleTest.java                  # Tests d'intégration

pom.xml                                     # Dépendance Maven ajoutée
```

## Configuration

### Paramètres UEMOA par défaut

```yaml
uemoa:
  qr:
    country-code: CI                    # Côte d'Ivoire
    currency: XOF                       # Franc CFA
    merchant-category-code: 0000
    merchant-name: "LIQUIDATION APP"
    merchant-city: "Abidjan"
    merchant-postal-code: "225"
    merchant-country: "CI"
    
    payment-system:
      identifier: "int.bceao.pi"
      name: "BCEAO Payment Interface"
    
    default-amount: 100
    min-amount: 1
    max-amount: 999999
    
    test:
      enabled: true
      merchant-id: "test-123"
      terminal-id: "test-terminal"
```

## API Endpoints

### Génération de QR Codes

#### QR Code Statique
```http
POST /api/uemoa-qr/generate-static
Content-Type: application/json

{
  "amount": 1000,
  "merchantName": "Mon Magasin"
}
```

#### QR Code Dynamique
```http
POST /api/uemoa-qr/generate-dynamic
Content-Type: application/json

{
  "amount": 2000,
  "merchantName": "Mon Magasin",
  "reference": "REF-123456"
}
```

#### Parsing de QR Code
```http
POST /api/uemoa-qr/parse
Content-Type: application/json

{
  "qrData": "00020101021136280012int.bceao.pi..."
}
```

#### QR Code de Test
```http
GET /api/uemoa-qr/test
```

#### Vérification de Santé
```http
GET /api/uemoa-qr/health
```

## Tests

### Exécution des Tests

```bash
# Test complet du module UEMOA
mvn test -Dtest=UemoaQRModuleTest

# Compilation et vérification
mvn clean compile
```

### Résultats des Tests

```
✅ Module UEMOA QR Code intégré avec succès
✅ Service UEMOA QR: com.aveplus.uemoa.qr.service.UemoaQRService
✅ Service d'intégration: com.example.demoQrcode.service.UemoaQRIntegrationService
✅ Configuration: com.example.demoQrcode.config.UemoaConfig
✅ Configuration UEMOA correcte:
   - Code pays: CI
   - Devise: XOF
   - Nom marchand: LIQUIDATION APP
   - Ville: Abidjan
✅ Génération de QR code réussie:
   - Marchand: TEST MERCHANT
   - Montant: 100
   - Devise: XOF
   - QR Code: 00020101021136280012int.bceao.pi...
```

## Utilisation

### Dans le Code Java

```java
@Autowired
private UemoaQRIntegrationService uemoaQRService;

// Génération d'un QR code statique
Map<String, Object> qrData = uemoaQRService.generateStaticQR(1000, "Mon Magasin");

// Génération d'un QR code dynamique
Map<String, Object> qrData = uemoaQRService.generateDynamicQR(2000, "Mon Magasin", "REF-123");

// Parsing d'un QR code
Map<String, Object> parsedData = uemoaQRService.parseQRCode(qrCodeString);
```

### Format des Données Retournées

```json
{
  "success": true,
  "qrData": {
    "qrCode": "00020101021136280012int.bceao.pi...",
    "merchantName": "Mon Magasin",
    "amount": 1000,
    "currency": "XOF",
    "countryCode": "CI",
    "type": "STATIC"
  },
  "message": "QR code statique généré avec succès"
}
```

## Dépendances

### Module UEMOA
- **GroupId**: `com.aveplus`
- **ArtifactId**: `uemoa-qrcode-module`
- **Version**: `1.0.0`
- **Source**: [GitHub - fanetech/UEMOA-QR-code-payment-module](https://github.com/fanetech/UEMOA-QR-code-payment-module)

### Dépendances Transitives
- Spring Boot 3.1.5+
- ZXing 3.5.2 (génération QR)
- Lombok 1.18.30
- Apache Commons Lang3 3.13.0

## Support des Pays UEMOA

Le module supporte les pays suivants de l'UEMOA :
- **BF** - Burkina Faso
- **CI** - Côte d'Ivoire
- **TG** - Togo
- **SN** - Sénégal
- **ML** - Mali
- **BJ** - Bénin
- **GW** - Guinée-Bissau
- **NE** - Niger

## Devise

- **XOF** - Franc CFA (Communauté Financière Africaine)

## Normes Conformes

- **EMVCo** - Standards internationaux pour les paiements par QR code
- **BCEAO** - Banque Centrale des États de l'Afrique de l'Ouest
- **UEMOA** - Union Économique et Monétaire Ouest-Africaine

## Sécurité

- Validation des montants (min/max)
- Validation des références de transaction
- Gestion des erreurs avec messages explicites
- Logs détaillés pour le debugging

## Maintenance

### Mise à Jour du Module

```bash
# Mettre à jour le module UEMOA
cd UEMOA-QR-code-payment-module
git pull origin main
mvn clean install -DskipTests

# Recompiler le projet principal
cd ..
mvn clean compile
```

### Logs

Les logs du module UEMOA sont disponibles dans :
- `logs/demoqrcode.log` - Logs généraux de l'application
- Console Spring Boot - Logs en temps réel

## Support

Pour toute question ou problème avec l'intégration UEMOA :

1. Vérifier les logs de l'application
2. Exécuter les tests d'intégration
3. Consulter la documentation du module UEMOA
4. Vérifier la configuration dans `application.yml`

---

**Statut**: ✅ **INTÉGRATION TERMINÉE ET TESTÉE**

**Version**: 1.0.0  
**Date**: 27 Août 2025  
**Auteur**: Assistant IA
