# System Architecture

## High-Level Architecture

The Liquidation App follows a modern layered architecture pattern with clear separation of concerns, ensuring maintainability, scalability, and testability.

```mermaid
graph TB
    subgraph "Client Layer"
        A[React Frontend<br/>Vite + Bootstrap]
    end

    subgraph "API Gateway Layer"
        B[Spring Boot REST API<br/>Controllers]
    end

    subgraph "Business Logic Layer"
        C[Service Layer<br/>Business Rules & Logic]
    end

    subgraph "Data Access Layer"
        D[Repository Layer<br/>JPA/Hibernate]
    end

    subgraph "Security Layer"
        E[JWT Authentication<br/>Spring Security]
    end

    subgraph "External Systems"
        F[UEMOA QR Module<br/>BCEAO Integration]
        G[PostgreSQL Database]
    end

    A --> B
    B --> C
    B --> E
    C --> D
    C --> F
    D --> G
    E --> G

    style A fill:#e1f5fe
    style B fill:#f3e5f5
    style C fill:#e8f5e8
    style D fill:#fff3e0
    style E fill:#ffebee
    style F fill:#f3e5f5
    style G fill:#e0f2f1
```

## Component Architecture

### Frontend Architecture

```mermaid
graph TD
    subgraph "React Application"
        H[App.jsx<br/>Main Component]
        I[Router<br/>React Router DOM]
        J[Components<br/>UI Components]
        K[Services<br/>API Services]
        L[Context/Store<br/>State Management]
    end

    subgraph "Key Components"
        M[Login/Register<br/>Authentication]
        N[Dashboard<br/>Main Interface]
        O[Customer Management<br/>CRUD Operations]
        P[Liquidation Management<br/>QR Generation]
        Q[QR Code Display<br/>Payment Interface]
    end

    H --> I
    I --> J
    J --> K
    J --> L
    J --> M
    J --> N
    J --> O
    J --> P
    J --> Q

    style H fill:#e3f2fd
    style I fill:#f3e5f5
    style J fill:#e8f5e8
    style K fill:#fff3e0
    style L fill:#ffebee
```

### Backend Architecture

```mermaid
graph TD
    subgraph "Spring Boot Application"
        R[Main Application<br/>DemoQrcodeApplication]
        S[Configuration<br/>Security, CORS, DB]
        T[Controllers<br/>REST Endpoints]
        U[Services<br/>Business Logic]
        V[Repositories<br/>Data Access]
        W[Entities<br/>Domain Models]
        X[DTOs<br/>Data Transfer Objects]
    end

    subgraph "Security Components"
        Y[JWT Util<br/>Token Management]
        Z[Security Config<br/>Auth Filters]
        AA[User Details Service<br/>Authentication]
    end

    subgraph "UEMOA Integration"
        BB[UEMOA QR Service<br/>QR Generation]
        CC[UEMOA Config<br/>Payment Settings]
        DD[QR Controllers<br/>Payment Endpoints]
    end

    R --> S
    S --> T
    T --> U
    U --> V
    V --> W
    T --> X
    S --> Y
    S --> Z
    S --> AA
    U --> BB
    BB --> CC
    T --> DD

    style R fill:#e3f2fd
    style S fill:#f3e5f5
    style T fill:#e8f5e8
    style U fill:#fff3e0
    style V fill:#ffebee
    style W fill:#fce4ec
    style X fill:#e0f2f1
```

## Data Flow Architecture

```mermaid
sequenceDiagram
    participant User
    participant Frontend
    participant Backend
    participant Database
    participant UEMOA

    User->>Frontend: User Action (Login/CRUD)
    Frontend->>Backend: HTTP Request + JWT
    Backend->>Backend: Validate JWT
    Backend->>Database: Query/Update Data
    Database-->>Backend: Data Response
    Backend->>Backend: Business Logic Processing
    Backend->>UEMOA: QR Code Generation (if needed)
    UEMOA-->>Backend: QR Data/Image
    Backend-->>Frontend: JSON Response
    Frontend-->>User: UI Update

    Note over Backend,UEMOA: QR Generation Flow
```

## Database Schema

```mermaid
erDiagram
    CUSTOMER ||--o{ LIQUIDATION : has
    USER ||--o{ USER_ROLES : has
    ROLE ||--o{ USER_ROLES : assigned

    CUSTOMER {
        bigint id PK
        varchar last_name
        varchar first_name
        varchar address
        varchar ifu
        varchar phone
        varchar email
        timestamp created_at
        timestamp updated_at
    }

    LIQUIDATION {
        bigint id PK
        bigint customer_id FK
        decimal amount
        varchar status
        text qr_code_data
        text qr_image_base64
        varchar merchant_channel
        varchar transaction_id
        varchar qr_type
        timestamp qr_generated_at
        decimal penalty_amount
        decimal total_amount
        timestamp created_at
        timestamp updated_at
    }

    USER {
        bigint id PK
        varchar username
        varchar password
        varchar email
        timestamp created_at
    }

    ROLE {
        bigint id PK
        varchar name
    }

    USER_ROLES {
        bigint user_id FK
        bigint role_id FK
    }
```

## Security Architecture

```mermaid
graph TD
    subgraph "Authentication Flow"
        AAA[Client Request] --> BBB{JWT Present?}
        BBB -->|No| CCC[401 Unauthorized]
        BBB -->|Yes| DDD[Validate JWT]
        DDD -->|Invalid| CCC
        DDD -->|Valid| EEE[Extract User Info]
        EEE --> FFF{Check Roles}
        FFF -->|Insufficient| GGG[403 Forbidden]
        FFF -->|Authorized| HHH[Process Request]
    end

    subgraph "Security Components"
        III[Spring Security]
        JJJ[JWT Authentication Filter]
        KKK[UserDetailsService]
        LLL[Password Encoder]
        MMM[CORS Configuration]
    end

    III --> JJJ
    III --> KKK
    III --> LLL
    III --> MMM

    style AAA fill:#e3f2fd
    style CCC fill:#ffebee
    style GGG fill:#ffebee
    style HHH fill:#e8f5e8
```

## Deployment Architecture

```mermaid
graph TD
    subgraph "Production Environment"
        NNN[Load Balancer<br/>Nginx/HAProxy]
        OOO[Application Server 1<br/>Spring Boot]
        PPP[Application Server 2<br/>Spring Boot]
        QQQ[Database Server<br/>PostgreSQL]
        RRR[File Storage<br/>For QR Images]
    end

    subgraph "Development Environment"
        SSS[Local Development<br/>IDE + Vite Dev Server]
        TTT[H2 Database<br/>In-Memory]
    end

    NNN --> OOO
    NNN --> PPP
    OOO --> QQQ
    PPP --> QQQ
    OOO --> RRR
    PPP --> RRR

    SSS --> TTT

    style NNN fill:#e3f2fd
    style OOO fill:#e8f5e8
    style PPP fill:#e8f5e8
    style QQQ fill:#fff3e0
    style SSS fill:#f3e5f5
```

## Technology Stack Details

### Backend Technologies
- **Framework**: Spring Boot 3.4.8
- **Language**: Java 17
- **Security**: Spring Security + JWT
- **Database**: PostgreSQL (Production), H2 (Testing)
- **Build Tool**: Maven
- **ORM**: Hibernate/JPA
- **Validation**: Bean Validation
- **Documentation**: SpringDoc OpenAPI

### Frontend Technologies
- **Framework**: React 19.1.1
- **Build Tool**: Vite
- **Styling**: Bootstrap 5.3.7
- **Routing**: React Router DOM 7.8.0
- **HTTP Client**: Axios 1.11.0
- **Forms**: React Hook Form 7.62.0
- **Validation**: Yup 1.7.0
- **Notifications**: React Toastify 11.0.5
- **QR Generation**: QRCode.react 4.2.0

### External Integrations
- **UEMOA QR Module**: Custom implementation for BCEAO compliance
- **Payment System**: BCEAO Payment Interface integration
- **Database**: PostgreSQL with Flyway migrations

## Design Patterns Used

1. **Layered Architecture**: Clear separation between presentation, business, and data layers
2. **Repository Pattern**: Abstract data access layer
3. **Service Layer Pattern**: Business logic encapsulation
4. **DTO Pattern**: Data transfer object for API communication
5. **Factory Pattern**: QR code generation based on type
6. **Strategy Pattern**: Different authentication strategies
7. **Observer Pattern**: Event-driven architecture for notifications

## Performance Considerations

- **Database Indexing**: Optimized queries with proper indexing
- **Connection Pooling**: HikariCP for efficient database connections
- **Caching**: Application-level caching for frequently accessed data
- **Lazy Loading**: JPA lazy loading for related entities
- **Pagination**: Server-side pagination for large datasets
- **Async Processing**: Asynchronous QR code generation
- **CDN**: Static asset delivery optimization

## Scalability Features

- **Horizontal Scaling**: Stateless application design
- **Database Sharding**: Potential for future database scaling
- **Microservices Ready**: Modular architecture for service decomposition
- **API Gateway**: Centralized API management
- **Load Balancing**: Distributed request handling
- **Caching Layer**: Redis integration ready
- **Message Queue**: Asynchronous processing capabilities