# Backend Documentation

## Overview

The backend is built with Spring Boot and provides a RESTful API for managing customers, liquidations, and QR code generation. It integrates with the UEMOA payment system for compliant QR code generation.

## Core Technologies

- **Framework**: Spring Boot 3.4.8
- **Language**: Java 17
- **Database**: PostgreSQL (Production), H2 (Testing)
- **Security**: Spring Security with JWT
- **ORM**: Hibernate/JPA
- **Build Tool**: Maven
- **Validation**: Bean Validation API
- **Logging**: SLF4J with Logback

## Project Structure

```
backend-liquidation-app/
├── src/main/java/com/example/demoQrcode/
│   ├── DemoQrcodeApplication.java          # Main application class
│   ├── config/                             # Configuration classes
│   │   ├── CorsConfig.java                 # CORS configuration
│   │   ├── DataInit.java                   # Data initialization
│   │   ├── SecurityConfig.java             # Security configuration
│   │   ├── UemoaAutoConfiguration.java     # UEMOA auto-configuration
│   │   ├── UemoaConfig.java                # UEMOA settings
│   │   └── UemoaQRConfig.java              # QR configuration
│   ├── controller/                         # REST controllers
│   │   ├── AuthController.java             # Authentication endpoints
│   │   ├── CustomerController.java         # Customer management
│   │   ├── LiquidationController.java      # Liquidation management
│   │   ├── LiquidationQRController.java    # QR code generation
│   │   ├── LiquidationQRDataController.java # QR data queries
│   │   ├── RestExceptionHandler.java       # Exception handling
│   │   └── UemoaQRController.java          # UEMOA QR endpoints
│   ├── dto/                                # Data Transfer Objects
│   │   ├── AuthRequest.java                # Login request
│   │   ├── AuthResponse.java               # Login response
│   │   ├── QRGenerationRequest.java        # QR generation request
│   │   ├── QRGenerationResponse.java       # QR generation response
│   │   ├── QRImageResponse.java            # QR image response
│   │   ├── RegisterRequest.java            # Registration request
│   │   └── RegisterResponse.java           # Registration response
│   ├── entity/                             # JPA entities
│   │   ├── Customer.java                   # Customer entity
│   │   ├── Liquidation.java                # Liquidation entity
│   │   ├── LiquidationStatus.java          # Status enum
│   │   ├── Role.java                       # User role entity
│   │   └── User.java                       # User entity
│   ├── repository/                         # Data repositories
│   │   ├── CustomerRepository.java         # Customer data access
│   │   ├── LiquidationRepository.java      # Liquidation data access
│   │   ├── RoleRepository.java             # Role data access
│   │   └── UserRepository.java             # User data access
│   ├── security/                           # Security components
│   │   ├── JwtAuthenticationEntryPoint.java # JWT entry point
│   │   ├── JwtAuthenticationFilter.java    # JWT filter
│   │   └── JwtUtil.java                    # JWT utilities
│   └── service/                            # Business logic services
│       ├── impl/                           # Service implementations
│       │   ├── CustomerServiceImpl.java    # Customer service impl
│       │   ├── LiquidationQRDataServiceImpl.java # QR data service
│       │   ├── LiquidationQRServiceImpl.java # QR service impl
│       │   └── LiquidationServiceImpl.java # Liquidation service impl
│       ├── CustomerService.java            # Customer service interface
│       ├── CustomUserDetailsService.java   # User details service
│       ├── LiquidationQRDataService.java   # QR data service
│       ├── LiquidationQRService.java       # QR service interface
│       ├── LiquidationService.java         # Liquidation service
│       └── UemoaQRIntegrationService.java  # UEMOA integration
└── src/main/resources/
    ├── application.properties              # Application configuration
    └── db/migration/                       # Database migrations
```

## Entities

### Customer Entity

Represents a customer in the system.

```java
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @Size(max = 255)
    private String address;

    @Size(max = 50)
    private String ifu; // Tax identification number

    @Size(max = 20)
    private String phone;

    @NotBlank
    @Email(message = "Invalid email")
    private String email;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Liquidation> liquidations;
}
```

**Key Fields:**
- `id`: Primary key
- `lastName`, `firstName`: Customer name
- `address`: Customer address
- `ifu`: Tax identification number (UEMOA specific)
- `phone`: Contact phone number
- `email`: Contact email (unique, validated)
- `createdAt`, `updatedAt`: Audit timestamps

### Liquidation Entity

Represents a liquidation process with QR code integration.

```java
@Entity
@Table(name = "liquidations")
public class Liquidation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private LiquidationStatus status;

    // QR Code fields
    @Column(columnDefinition = "TEXT")
    private String qrCodeData;

    @Column(columnDefinition = "TEXT")
    private String qrImageBase64;

    @Size(max = 64)
    private String merchantChannel;

    @Size(max = 128)
    private String transactionId;

    @Size(max = 16)
    private String qrType;

    private LocalDateTime qrGeneratedAt;

    @DecimalMin(value = "0.00")
    private BigDecimal penaltyAmount;

    @DecimalMin(value = "0.00")
    private BigDecimal totalAmount;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Business methods
    public BigDecimal calculateTotalAmount() {
        return amount.add(penaltyAmount != null ? penaltyAmount : BigDecimal.ZERO);
    }
}
```

**Key Fields:**
- `id`: Primary key
- `customer`: Associated customer (foreign key)
- `amount`: Base liquidation amount
- `status`: Current status (enum)
- `qrCodeData`: Generated QR code data
- `qrImageBase64`: QR code image in Base64 format
- `merchantChannel`: UEMOA merchant channel
- `transactionId`: Unique transaction identifier
- `qrType`: Type of QR code (STATIC, DYNAMIC, P2P, PENALTY)
- `qrGeneratedAt`: Timestamp of QR generation
- `penaltyAmount`: Additional penalty amount
- `totalAmount`: Total amount (base + penalty)

### User Entity

Represents system users for authentication.

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password;

    @Email
    @Size(max = 100)
    private String email;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Relationships
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}
```

### Role Entity

Represents user roles for authorization.

```java
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String name; // e.g., "ROLE_ADMIN", "ROLE_USER"
}
```

### LiquidationStatus Enum

Defines the possible states of a liquidation.

```java
public enum LiquidationStatus {
    PENDING,
    APPROVED,
    PAID,
    CANCELLED,
    OVERDUE
}
```

## Services

### LiquidationService

Interface defining liquidation business operations.

```java
public interface LiquidationService {
    Page<Liquidation> list(Pageable pageable);
    Optional<Liquidation> get(Long id);
    Liquidation create(Liquidation liquidation);
    Optional<Liquidation> update(Long id, Liquidation liquidation);
    Optional<Liquidation> markAsPaid(Long id);
    Page<Liquidation> searchWithFilters(Long customerId, LiquidationStatus status,
                                       LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<Liquidation> findByCustomer(Long customerId);
    BigDecimal calculatePenalty(Liquidation liquidation, BigDecimal dailyRate);
    Page<Liquidation> searchByTerm(String term, Pageable pageable);
}
```

### UemoaQRIntegrationService

Service handling UEMOA QR code generation and parsing.

**Key Methods:**

1. **generateStaticQR(int amount, String merchantName)**
   - Generates static QR codes for fixed amounts
   - Validates amount against configured limits
   - Returns QR data, merchant info, and metadata

2. **generateDynamicQR(int amount, String merchantName, String reference)**
   - Generates dynamic QR codes with transaction references
   - Includes unique transaction ID for tracking
   - Supports variable amounts

3. **parseQRCode(String qrData)**
   - Parses QR code data to extract payment information
   - Validates QR code format and integrity
   - Returns structured payment data

4. **generateTestQR()**
   - Generates test QR codes for development
   - Only available when test mode is enabled

## Controllers

### AuthController

Handles user authentication and registration.

**Endpoints:**
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/register/admin` - Admin registration

### CustomerController

Manages customer CRUD operations.

**Endpoints:**
- `GET /api/customers` - List customers (paginated)
- `GET /api/customers/{id}` - Get customer by ID
- `GET /api/customers/search?q=term&page=0&size=10` - Search customers
- `POST /api/customers` - Create customer (Admin only)
- `PUT /api/customers/{id}` - Update customer (Admin only)
- `DELETE /api/customers/{id}` - Delete customer (Admin only)

### LiquidationController

Manages liquidation processes and QR code generation.

**Endpoints:**
- `GET /api/liquidations` - List liquidations (paginated)
- `GET /api/liquidations/search` - Search liquidations with filters
- `GET /api/liquidations/{id}` - Get liquidation by ID
- `POST /api/liquidations` - Create liquidation (Admin only)
- `PUT /api/liquidations/{id}` - Update liquidation (Admin only)
- `DELETE /api/liquidations/{id}` - Delete liquidation (Admin only)
- `GET /api/liquidations/customer/{customerId}` - Get liquidations by customer
- `GET /api/liquidations/{id}/penalty` - Calculate penalty for liquidation
- `POST /api/liquidations/{id}/generate-qr` - Generate QR code for liquidation
- `GET /api/liquidations/{id}/qr-image` - Get QR code image
- `PUT /api/liquidations/{id}/regenerate-qr` - Regenerate QR code

### LiquidationQRController

Handles QR code generation for different payment types.

**Endpoints:**
- `POST /api/liquidations/qr/static` - Generate static QR code
- `POST /api/liquidations/qr/dynamic` - Generate dynamic QR code
- `POST /api/liquidations/qr/p2p` - Generate P2P QR code
- `POST /api/liquidations/qr/penalty` - Generate penalty QR code
- `GET /api/liquidations/qr/reference` - Get QR reference data
- `GET /api/liquidations/qr/validate` - Validate QR code

### LiquidationQRDataController

Provides data access for liquidations with QR codes.

**Endpoints:**
- `GET /api/liquidations/data/with-qr` - Get liquidations with QR codes
- `GET /api/liquidations/data/without-qr` - Get liquidations without QR codes
- `GET /api/liquidations/data/type/{qrType}` - Get by QR type
- `GET /api/liquidations/data/transaction/{transactionId}` - Get by transaction ID
- `GET /api/liquidations/data/customer/{customerId}` - Get by customer
- `GET /api/liquidations/data/status/{status}` - Get by status
- `GET /api/liquidations/data/today` - Today's liquidations
- `GET /api/liquidations/data/this-week` - This week's liquidations
- `GET /api/liquidations/data/this-month` - This month's liquidations
- `GET /api/liquidations/data/with-penalties` - Liquidations with penalties
- `GET /api/liquidations/data/stats/count-by-type` - Statistics by QR type
- `GET /api/liquidations/data/stats/total-amount` - Total amount statistics
- `GET /api/liquidations/data/stats/total-penalties` - Total penalties statistics

### UemoaQRController

Direct UEMOA QR code operations.

**Endpoints:**
- `POST /api/uemoa/qr/generate-static` - Generate static QR
- `POST /api/uemoa/qr/generate-dynamic` - Generate dynamic QR
- `POST /api/uemoa/qr/parse` - Parse QR code
- `GET /api/uemoa/qr/test` - Generate test QR
- `GET /api/uemoa/qr/health` - Health check

## Security Configuration

### JWT Authentication

The application uses JWT (JSON Web Tokens) for stateless authentication.

**Key Components:**
- **JwtUtil**: Token generation, validation, and parsing
- **JwtAuthenticationFilter**: Intercepts requests to validate JWT tokens
- **JwtAuthenticationEntryPoint**: Handles unauthorized access attempts
- **CustomUserDetailsService**: Loads user details for authentication

### Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/uemoa/qr/health").permitAll()
                .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
```

### CORS Configuration

Configured to allow requests from the React frontend:

```properties
app.cors.allowed-origins=http://localhost:5173,http://localhost:3000,http://localhost:4200,http://localhost:8080
app.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,PATCH
app.cors.allowed-headers=Authorization,Content-Type,X-Requested-With,Accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,X-Test-Type,X-Test-Index
app.cors.exposed-headers=Authorization,Content-Type,X-Test-Type,X-Test-Index
app.cors.allow-credentials=true
app.cors.max-age=3600
```

### Role-Based Access Control

- **ROLE_ADMIN**: Full access to all operations
- **ROLE_USER**: Read-only access to relevant data

## Database Schema

### Core Tables

```sql
-- Customers table
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    last_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    ifu VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Liquidations table
CREATE TABLE liquidations (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    amount DECIMAL(18,2) NOT NULL CHECK (amount >= 0.01),
    status VARCHAR(20) NOT NULL,
    qr_code_data TEXT,
    qr_image_base64 TEXT,
    merchant_channel VARCHAR(64),
    transaction_id VARCHAR(128),
    qr_type VARCHAR(16) CHECK (qr_type IN ('STATIC', 'DYNAMIC', 'P2P', 'PENALTY')),
    qr_generated_at TIMESTAMP,
    penalty_amount DECIMAL(18,2) DEFAULT 0.00 CHECK (penalty_amount >= 0),
    total_amount DECIMAL(18,2) DEFAULT 0.00 CHECK (total_amount >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- User-Role relationship
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);
```

### Indexes

```sql
-- Performance indexes
CREATE INDEX idx_liquidations_customer_id ON liquidations(customer_id);
CREATE INDEX idx_liquidations_status ON liquidations(status);
CREATE INDEX idx_liquidations_created_at ON liquidations(created_at);
CREATE INDEX idx_liquidations_qr_type ON liquidations(qr_type);
CREATE INDEX idx_liquidations_transaction_id ON liquidations(transaction_id);
CREATE INDEX idx_liquidations_qr_generated_at ON liquidations(qr_generated_at);
CREATE INDEX idx_liquidations_merchant_channel ON liquidations(merchant_channel);
```

## Configuration

### Application Properties

Key configuration properties:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/qr_demo_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=${JWT_SECRET:myVerySecureSecretKey...}
jwt.expiration=3600000

# Server
server.port=8080

# UEMOA Configuration
uemoa.qr.country-code=CI
uemoa.qr.currency=XOF
uemoa.qr.merchant-name=LIQUIDATION APP
uemoa.qr.default-amount=100
uemoa.qr.min-amount=1
uemoa.qr.max-amount=999999
```

### Environment Variables

Required environment variables:

- `JWT_SECRET`: Secret key for JWT token signing (32+ characters)

## Error Handling

### RestExceptionHandler

Global exception handler for consistent error responses.

```java
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {
        return ResponseEntity.badRequest().body("Data integrity violation");
    }
}
```

## Data Initialization

### DataInit

Component for initializing default data on application startup.

```java
@Component
public class DataInit implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // Create default roles
        // Create default admin user
        // Initialize sample data if needed
    }
}
```

## Validation

The application uses Bean Validation (JSR-303) for input validation:

- `@NotBlank`: Fields cannot be null or empty
- `@Size`: Field length constraints
- `@Email`: Email format validation
- `@DecimalMin`: Minimum decimal values
- `@NotNull`: Fields cannot be null

## Logging

Configured logging levels:

```properties
logging.level.root=INFO
logging.level.com.example.demoQrcode=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.springframework.security=DEBUG
logging.file.name=logs/demoqrcode.log