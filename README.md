# Liquidation App Backend - Spring Boot API

A comprehensive Spring Boot REST API for managing customer liquidations with integrated UEMOA-compliant QR code payment functionality.

## 🚀 Overview

This backend application provides a robust API for:
- **Customer Management**: Complete CRUD operations for customer data
- **Liquidation Processing**: Full lifecycle management of liquidation processes
- **QR Code Generation**: UEMOA/BCEAO compliant payment QR codes
- **Authentication & Authorization**: JWT-based security with role-based access control
- **Database Integration**: PostgreSQL with automated migrations

## 🏗️ Architecture

### Project Structure

```
backend-liquidation-app/
├── src/main/java/com/example/demoQrcode/
│   ├── DemoQrcodeApplication.java          # Main application class
│   ├── config/                             # Configuration classes
│   │   ├── CorsConfig.java                 # CORS configuration
│   │   ├── DataInit.java                   # Data initialization
│   │   ├── SecurityConfig.java             # Security configuration
│   │   ├── UemoaAutoConfiguration.java     # UEMOA auto-configuration
│   │   └── UemoaConfig.java                # UEMOA settings
│   ├── controller/                         # REST API endpoints
│   │   ├── AuthController.java             # Authentication endpoints
│   │   ├── CustomerController.java         # Customer management
│   │   ├── LiquidationController.java      # Liquidation management
│   │   ├── LiquidationQRController.java    # QR code generation
│   │   ├── LiquidationQRDataController.java # QR data queries
│   │   ├── RestExceptionHandler.java       # Global exception handling
│   │   └── UemoaQRController.java          # UEMOA QR operations
│   ├── dto/                                # Data Transfer Objects
│   │   ├── AuthRequest.java                # Authentication requests
│   │   ├── AuthResponse.java               # Authentication responses
│   │   ├── QRGenerationRequest.java        # QR generation requests
│   │   ├── QRGenerationResponse.java       # QR generation responses
│   │   ├── QRImageResponse.java            # QR image responses
│   │   └── RegisterRequest.java            # Registration requests
│   ├── entity/                             # JPA entities
│   │   ├── Customer.java                   # Customer entity
│   │   ├── Liquidation.java                # Liquidation entity
│   │   ├── LiquidationStatus.java          # Status enumeration
│   │   ├── Role.java                       # User role entity
│   │   └── User.java                       # User entity
│   ├── repository/                         # Data access layer
│   │   ├── CustomerRepository.java         # Customer data access
│   │   ├── LiquidationRepository.java      # Liquidation data access
│   │   ├── RoleRepository.java             # Role data access
│   │   └── UserRepository.java             # User data access
│   ├── security/                           # Security components
│   │   ├── JwtAuthenticationEntryPoint.java # JWT entry point
│   │   ├── JwtAuthenticationFilter.java    # JWT filter
│   │   └── JwtUtil.java                    # JWT utilities
│   └── service/                            # Business logic layer
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
├── src/main/resources/
│   ├── application.properties              # Application configuration
│   └── db/migration/                       # Database migrations
├── src/test/java/                          # Test classes
├── .mvn/                                  # Maven wrapper
├── mvnw                                   # Maven wrapper script (Unix)
├── mvnw.cmd                               # Maven wrapper script (Windows)
├── pom.xml                                # Maven configuration
├── application-dev.properties             # Development properties
├── Liquidation_QR_Endpoints.postman_collection.json  # Postman collection
├── test-uemoa-integration.bat             # UEMOA integration test script
└── README.md                              # This file
```

## 🛠️ Technology Stack

### Core Framework
- **Java**: 17 (LTS)
- **Spring Boot**: 3.4.8
- **Spring Framework**: 6.x

### Security & Authentication
- **Spring Security**: 6.x
- **JWT (JSON Web Tokens)**: 0.12.3
- **BCrypt**: Password hashing

### Database & Persistence
- **Spring Data JPA**: 3.x
- **Hibernate**: 6.x
- **PostgreSQL**: 12+
- **H2 Database**: For testing
- **Flyway**: Database migrations

### API & Documentation
- **Spring Web**: REST API development
- **Spring Validation**: Input validation
- **Jackson**: JSON processing

### Development Tools
- **Maven**: 3.6+
- **Lombok**: Code generation
- **Spring Boot DevTools**: Development utilities

### External Integrations
- **UEMOA QR Module**: Custom payment integration
- **BCEAO Standards**: Regional payment compliance

## 📋 Prerequisites

- **Java 17+**: JDK installation
- **Maven 3.6+**: Build tool
- **PostgreSQL 12+**: Database server
- **Git**: Version control

## ⚙️ Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd backend-liquidation-app
```

### 2. Database Setup

#### Create PostgreSQL Database

```sql
-- Create database
CREATE DATABASE qr_demo_db;

-- Create user
CREATE USER qr_user WITH PASSWORD 'your_secure_password';

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE qr_demo_db TO qr_user;
```

#### Alternative: Using Docker

```bash
# Run PostgreSQL in Docker
docker run --name postgres-liquidation \
  -e POSTGRES_DB=qr_demo_db \
  -e POSTGRES_USER=qr_user \
  -e POSTGRES_PASSWORD=your_secure_password \
  -p 5432:5432 \
  -d postgres:15
```

### 3. Environment Configuration

Create environment variables or update `application.properties`:

```bash
# JWT Secret (generate a secure 256-bit key)
export JWT_SECRET=myVerySecureSecretKey1234567890123456789012345678901234567890ABCD

# Database Configuration
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=qr_demo_db
export DB_USERNAME=qr_user
export DB_PASSWORD=your_secure_password
```

### 4. Build the Application

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the application
mvn package
```

### 5. Run the Application

#### Development Mode

```bash
mvn spring-boot:run
```

#### Production Mode

```bash
java -jar target/demoQrcode-0.0.1-SNAPSHOT.jar
```

The API will be available at `http://localhost:8080`

## 🔧 Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/qr_demo_db
spring.datasource.username=qr_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration=3600000

# Logging
logging.level.com.example.demoQrcode=DEBUG
logging.file.name=logs/demoqrcode.log

# UEMOA Configuration
uemoa.qr.country-code=CI
uemoa.qr.currency=XOF
uemoa.qr.merchant-name=LIQUIDATION APP
```

### Development Properties

Additional settings in `application-dev.properties`:

```properties
# Development-specific settings
spring.profiles.active=dev
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## 🧪 Testing

### Unit Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=LiquidationServiceTest

# Run with coverage
mvn test jacoco:report
```

### Integration Tests

```bash
# Run integration tests only
mvn test -Dtest="*IT"

# Run with specific profile
mvn test -Dspring.profiles.active=test
```

### Manual Testing with Postman

Import the provided Postman collection: `Liquidation_QR_Endpoints.postman_collection.json`

#### Test Authentication Flow

1. **Register User**
   ```
   POST http://localhost:8080/api/auth/register
   {
     "username": "testuser",
     "password": "password123"
   }
   ```

2. **Login**
   ```
   POST http://localhost:8080/api/auth/login
   {
     "username": "testuser",
     "password": "password123"
   }
   ```

3. **Access Protected Resource**
   ```
   GET http://localhost:8080/api/customers
   Authorization: Bearer {token_from_login}
   ```

## 📚 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | User registration |
| POST | `/api/auth/login` | User authentication |
| POST | `/api/auth/register/admin` | Admin registration |

### Customer Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/customers` | List customers (paginated) | ✅ |
| GET | `/api/customers/{id}` | Get customer by ID | ✅ |
| GET | `/api/customers/search` | Search customers | ✅ |
| POST | `/api/customers` | Create customer | ✅ (Admin) |
| PUT | `/api/customers/{id}` | Update customer | ✅ (Admin) |
| DELETE | `/api/customers/{id}` | Delete customer | ✅ (Admin) |

### Liquidation Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/liquidations` | List liquidations | ✅ |
| GET | `/api/liquidations/{id}` | Get liquidation by ID | ✅ |
| POST | `/api/liquidations` | Create liquidation | ✅ (Admin) |
| PUT | `/api/liquidations/{id}` | Update liquidation | ✅ (Admin) |
| DELETE | `/api/liquidations/{id}` | Delete liquidation | ✅ (Admin) |
| POST | `/api/liquidations/{id}/generate-qr` | Generate QR code | ✅ |
| GET | `/api/liquidations/{id}/qr-image` | Get QR image | ✅ |

### QR Code Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/liquidations/qr/static` | Generate static QR | ✅ |
| POST | `/api/liquidations/qr/dynamic` | Generate dynamic QR | ✅ |
| POST | `/api/liquidations/qr/p2p` | Generate P2P QR | ✅ |
| POST | `/api/liquidations/qr/penalty` | Generate penalty QR | ✅ |
| GET | `/api/liquidations/qr/validate` | Validate QR code | ✅ |

## 🔒 Security Features

### JWT Authentication
- Stateless authentication using JSON Web Tokens
- Configurable token expiration
- Secure token storage and transmission

### Authorization
- Role-based access control (RBAC)
- Admin and User roles
- Method-level security annotations

### CORS Configuration
- Configurable allowed origins
- Support for credentials
- Comprehensive header management

### Input Validation
- Bean validation annotations
- Custom validation constraints
- Comprehensive error messages

## 🗄️ Database Schema

### Core Tables

```sql
-- Customers
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

-- Liquidations
CREATE TABLE liquidations (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT REFERENCES customers(id),
    amount DECIMAL(18,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
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

-- Users and Roles
CREATE TABLE users (...);
CREATE TABLE roles (...);
CREATE TABLE user_roles (...);
```

## 🚀 Deployment

### JAR Deployment

```bash
# Build for production
mvn clean package -DskipTests

# Run the application
java -jar target/demoQrcode-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --server.port=8080
```

### Docker Deployment

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/demoQrcode-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Build and run
docker build -t liquidation-backend .
docker run -p 8080:8080 liquidation-backend
```

### Systemd Service

```bash
# Create systemd service
sudo nano /etc/systemd/system/liquidation-backend.service

[Unit]
Description=Liquidation Backend Service
After=network.target

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/liquidation-backend
ExecStart=/usr/bin/java -jar demoQrcode-0.0.1-SNAPSHOT.jar
Restart=always

[Install]
WantedBy=multi-user.target

# Enable and start service
sudo systemctl daemon-reload
sudo systemctl enable liquidation-backend
sudo systemctl start liquidation-backend
```

## 🔧 Development

### IDE Setup

#### IntelliJ IDEA
- Import as Maven project
- Enable annotation processing for Lombok
- Configure Spring Boot run configuration

#### Eclipse
- Import existing Maven project
- Install Lombok plugin
- Configure Java 17 runtime

### Code Style

- Follow Spring Boot conventions
- Use meaningful variable and method names
- Add comprehensive JavaDoc comments
- Maintain consistent code formatting

### Debugging

```bash
# Run with debug enabled
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Attach debugger in IDE on port 5005
```

## 📊 Monitoring

### Health Checks

```
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
```

### Logging

- Application logs: `logs/demoqrcode.log`
- Console logging with configurable levels
- Structured logging with MDC context

## 🐛 Troubleshooting

### Common Issues

#### Database Connection Issues
```bash
# Check PostgreSQL status
sudo systemctl status postgresql

# Test connection
psql -h localhost -U qr_user -d qr_demo_db

# Verify credentials in application.properties
```

#### Port Conflicts
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>

# Change port in application.properties
server.port=8081
```

#### JWT Authentication Issues
- Verify JWT_SECRET is set
- Check token expiration
- Validate token format

#### Memory Issues
```bash
# Increase heap size
java -Xmx2g -Xms1g -jar app.jar

# Configure in application.properties
server.tomcat.max-http-header-size=8192
```

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [UEMOA Standards](https://www.bceao.int/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Version**: 1.0.0
**Last Updated**: January 2025
**Maintained by**: Development Team
