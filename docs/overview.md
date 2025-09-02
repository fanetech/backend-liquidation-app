# Backend - Liquidation App API

## Overview

The backend is a robust Spring Boot REST API that provides comprehensive server-side functionality for the Liquidation App. It handles customer management, liquidation processing, QR code generation, and integrates with UEMOA payment standards for BCEAO compliance.

## Key Features

- **RESTful API**: Complete REST API with 30+ endpoints
- **JWT Authentication**: Secure stateless authentication with role-based access
- **Customer Management**: Full CRUD operations for customer data
- **Liquidation Processing**: Complete lifecycle management with QR integration
- **UEMOA QR Generation**: BCEAO-compliant QR codes (Static, Dynamic, P2P, Penalty)
- **Database Integration**: PostgreSQL with JPA/Hibernate and Flyway migrations
- **Security Layer**: Spring Security with CORS, CSRF protection, and validation
- **Error Handling**: Comprehensive exception handling with custom responses
- **Data Validation**: Bean validation with detailed error messages

## Technology Stack

- **Java 17**: Modern Java runtime with LTS support
- **Spring Boot 3.4.8**: Production-ready framework with auto-configuration
- **Spring Security 6.x**: Authentication and authorization framework
- **Spring Data JPA 3.x**: Data persistence and ORM layer
- **PostgreSQL 12+**: Primary production database
- **H2 Database**: In-memory database for testing and development
- **JWT (JSON Web Tokens)**: Stateless authentication tokens
- **Maven 3.6+**: Build automation and dependency management
- **Lombok**: Code generation to reduce boilerplate

## API Architecture

### Core Components

```
DemoQrcodeApplication (Main Class)
├── Configuration Layer
│   ├── SecurityConfig (JWT, CORS, Roles)
│   ├── CorsConfig (Cross-origin settings)
│   ├── UemoaConfig (Payment standards)
│   └── DataInit (Database initialization)
├── Controller Layer (REST Endpoints)
│   ├── AuthController (Authentication)
│   ├── CustomerController (Customer CRUD)
│   ├── LiquidationController (Liquidation management)
│   ├── LiquidationQRController (QR generation)
│   └── UemoaQRController (UEMOA integration)
├── Service Layer (Business Logic)
│   ├── CustomerService (Customer operations)
│   ├── LiquidationService (Liquidation processing)
│   ├── LiquidationQRService (QR generation)
│   └── UemoaQRIntegrationService (BCEAO compliance)
├── Repository Layer (Data Access)
│   ├── CustomerRepository
│   ├── LiquidationRepository
│   ├── UserRepository
│   └── RoleRepository
└── Entity Layer (Domain Models)
    ├── Customer (Customer data)
    ├── Liquidation (Liquidation records)
    ├── User (Authentication)
    └── Role (Authorization)
```

### Security Implementation

- **JWT Authentication**: Stateless token-based security
- **Role-based Access**: Admin/User permission system
- **CORS Configuration**: Configurable cross-origin policies
- **Input Validation**: Comprehensive data validation
- **Password Security**: BCrypt password hashing

## Database Design

### Core Tables

- **customers**: Customer information with IFU tax IDs
- **liquidations**: Liquidation records with QR code data
- **users**: System users with authentication data
- **roles**: User roles for authorization
- **user_roles**: Many-to-many relationship table

### Key Relationships

```
Customer (1) ────► Liquidation (N)
User (N) ────► UserRole (N) ◄──── (N) Role
```

## UEMOA Integration

### QR Code Types

1. **Static QR**: Fixed amount payments
2. **Dynamic QR**: Variable amount payments
3. **P2P QR**: Person-to-person transfers
4. **Penalty QR**: Penalty payment processing

### BCEAO Compliance

- **Currency**: XOF (West African CFA franc)
- **Country Code**: CI (Côte d'Ivoire) - configurable
- **Merchant Information**: Configurable merchant details
- **Payment Standards**: EMVCo compliant QR format

## Development Status

### Completed Features
- ✅ JWT authentication and authorization
- ✅ Customer CRUD operations with validation
- ✅ Liquidation management with status tracking
- ✅ UEMOA QR code generation (all types)
- ✅ PostgreSQL integration with migrations
- ✅ Comprehensive API documentation
- ✅ Error handling and logging
- ✅ Security configuration and CORS
- ✅ Data validation and constraints

### Current Focus
- 🔄 API performance optimization
- 🔄 Advanced filtering and search
- 🔄 Bulk operations support
- 🔄 Enhanced error responses
- 🔄 API versioning strategy

## API Endpoints

### Authentication (5 endpoints)
- `POST /api/auth/login` - User authentication
- `POST /api/auth/register/user` - User registration
- `POST /api/auth/register/admin` - Admin registration

### Customer Management (6 endpoints)
- `GET /api/customers` - List customers (paginated)
- `GET /api/customers/{id}` - Get customer by ID
- `POST /api/customers` - Create customer (Admin)
- `PUT /api/customers/{id}` - Update customer (Admin)
- `DELETE /api/customers/{id}` - Delete customer (Admin)
- `GET /api/customers/search` - Search customers

### Liquidation Management (12 endpoints)
- `GET /api/liquidations` - List liquidations
- `GET /api/liquidations/{id}` - Get liquidation by ID
- `POST /api/liquidations` - Create liquidation (Admin)
- `PUT /api/liquidations/{id}` - Update liquidation (Admin)
- `DELETE /api/liquidations/{id}` - Delete liquidation (Admin)
- `POST /api/liquidations/{id}/generate-qr` - Generate QR code
- `GET /api/liquidations/{id}/qr-image` - Get QR image
- Plus additional endpoints for filtering and statistics

### QR Code Operations (8 endpoints)
- Static, Dynamic, P2P, and Penalty QR generation
- QR validation and reference data
- UEMOA-specific operations

## Configuration

### Environment Variables

```bash
JWT_SECRET=myVerySecureSecretKey...  # 256-bit key required
DB_HOST=localhost                     # Database host
DB_PORT=5432                         # Database port
DB_NAME=qr_demo_db                   # Database name
DB_USERNAME=qr_user                  # Database user
DB_PASSWORD=your_password            # Database password
```

### Application Properties

```properties
# Server
server.port=8080
server.servlet.context-path=/api

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/qr_demo_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=3600000

# UEMOA
uemoa.qr.country-code=CI
uemoa.qr.currency=XOF
uemoa.qr.merchant-name=Liquidation App
```

## Target Environment

- **Production**: Linux servers with PostgreSQL
- **Development**: Local development with H2 database
- **Testing**: Automated tests with test containers
- **CI/CD**: Maven-based build pipeline

## Compliance & Standards

- **UEMOA Standards**: West African Economic and Monetary Union compliance
- **BCEAO Guidelines**: Central Bank of West African States requirements
- **EMVCo Standards**: Global QR code payment standards
- **REST API Standards**: Proper HTTP methods and status codes
- **Security Best Practices**: OWASP guidelines implementation