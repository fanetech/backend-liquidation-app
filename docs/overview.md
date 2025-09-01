# Liquidation App - Project Overview

## Description

The Liquidation App is a comprehensive full-stack application designed for managing customer liquidations with integrated QR code payment functionality compliant with UEMOA (West African Economic and Monetary Union) standards. The system provides a secure platform for administrators to manage customers and their liquidation processes, while generating QR codes for seamless payment transactions.

## Key Features

- **User Authentication & Authorization**: JWT-based secure authentication with role-based access control (Admin/User roles)
- **Customer Management**: Complete CRUD operations for customer data management
- **Liquidation Management**: Full lifecycle management of liquidation processes with QR code generation
- **UEMOA QR Code Integration**: Compliant QR code generation for payments according to BCEAO standards
- **Multi-type QR Support**: Static, Dynamic, P2P, and Penalty QR codes
- **RESTful API**: Well-documented API endpoints for all operations
- **Modern Web Interface**: Responsive React frontend with Bootstrap UI components
- **Database Integration**: PostgreSQL with automated migrations
- **Security**: Comprehensive security configuration with CORS, CSRF protection, and input validation

## Technology Stack

### Backend
- **Java 17**: Modern Java runtime
- **Spring Boot 3.4.8**: Framework for rapid application development
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data persistence layer
- **PostgreSQL**: Primary database
- **H2 Database**: In-memory database for testing
- **JWT (JSON Web Tokens)**: Stateless authentication
- **Maven**: Build and dependency management
- **Lombok**: Code generation library

### Frontend
- **React 19.1.1**: Modern JavaScript library for UI
- **Vite**: Fast build tool and development server
- **Bootstrap 5.3.7**: Responsive CSS framework
- **Axios**: HTTP client for API communication
- **React Router DOM**: Client-side routing
- **React Hook Form**: Form management
- **Yup**: Form validation
- **React Toastify**: Notification system
- **QRCode.react**: QR code generation and display

## Business Context

This application serves organizations that need to:
- Manage customer information and liquidation processes
- Generate secure QR codes for payment collection
- Ensure compliance with regional payment standards (UEMOA)
- Provide both administrative and user interfaces
- Maintain audit trails and transaction records

## Project Structure

```
liquidation-app/
├── backend-liquidation-app/          # Spring Boot backend
│   ├── src/main/java/com/example/demoQrcode/
│   │   ├── controller/               # REST API endpoints
│   │   ├── entity/                   # JPA entities
│   │   ├── service/                  # Business logic
│   │   ├── security/                 # Security configuration
│   │   └── config/                   # Application configuration
│   └── src/main/resources/           # Configuration files
├── frontend-liquidation-app/         # React frontend
│   ├── src/
│   │   ├── components/               # React components
│   │   ├── services/                 # API service layer
│   │   └── assets/                   # Static assets
│   └── public/                       # Public assets
└── docs/                             # Documentation
```

## Development Status

The application is in active development with:
- ✅ Core authentication system
- ✅ Customer management functionality
- ✅ Basic liquidation management
- ✅ QR code generation (UEMOA compliant)
- ✅ RESTful API implementation
- ✅ React frontend with routing
- ✅ Database integration with migrations
- 🔄 Testing suite (in progress)
- 🔄 Advanced features (planned)

## Target Users

1. **Administrators**: Full access to all features including customer and liquidation management
2. **Users**: Limited access for viewing relevant information
3. **Integration Partners**: API access for third-party integrations

## Compliance & Standards

- **UEMOA Standards**: Compliant with West African payment standards
- **BCEAO Guidelines**: Adheres to Central Bank of West African States requirements
- **EMVCo Standards**: QR code format compliance
- **Security Best Practices**: Industry-standard security implementations