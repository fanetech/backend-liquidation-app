# 📊 JPA Entities

This directory contains all the JPA (Java Persistence API) entity classes that represent the database tables and relationships in the Liquidation App.

## 📋 Overview

Entities define the data model and are mapped to database tables using JPA annotations. They represent the core business objects and their relationships.

## 🏗️ Entity Architecture

### Base Entity Features
- **Table Mapping**: JPA `@Table` annotations
- **Primary Keys**: `@Id` with generation strategies
- **Relationships**: `@OneToMany`, `@ManyToOne`, `@ManyToMany`
- **Validation**: Bean Validation annotations
- **Auditing**: Automatic timestamp tracking
- **Indexing**: Performance optimization with `@Index`

### Common Patterns
```java
@Entity
@Table(name = "table_name")
@EntityListeners(AuditingEntityListener.class)
public class EntityName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationships and business methods
}
```

## 📁 Entity Files

### Core Business Entities

#### Customer (`Customer.java`)
**Purpose**: Represents customers in the system
**Table**: `customers`
**Key Fields**:
- `id`: Primary key (auto-generated)
- `lastName`: Customer's last name
- `firstName`: Customer's first name
- `address`: Customer's address
- `ifu`: Tax identification number (UEMOA specific)
- `phone`: Contact phone number
- `email`: Contact email (unique)

**Relationships**:
- `OneToMany` with `Liquidation` (a customer can have multiple liquidations)

**Validation**:
```java
@NotBlank(message = "Last name is required")
@Size(max = 100, message = "Last name cannot exceed 100 characters")
private String lastName;
```

#### Liquidation (`Liquidation.java`)
**Purpose**: Represents liquidation processes
**Table**: `liquidations`
**Key Fields**:
- `id`: Primary key (auto-generated)
- `customer`: Associated customer (foreign key)
- `amount`: Base liquidation amount
- `status`: Current status (enum)
- `penaltyAmount`: Additional penalty amount
- `totalAmount`: Calculated total amount
- QR code related fields

**QR Code Fields**:
- `qrCodeData`: Generated QR code data
- `qrImageBase64`: QR code image in Base64 format
- `merchantChannel`: UEMOA merchant channel
- `transactionId`: Unique transaction identifier
- `qrType`: Type of QR code (STATIC, DYNAMIC, P2P, PENALTY)
- `qrGeneratedAt`: QR code generation timestamp

**Business Methods**:
```java
public BigDecimal calculateTotalAmount() {
    return amount.add(penaltyAmount != null ? penaltyAmount : BigDecimal.ZERO);
}
```

### Security Entities

#### User (`User.java`)
**Purpose**: System users for authentication
**Table**: `users`
**Key Fields**:
- `id`: Primary key (auto-generated)
- `username`: Unique username
- `password`: Encrypted password
- `email`: User email

**Relationships**:
- `ManyToMany` with `Role` (users can have multiple roles)

**Security Features**:
- Password encryption with BCrypt
- Unique constraints on username
- Role-based access control

#### Role (`Role.java`)
**Purpose**: User roles for authorization
**Table**: `roles`
**Key Fields**:
- `id`: Primary key (auto-generated)
- `name`: Role name (e.g., "ROLE_ADMIN", "ROLE_USER")

**Relationships**:
- `ManyToMany` with `User` (roles can be assigned to multiple users)

### Enumeration Types

#### LiquidationStatus (`LiquidationStatus.java`)
**Purpose**: Defines possible liquidation states
**Values**:
- `PENDING`: Initial state
- `APPROVED`: Approved for processing
- `PAID`: Payment completed
- `CANCELLED`: Process cancelled
- `OVERDUE`: Past due date

```java
public enum LiquidationStatus {
    PENDING,
    APPROVED,
    PAID,
    CANCELLED,
    OVERDUE
}
```

## 🔗 Entity Relationships

### Database Schema Overview
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
    amount DECIMAL(18,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    -- QR code fields
    qr_code_data TEXT,
    qr_image_base64 TEXT,
    merchant_channel VARCHAR(64),
    transaction_id VARCHAR(128),
    qr_type VARCHAR(16),
    qr_generated_at TIMESTAMP,
    penalty_amount DECIMAL(18,2) DEFAULT 0,
    total_amount DECIMAL(18,2) DEFAULT 0,
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

### JPA Relationship Mapping

#### One-to-Many (Customer → Liquidations)
```java
// In Customer.java
@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<Liquidation> liquidations;

// In Liquidation.java
@ManyToOne(optional = false, fetch = FetchType.LAZY)
@JoinColumn(name = "customer_id")
private Customer customer;
```

#### Many-to-Many (User ↔ Role)
```java
// In User.java
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
private Set<Role> roles;

// In Role.java
@ManyToMany(mappedBy = "roles")
private Set<User> users;
```

## ✅ Validation & Constraints

### Bean Validation Annotations

#### Field-Level Validation
```java
@NotBlank(message = "Last name is required")
@Size(max = 100, message = "Last name cannot exceed 100 characters")
private String lastName;

@NotNull(message = "Amount is required")
@DecimalMin(value = "0.01", message = "Amount must be greater than 0")
private BigDecimal amount;

@Email(message = "Invalid email format")
private String email;
```

#### Class-Level Validation
```java
@UniqueEmail(message = "Email already exists")
public class Customer {
    // fields
}
```

### Database Constraints

#### Unique Constraints
```java
@Column(unique = true)
private String email;

@Column(unique = true)
private String username;
```

#### Check Constraints
```sql
ALTER TABLE liquidations
ADD CONSTRAINT chk_amount CHECK (amount >= 0.01);

ALTER TABLE liquidations
ADD CONSTRAINT chk_penalty_amount CHECK (penalty_amount >= 0);
```

## 🔄 Auditing & Timestamps

### Automatic Timestamp Tracking
```java
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

### Configuration
```java
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // Auditing configuration
}
```

## 📊 Performance Optimization

### Indexing Strategy
```java
@Table(indexes = {
    @Index(name = "idx_customer_email", columnList = "email"),
    @Index(name = "idx_customer_name", columnList = "lastName, firstName")
})
public class Customer {
    // fields
}
```

### Fetch Strategies
```java
// Lazy loading for performance
@OneToMany(fetch = FetchType.LAZY)
private List<Liquidation> liquidations;

// Eager loading for security roles
@ManyToMany(fetch = FetchType.EAGER)
private Set<Role> roles;
```

### Query Optimization
```java
// Custom repository methods
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT c FROM Customer c WHERE c.email = :email")
    Optional<Customer> findByEmail(@Param("email") String email);

    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.liquidations WHERE c.id = :id")
    Optional<Customer> findByIdWithLiquidations(@Param("id") Long id);
}
```

## 🔒 Security Considerations

### Data Protection
- Sensitive fields are properly validated
- Passwords are encrypted using BCrypt
- PII (Personally Identifiable Information) is handled carefully
- Audit trails for data changes

### Access Control
- Entities respect role-based access
- Admin operations are properly secured
- Data isolation between users where applicable

## 🧪 Testing Entities

### Unit Testing
```java
@EntityTest
class CustomerEntityTest {

    @Test
    void shouldCreateCustomer() {
        Customer customer = Customer.builder()
                .lastName("Doe")
                .firstName("John")
                .email("john.doe@example.com")
                .build();

        assertThat(customer.getLastName()).isEqualTo("Doe");
        assertThat(customer.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void shouldCalculateTotalAmount() {
        Liquidation liquidation = Liquidation.builder()
                .amount(new BigDecimal("1000.00"))
                .penaltyAmount(new BigDecimal("50.00"))
                .build();

        assertThat(liquidation.calculateTotalAmount())
                .isEqualTo(new BigDecimal("1050.00"));
    }
}
```

### Integration Testing
```java
@SpringBootTest
@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldSaveAndRetrieveCustomer() {
        Customer customer = createTestCustomer();
        Customer saved = customerRepository.save(customer);

        assertThat(customerRepository.findById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getLastName()).isEqualTo("Doe");
                    assertThat(c.getEmail()).isEqualTo("john.doe@example.com");
                });
    }
}
```

## 🔧 Development Guidelines

### Entity Best Practices
1. **Immutable IDs**: Use auto-generated primary keys
2. **Consistent Naming**: Follow database naming conventions
3. **Validation**: Always validate input data
4. **Relationships**: Use appropriate fetch strategies
5. **Indexing**: Add indexes for frequently queried fields

### Design Principles
1. **Single Responsibility**: Each entity represents one concept
2. **Rich Domain Model**: Include business logic in entities
3. **Proper Encapsulation**: Use private fields with getters/setters
4. **Validation**: Validate data at the entity level
5. **Documentation**: Document complex business rules

## 📈 Future Enhancements

### Planned Features
- **Soft Deletes**: Implement soft delete functionality
- **Versioning**: Add optimistic locking for concurrent updates
- **Auditing**: Enhanced audit trail with user tracking
- **Encryption**: Field-level encryption for sensitive data
- **Caching**: Entity caching with Redis
- **Search**: Full-text search capabilities

### Performance Improvements
- **Query Optimization**: Implement query result caching
- **Batch Operations**: Support for bulk entity operations
- **Lazy Loading**: Optimize relationship loading strategies
- **Database Sharding**: Support for horizontal scaling

---

**Directory**: `src/main/java/com/example/demoQrcode/entity/`
**Last Updated**: January 2025
**Entities**: 5 files
**Relationships**: 3 main relationships
**Database Tables**: 4 core tables