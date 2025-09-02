# System Architecture

## High-Level Architecture

The Liquidation App follows a modern layered architecture pattern with clear separation of concerns, ensuring maintainability, scalability, and testability.

### Architecture Layers

```
┌─────────────────────────────────────┐
│         Client Layer                │
│   React Frontend + Vite + Bootstrap │
└─────────────────────────────────────┘
                │
                ▼
┌─────────────────────────────────────┐
│      API Gateway Layer              │
│  Spring Boot REST API + Controllers │
└─────────────────────────────────────┘
                │
                ▼
┌─────────────────────────────────────┐
│     Business Logic Layer            │
│   Service Layer + Business Rules    │
└─────────────────────────────────────┘
                │
                ▼
┌─────────────────────────────────────┐
│      Data Access Layer              │
│   Repository Layer + JPA/Hibernate  │
└─────────────────────────────────────┘
                │
                ▼
┌─────────────────────────────────────┐
│         Database                    │
│         PostgreSQL                  │
└─────────────────────────────────────┘
```

### Security & External Systems

```
┌─────────────────────────────────────┐     ┌─────────────────────────────────────┐
│        Security Layer               │     │      External Systems             │
│   JWT Authentication + Spring       │     │   UEMOA QR Module + BCEAO        │
│   Security + Role-based Access      │     │   Integration + PostgreSQL       │
└─────────────────────────────────────┘     └─────────────────────────────────────┘
                ▲                                       ▲
                │                                       │
                └───────────────────────────────────────┘
                        Security protects all layers
```

### Data Flow

```
User Action → Client Layer → API Gateway → Business Logic → Data Access → Database
                    ▲              ▲              ▲             ▲
                    │              │              │             │
                    └──────────────┼──────────────┼─────────────┘
                                   │              │
                                   └──────────────┘
                                   Security Layer
```

## Component Architecture

### Frontend Architecture

**React Application Structure:**

```
┌─────────────────────────────────────┐
│           App.jsx                   │
│   Main component + routing + layout │
└─────────────────────────────────────┘
                │
                ▼
┌─────────────────────────────────────┐
│         React Router DOM            │
│     Client-side routing system      │
└─────────────────────────────────────┘
                │
       ┌────────┴────────┐
       ▼                 ▼
┌─────────────┐    ┌─────────────┐
│ Components  │    │  Services   │
│ UI elements │    │ API calls   │
└─────────────┘    └─────────────┘
       │                 │
       └────────┬────────┘
                ▼
┌─────────────────────────────────────┐
│       State Management             │
│   React Hooks + Context API        │
└─────────────────────────────────────┘
```

**Key Components:**

```
Authentication Components:
├── Login.jsx          - User authentication
├── Register.jsx       - User registration
└── ProtectedRoute.jsx - Route protection

Customer Management:
├── CustomersPage.jsx     - Customer list & search
├── CustomerModals.jsx    - CRUD operations
└── Customer forms        - Create/Edit forms

Liquidation Management:
├── LiquidationsPage.jsx  - Liquidation dashboard
├── LiquidationDetail.jsx - Detailed view
├── LiquidationModals.jsx - CRUD operations
└── QRCodeDisplay.jsx     - QR code interface

Administrative:
├── Admin.jsx             - Admin dashboard
├── Home.jsx              - Landing page
└── QRCodeDemo.jsx        - Demo component
```

### Backend Architecture

**Spring Boot Application Structure:**

```
┌─────────────────────────────────────┐
│    DemoQrcodeApplication.java       │
│       Main Application Class        │
└─────────────────────────────────────┘
                │
                ▼
┌─────────────────────────────────────┐
│        Configuration Layer          │
│ Security + CORS + Database + UEMOA  │
└─────────────────────────────────────┘
                │
       ┌────────┴────────┐
       ▼                 ▼
┌─────────────┐    ┌─────────────┐
│ Controllers │    │  Services   │
│ REST APIs   │    │ Business    │
│ Endpoints   │    │ Logic       │
└─────────────┘    └─────────────┘
       │                 │
       └────────┬────────┘
                ▼
┌─────────────────────────────────────┐
│       Data Access Layer             │
│ Repositories + Entities + DTOs      │
└─────────────────────────────────────┘
```

**Security Components:**

```
┌─────────────────────────────────────┐
│       Security Components           │
├─────────────────────────────────────┤
│ JWT Util: Token generation &        │
│ validation                          │
│                                     │
│ Security Config: Authentication     │
│ filters & configuration             │
│                                     │
│ User Details Service: User auth     │
│ & role management                   │
└─────────────────────────────────────┘
```

**UEMOA Integration:**

```
┌─────────────────────────────────────┐
│     UEMOA Integration Layer         │
├─────────────────────────────────────┤
│ UEMOA QR Service: QR generation     │
│ & validation                        │
│                                     │
│ UEMOA Config: Payment settings      │
│ & BCEAO compliance                  │
│                                     │
│ QR Controllers: Payment endpoints   │
└─────────────────────────────────────┘
```

## Data Flow Architecture

**User Interaction Flow:**
1. User performs action (login, CRUD operation) in Frontend
2. Frontend sends HTTP request with JWT token to Backend
3. Backend validates JWT token
4. Backend queries/updates data in Database
5. Database returns data response to Backend
6. Backend processes business logic
7. If QR generation needed, Backend calls UEMOA service
8. UEMOA service returns QR data/image
9. Backend sends JSON response to Frontend
10. Frontend updates UI for user

**QR Generation Flow:** Backend processes QR generation through UEMOA integration

## Database Schema

### Core Tables

```
┌─────────────────────────────────────┐
│         CUSTOMER Table              │
├─────────────────────────────────────┤
│ id (PK)           BIGINT            │
│ last_name         VARCHAR(100)      │
│ first_name        VARCHAR(100)      │
│ address           VARCHAR(255)      │
│ ifu               VARCHAR(50)       │
│ phone             VARCHAR(20)       │
│ email             VARCHAR(100) UNI  │
│ created_at        TIMESTAMP         │
│ updated_at        TIMESTAMP         │
└─────────────────────────────────────┘
                    │
                    │ 1:N
                    ▼
┌─────────────────────────────────────┐
│       LIQUIDATION Table             │
├─────────────────────────────────────┤
│ id (PK)           BIGINT            │
│ customer_id (FK)  BIGINT            │
│ amount            DECIMAL(18,2)     │
│ status            VARCHAR(20)       │
│ qr_code_data      TEXT              │
│ qr_image_base64   TEXT              │
│ merchant_channel  VARCHAR(64)       │
│ transaction_id    VARCHAR(128)      │
│ qr_type           VARCHAR(16)       │
│ qr_generated_at   TIMESTAMP         │
│ penalty_amount    DECIMAL(18,2)     │
│ total_amount      DECIMAL(18,2)     │
│ created_at        TIMESTAMP         │
│ updated_at        TIMESTAMP         │
└─────────────────────────────────────┘
```

```
┌─────────────────────────────────────┐
│          USER Table                 │
├─────────────────────────────────────┤
│ id (PK)           BIGINT            │
│ username          VARCHAR(50) UNI   │
│ password          VARCHAR(255)      │
│ email             VARCHAR(100)      │
│ created_at        TIMESTAMP         │
└─────────────────────────────────────┘
                    │
                    │ N:M
                    ▼
┌─────────────────────────────────────┐
│       USER_ROLES Table              │
├─────────────────────────────────────┤
│ user_id (FK)      BIGINT            │
│ role_id (FK)      BIGINT            │
└─────────────────────────────────────┘
                    ▲
                    │ N:M
┌─────────────────────────────────────┐
│          ROLE Table                 │
├─────────────────────────────────────┤
│ id (PK)           BIGINT            │
│ name              VARCHAR(50) UNI   │
└─────────────────────────────────────┘
```

### Relationships

```
CUSTOMER ────1:N────► LIQUIDATION
   ▲                      │
   │                      │
   └───────references─────┘

USER ────N:M────► USER_ROLES ◄────N:M──── ROLE
```

## Security Architecture

### Authentication Flow

```
┌─────────────────┐
│   Client Request│
│  (with JWT)     │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│ JWT Token       │
│ Present?        │
└─────────────────┘
         │
    ┌────┴────┐
    │         │
┌───▼──┐  ┌───▼──┐
│  No   │  │ Yes  │
│ 401   │  │Validate│
│Unauthorized││JWT     │
└────────┘  └────────┘
               │
               ▼
┌─────────────────┐
│ JWT Valid?      │
└─────────────────┘
         │
    ┌────┴────┐
    │         │
┌───▼──┐  ┌───▼──┐
│Invalid│  │Valid │
│ 401   │  │Extract│
│Unauthorized││User Info│
└────────┘  └────────┘
               │
               ▼
┌─────────────────┐
│ Check Roles &   │
│ Permissions     │
└─────────────────┘
         │
    ┌────┴────┐
    │         │
┌───▼──┐  ┌───▼──┐
│Insufficient││Authorized│
│ 403 Forbidden││Process   │
│             ││Request    │
└─────────────┘└───────────┘
```

### Security Components

```
┌─────────────────────────────────────┐
│       Security Framework            │
├─────────────────────────────────────┤
│                                    │
│  ┌─────────────────────────────────┐ │
│  │     Spring Security             │ │
│  │   Main security framework       │ │
│  └─────────────────────────────────┘ │
│                                    │
│  ┌─────────────────────────────────┐ │
│  │  JWT Authentication Filter      │ │
│  │  Intercepts & validates requests│ │
│  └─────────────────────────────────┘ │
│                                    │
│  ┌─────────────────────────────────┐ │
│  │     UserDetailsService          │ │
│  │  Loads user details for auth    │ │
│  └─────────────────────────────────┘ │
│                                    │
│  ┌─────────────────────────────────┐ │
│  │     Password Encoder            │ │
│  │  Secure password hashing        │ │
│  └─────────────────────────────────┘ │
│                                    │
│  ┌─────────────────────────────────┐ │
│  │    CORS Configuration           │ │
│  │ Cross-origin resource sharing   │ │
│  └─────────────────────────────────┘ │
│                                    │
└─────────────────────────────────────┘
```

## Technology Stack Details

### Backend Technologies

```
┌─────────────────────────────────────┐
│        Backend Stack                │
├─────────────────────────────────────┤
│ Framework:    Spring Boot 3.4.8     │
│ Language:     Java 17               │
│ Security:     Spring Security + JWT │
│ Database:     PostgreSQL + H2       │
│ Build:        Maven                 │
│ ORM:          Hibernate/JPA         │
│ Validation:   Bean Validation       │
└─────────────────────────────────────┘
```

### Frontend Technologies

```
┌─────────────────────────────────────┐
│        Frontend Stack               │
├─────────────────────────────────────┤
│ Framework:    React 19.1.1          │
│ Build:        Vite                  │
│ Styling:      Bootstrap 5.3.7       │
│ Routing:      React Router DOM      │
│ HTTP:         Axios 1.11.0          │
│ Forms:        React Hook Form       │
│ Validation:   Yup 1.7.0             │
│ Notifications: React Toastify       │
│ QR:           QRCode.react 4.2.0    │
└─────────────────────────────────────┘
```

### External Integrations

```
┌─────────────────────────────────────┐
│    External Integrations            │
├─────────────────────────────────────┤
│ UEMOA QR Module: BCEAO compliance   │
│ Payment System: BCEAO Interface     │
│ Database: PostgreSQL + Flyway       │
└─────────────────────────────────────┘
```

## Design Patterns Used

```
┌─────────────────────────────────────┐
│      Design Patterns                │
├─────────────────────────────────────┤
│ • Layered Architecture              │
│ • Repository Pattern                │
│ • Service Layer Pattern             │
│ • DTO Pattern                       │
│ • Factory Pattern                   │
│ • Strategy Pattern                  │
└─────────────────────────────────────┘
```