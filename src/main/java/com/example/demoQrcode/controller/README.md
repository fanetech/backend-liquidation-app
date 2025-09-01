# 🔌 API Controllers

This directory contains all the REST API controllers for the Liquidation App backend. Controllers handle HTTP requests, process them through the service layer, and return appropriate responses.

## 📋 Overview

The controllers are organized by functional domains and follow Spring MVC patterns with comprehensive error handling and validation.

## 🏗️ Controller Architecture

### Base Controller Features
- **Request Mapping**: RESTful URL mappings
- **Request/Response Handling**: JSON serialization/deserialization
- **Validation**: Input validation with Bean Validation
- **Error Handling**: Global exception handling
- **Security**: Method-level security annotations
- **Documentation**: OpenAPI/Swagger annotations

### Common Patterns
```java
@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
@Tag(name = "Resource", description = "Resource management API")
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all resources")
    public ResponseEntity<Page<ResourceDTO>> getAll(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(resourceService.findAll(PageRequest.of(page, size)));
    }
}
```

## 📁 Controller Files

### Authentication Controller (`AuthController.java`)
**Purpose**: User authentication and registration
**Endpoints**:
- `POST /api/auth/login` - User login
- `POST /api/auth/register/user` - User registration
- `POST /api/auth/register/admin` - Admin registration

**Key Features**:
- JWT token generation and validation
- Password encoding with BCrypt
- Role-based registration
- Comprehensive input validation

### Customer Controller (`CustomerController.java`)
**Purpose**: Customer management operations
**Endpoints**:
- `GET /api/customers` - List customers (paginated)
- `GET /api/customers/{id}` - Get customer by ID
- `GET /api/customers/search` - Search customers
- `POST /api/customers` - Create customer (Admin only)
- `PUT /api/customers/{id}` - Update customer (Admin only)
- `DELETE /api/customers/{id}` - Delete customer (Admin only)

**Key Features**:
- Pagination support with Spring Data
- Search functionality with QueryDSL
- Admin-only write operations
- Customer data validation

### Liquidation Controller (`LiquidationController.java`)
**Purpose**: Liquidation lifecycle management
**Endpoints**:
- `GET /api/liquidations` - List liquidations with filters
- `GET /api/liquidations/{id}` - Get liquidation details
- `POST /api/liquidations` - Create liquidation (Admin only)
- `PUT /api/liquidations/{id}` - Update liquidation (Admin only)
- `DELETE /api/liquidations/{id}` - Delete liquidation (Admin only)
- `POST /api/liquidations/{id}/generate-qr` - Generate QR code
- `GET /api/liquidations/{id}/qr-image` - Get QR code image
- `PUT /api/liquidations/{id}/regenerate-qr` - Regenerate QR code

**Key Features**:
- Advanced filtering (customer, status, date range)
- QR code generation integration
- Penalty calculation endpoints
- Status management workflow

### QR Code Controllers

#### LiquidationQRController (`LiquidationQRController.java`)
**Purpose**: QR code generation for different payment types
**Endpoints**:
- `POST /api/liquidations/qr/static` - Static QR codes
- `POST /api/liquidations/qr/dynamic` - Dynamic QR codes
- `POST /api/liquidations/qr/p2p` - P2P payment QR codes
- `POST /api/liquidations/qr/penalty` - Penalty payment QR codes

#### LiquidationQRDataController (`LiquidationQRDataController.java`)
**Purpose**: QR-related data queries and operations
**Endpoints**:
- `GET /api/liquidations/data/with-qr` - Liquidations with QR codes
- `GET /api/liquidations/data/without-qr` - Liquidations without QR codes
- `GET /api/liquidations/data/type/{type}` - Filter by QR type
- `GET /api/liquidations/data/stats/count-by-type` - Statistics

### UEMOA Controller (`UemoaQRController.java`)
**Purpose**: Direct UEMOA QR code operations
**Endpoints**:
- `POST /api/uemoa/qr/generate-static` - Generate static QR
- `POST /api/uemoa/qr/generate-dynamic` - Generate dynamic QR
- `POST /api/uemoa/qr/parse` - Parse QR code
- `GET /api/uemoa/qr/test` - Test QR generation
- `GET /api/uemoa/qr/health` - Health check

### Exception Handler (`RestExceptionHandler.java`)
**Purpose**: Global exception handling for consistent error responses
**Handles**:
- `MethodArgumentNotValidException` - Validation errors
- `IllegalArgumentException` - Invalid arguments
- `DataIntegrityViolationException` - Database constraint violations

## 🔒 Security Implementation

### Method-Level Security
```java
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@PostMapping
public ResponseEntity<CustomerDTO> create(@Valid @RequestBody CustomerDTO customerDTO) {
    return ResponseEntity.status(HttpStatus.CREATED)
                        .body(customerService.create(customerDTO));
}
```

### Authentication Integration
- JWT tokens extracted from Authorization header
- User context available through `SecurityContextHolder`
- Role-based access control for sensitive operations

## ✅ Validation & Error Handling

### Input Validation
```java
@PostMapping
public ResponseEntity<CustomerDTO> create(@Valid @RequestBody CustomerDTO customerDTO) {
    // Validation happens automatically
    return ResponseEntity.status(HttpStatus.CREATED)
                        .body(customerService.create(customerDTO));
}
```

### Error Response Format
```json
{
  "timestamp": "2025-01-01T12:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/customers",
  "errors": [
    {
      "field": "email",
      "message": "Email should be valid",
      "rejectedValue": "invalid-email"
    }
  ]
}
```

## 📊 Response Formats

### Paginated Response
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 5,
  "totalElements": 50,
  "last": false,
  "first": true,
  "numberOfElements": 10,
  "size": 10,
  "number": 0,
  "sort": {
    "sorted": false
  },
  "empty": false
}
```

### Single Resource Response
```json
{
  "id": 1,
  "name": "Resource Name",
  "createdAt": "2025-01-01T10:00:00Z",
  "updatedAt": "2025-01-01T10:00:00Z"
}
```

## 🔧 Development Guidelines

### Controller Best Practices
1. **Single Responsibility**: Each controller handles one domain
2. **Consistent Naming**: RESTful URL patterns
3. **Proper HTTP Methods**: GET, POST, PUT, DELETE appropriately
4. **Status Codes**: Use appropriate HTTP status codes
5. **Documentation**: Comprehensive OpenAPI annotations

### Error Handling
1. **Global Exception Handler**: Centralized error handling
2. **Consistent Format**: Standardized error response format
3. **Security**: Don't expose sensitive information in errors
4. **Logging**: Proper error logging for debugging

### Validation
1. **Bean Validation**: Use JSR-303 annotations
2. **Custom Validators**: Implement custom validation logic
3. **Clear Messages**: Provide clear validation error messages
4. **Security**: Validate input to prevent injection attacks

## 🧪 Testing Controllers

### Unit Testing
```java
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Test
    void shouldReturnCustomers() throws Exception {
        mockMvc.perform(get("/api/customers"))
               .andExpect(status().isOk());
    }
}
```

### Integration Testing
```java
@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateCustomer() throws Exception {
        mockMvc.perform(post("/api/customers")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"name\":\"Test Customer\"}"))
               .andExpect(status().isCreated());
    }
}
```

## 📈 Performance Considerations

### Optimization Techniques
1. **Pagination**: Always use pagination for list endpoints
2. **Caching**: Implement caching for frequently accessed data
3. **Async Processing**: Use async for long-running operations
4. **Database Optimization**: Optimize queries and use indexes

### Monitoring
1. **Metrics**: Spring Boot Actuator for endpoint metrics
2. **Logging**: Request/response logging for debugging
3. **Health Checks**: Endpoint health monitoring
4. **Performance Monitoring**: Response time tracking

## 🔗 Related Components

### Service Layer
- Controllers delegate business logic to services
- Services handle data transformation and validation
- Service methods are transactional where needed

### Repository Layer
- Controllers don't access repositories directly
- All data access goes through service layer
- Repository interfaces define data access contracts

### DTOs
- Data Transfer Objects for API requests/responses
- Separate from entity models for security
- Validation annotations on DTOs

## 🚀 Future Enhancements

### Planned Features
- **API Versioning**: URL-based API versioning
- **Rate Limiting**: Request rate limiting per user
- **Caching**: Response caching with Redis
- **WebSocket Support**: Real-time notifications
- **GraphQL**: Alternative query interface
- **API Documentation**: Enhanced Swagger documentation

### Improvements
- **Request/Response Logging**: Comprehensive audit logging
- **Metrics Collection**: Detailed performance metrics
- **Circuit Breaker**: Fault tolerance for external services
- **API Gateway Integration**: Centralized API management

---

**Directory**: `src/main/java/com/example/demoQrcode/controller/`
**Last Updated**: January 2025
**Controllers**: 7 files
**Endpoints**: 50+ REST endpoints