# Demo QR Code - API Spring Boot

## Configuration requise

- Java 17+
- Maven 3.6+
- PostgreSQL (ou H2 pour les tests)

## Variables d'environnement

Créez un fichier `.env` à la racine du projet avec :

```bash
JWT_SECRET=mySecretKey1234567890123456789012345678901234567890
```

## Démarrage

```bash
mvn spring-boot:run
```

L'API sera accessible sur `http://localhost:8080`

## Test dans Postman

### 1. Créer un utilisateur (Register)

**POST** `http://localhost:8080/api/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
    "username": "testuser",
    "password": "password123"
}
```

### 2. Se connecter (Login)

**POST** `http://localhost:8080/api/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
    "username": "testuser",
    "password": "password123"
}
```

**Réponse attendue:**
```json
{
    "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

### 3. Accéder aux ressources protégées

**GET** `http://localhost:8080/api/products`

**Headers:**
```
Authorization: Bearer {token_from_login}
```

## Utilisateurs de test pré-configurés

- **user1** / **Mot de passe1** (ROLE_USER)
- **admin1** / **password1** (ROLE_ADMIN)

## Endpoints disponibles

- `POST /api/auth/register` - Inscription
- `POST /api/auth/login` - Connexion
- `GET /api/products` - Liste des produits (authentifié)
- `POST /api/products` - Créer un produit (authentifié)

## Résolution des erreurs 401

Si vous recevez une erreur 401 :

1. Vérifiez que l'utilisateur existe
2. Vérifiez le mot de passe
3. Assurez-vous que la variable `JWT_SECRET` est définie
4. Vérifiez que le token JWT est valide et non expiré
