# Liquidation App Backend

A Spring Boot REST API for managing customer liquidations with UEMOA-compliant QR code payment functionality.

## 🚀 Overview

This backend provides a robust API for:
- Customer management (CRUD operations)
- Liquidation processing and lifecycle management
- QR code generation compliant with UEMOA/BCEAO standards
- JWT-based authentication and role-based authorization
- PostgreSQL database integration with Flyway migrations

## 🛠️ Technology Stack

- **Java 17** with Spring Boot 3.4.8
- **Spring Security** for authentication and authorization
- **JWT** for stateless authentication
- **Spring Data JPA** with Hibernate
- **PostgreSQL** database
- **Flyway** for database migrations
- **Maven** for build management
- **Lombok** for code generation

## 📋 Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Git

## ⚙️ Installation & Setup

### 1. Clone and Setup

```bash
git clone <repository-url>
cd backend-liquidation-app
```

### 2. Database Setup

Create PostgreSQL database:

```sql
CREATE DATABASE qr_demo_db;
CREATE USER qr_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE qr_demo_db TO qr_user;
```

Or use Docker:

```bash
docker run --name postgres-liquidation \
  -e POSTGRES_DB=qr_demo_db \
  -e POSTGRES_USER=qr_user \
  -e POSTGRES_PASSWORD=your_secure_password \
  -p 5432:5432 \
  -d postgres:15
```

### 3. Environment Variables

```bash
export JWT_SECRET=myVerySecureSecretKey1234567890123456789012345678901234567890ABCD
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=qr_demo_db
export DB_USERNAME=qr_user
export DB_PASSWORD=your_secure_password
```

### 4. Build and Run

```bash
mvn clean package
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api`

## 🔧 Configuration

### Application Properties

Key settings in `application.properties`:

```properties
server.port=8080
server.servlet.context-path=/api

spring.datasource.url=jdbc:postgresql://localhost:5432/qr_demo_db
spring.datasource.username=qr_user
spring.datasource.password=your_password

jwt.secret=${JWT_SECRET}
jwt.expiration=3600000

uemoa.qr.country-code=CI
uemoa.qr.currency=XOF
uemoa.qr.merchant-name=LIQUIDATION APP
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=LiquidationServiceTest
```

Import `Liquidation_QR_Endpoints.postman_collection.json` for manual testing.

## 📚 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User authentication
- `POST /api/auth/register/admin` - Admin registration

### Customers
- `GET /api/customers` - List customers (paginated)
- `GET /api/customers/{id}` - Get customer by ID
- `POST /api/customers` - Create customer (Admin)
- `PUT /api/customers/{id}` - Update customer (Admin)
- `DELETE /api/customers/{id}` - Delete customer (Admin)

### Liquidations
- `GET /api/liquidations` - List liquidations
- `GET /api/liquidations/{id}` - Get liquidation by ID
- `POST /api/liquidations` - Create liquidation (Admin)
- `PUT /api/liquidations/{id}` - Update liquidation (Admin)
- `POST /api/liquidations/{id}/generate-qr` - Generate QR code

### QR Code Operations
- `POST /api/liquidations/qr/static` - Generate static QR
- `POST /api/liquidations/qr/dynamic` - Generate dynamic QR
- `POST /api/liquidations/qr/p2p` - Generate P2P QR
- `GET /api/liquidations/qr/validate` - Validate QR code

## 🗄️ Database Schema

### Core Tables

- **customers**: Customer information (id, name, contact, IFU)
- **liquidations**: Liquidation records (id, customer_id, amount, status, QR data)
- **users**: User accounts with roles
- **roles**: User role definitions

Database migrations are handled by Flyway in `src/main/resources/db/migration/`.

## 🔧 Development

### Project Structure

```
src/main/java/com/example/demoQrcode/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data transfer objects
├── entity/         # JPA entities
├── repository/     # Data access layer
├── security/       # Security components
└── service/        # Business logic
```

### Code Style

- Follow Spring Boot conventions
- Use Lombok annotations
- Add JavaDoc comments
- Maintain consistent formatting

### Debugging

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

Attach debugger in IDE on port 5005.

## 📊 Monitoring

- Health checks: `/actuator/health`
- Application logs: `logs/demoqrcode.log`
- Configurable logging levels

## 🐛 Troubleshooting

### Common Issues

- **Database Connection**: Verify PostgreSQL is running and credentials are correct
- **Port Conflicts**: Check if port 8080 is available
- **JWT Issues**: Ensure JWT_SECRET is properly set
- **Memory Issues**: Increase heap size with `-Xmx2g -Xms1g`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes and add tests
4. Ensure all tests pass
5. Submit a pull request

---

**Version**: 1.0.0
**Last Updated**: January 2025
