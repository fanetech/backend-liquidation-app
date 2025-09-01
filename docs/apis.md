# API Documentation

## Overview

The Liquidation App provides a comprehensive REST API for managing customers, liquidations, and QR code generation. The API follows RESTful conventions and uses JSON for data exchange.

## Base URL

```
http://localhost:8080/api
```

## Authentication

All API endpoints (except authentication) require JWT token authentication.

### Headers

```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

## Response Format

### Success Response

```json
{
  "data": { ... },
  "message": "Operation successful",
  "timestamp": "2025-01-01T12:00:00Z"
}
```

### Error Response

```json
{
  "error": "Error message",
  "status": 400,
  "timestamp": "2025-01-01T12:00:00Z"
}
```

## Authentication Endpoints

### POST /auth/login

Authenticate user and return JWT token.

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "user",
  "role": "USER",
  "redirect": "/"
}
```

**Status Codes:**
- `200` - Success
- `401` - Invalid credentials
- `400` - Bad request

### POST /auth/register/user

Register a new regular user.

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "message": "User registered successfully"
}
```

### POST /auth/register/admin

Register a new admin user (requires admin authentication).

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

## Customer Endpoints

### GET /customers

Get paginated list of customers.

**Query Parameters:**
- `page` (integer, default: 0) - Page number
- `size` (integer, default: 10) - Page size

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "lastName": "Doe",
      "firstName": "John",
      "address": "123 Main St",
      "ifu": "CI123456789",
      "phone": "+2250102030405",
      "email": "john.doe@example.com",
      "createdAt": "2025-01-01T10:00:00Z",
      "updatedAt": "2025-01-01T10:00:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### GET /customers/search

Search customers with pagination.

**Query Parameters:**
- `q` (string) - Search term
- `page` (integer, default: 0)
- `size` (integer, default: 10)

### GET /customers/{id}

Get customer by ID.

**Path Parameters:**
- `id` (long) - Customer ID

**Response:**
```json
{
  "id": 1,
  "lastName": "Doe",
  "firstName": "John",
  "address": "123 Main St",
  "ifu": "CI123456789",
  "phone": "+2250102030405",
  "email": "john.doe@example.com",
  "createdAt": "2025-01-01T10:00:00Z",
  "updatedAt": "2025-01-01T10:00:00Z"
}
```

### POST /customers

Create a new customer (Admin only).

**Request Body:**
```json
{
  "lastName": "string",
  "firstName": "string",
  "address": "string",
  "ifu": "string",
  "phone": "string",
  "email": "string"
}
```

### PUT /customers/{id}

Update customer (Admin only).

**Path Parameters:**
- `id` (long) - Customer ID

**Request Body:**
```json
{
  "lastName": "string",
  "firstName": "string",
  "address": "string",
  "ifu": "string",
  "phone": "string",
  "email": "string"
}
```

### DELETE /customers/{id}

Delete customer (Admin only).

**Path Parameters:**
- `id` (long) - Customer ID

## Liquidation Endpoints

### GET /liquidations

Get paginated list of liquidations with optional filters.

**Query Parameters:**
- `customerId` (long) - Filter by customer
- `status` (string) - Filter by status (PENDING, PAID, OVERDUE)
- `startDate` (string) - Start date (YYYY-MM-DD)
- `endDate` (string) - End date (YYYY-MM-DD)
- `page` (integer, default: 0)
- `size` (integer, default: 10)

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "customer": {
        "id": 1,
        "lastName": "Doe",
        "firstName": "John"
      },
      "amount": 1000.00,
      "status": "PENDING",
      "qrCodeData": "000201...",
      "qrImageBase64": "iVBORw0KGgoAAAANSUhEUgAA...",
      "merchantChannel": "int.bceao.pi",
      "transactionId": "TXN123456789",
      "qrType": "STATIC",
      "qrGeneratedAt": "2025-01-01T11:00:00Z",
      "penaltyAmount": 0.00,
      "totalAmount": 1000.00,
      "createdAt": "2025-01-01T10:00:00Z",
      "updatedAt": "2025-01-01T10:00:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### GET /liquidations/search

Search liquidations by term.

**Query Parameters:**
- `q` (string) - Search term
- `page` (integer, default: 0)
- `size` (integer, default: 10)

### GET /liquidations/{id}

Get liquidation by ID.

**Path Parameters:**
- `id` (long) - Liquidation ID

### POST /liquidations

Create a new liquidation (Admin only).

**Request Body:**
```json
{
  "customerId": 1,
  "amount": 1000.00,
  "status": "PENDING"
}
```

### PUT /liquidations/{id}

Update liquidation (Admin only).

**Path Parameters:**
- `id` (long) - Liquidation ID

**Request Body:**
```json
{
  "customerId": 1,
  "amount": 1000.00,
  "status": "PENDING"
}
```

### DELETE /liquidations/{id}

Delete liquidation (Admin only).

**Path Parameters:**
- `id` (long) - Liquidation ID

### GET /liquidations/customer/{customerId}

Get liquidations for a specific customer.

**Path Parameters:**
- `customerId` (long) - Customer ID

### GET /liquidations/{id}/penalty

Calculate penalty for a liquidation.

**Path Parameters:**
- `id` (long) - Liquidation ID

**Query Parameters:**
- `dailyRate` (decimal, default: 0.0) - Daily penalty rate

**Response:**
```json
{
  "liquidationId": 1,
  "baseAmount": 1000.00,
  "penaltyAmount": 50.00,
  "totalAmount": 1050.00,
  "calculationDate": "2025-01-01T12:00:00Z"
}
```

### POST /liquidations/{id}/generate-qr

Generate QR code for liquidation.

**Path Parameters:**
- `id` (long) - Liquidation ID

**Request Body:**
```json
{
  "qrType": "STATIC",
  "amount": 1000.00,
  "merchantName": "Liquidation App"
}
```

**Response:**
```json
{
  "qrCodeData": "000201...",
  "qrImageBase64": "iVBORw0KGgoAAAANSUhEUgAA...",
  "transactionId": "TXN123456789",
  "qrType": "STATIC",
  "generatedAt": "2025-01-01T12:00:00Z"
}
```

### GET /liquidations/{id}/qr-image

Get QR code image for liquidation.

**Path Parameters:**
- `id` (long) - Liquidation ID

**Response:**
Base64 encoded PNG image

### PUT /liquidations/{id}/regenerate-qr

Regenerate QR code for liquidation.

**Path Parameters:**
- `id` (long) - Liquidation ID

## QR Code Endpoints

### POST /liquidations/qr/static

Generate static QR code.

**Request Body:**
```json
{
  "amount": 1000,
  "merchantName": "Merchant Name",
  "reference": "REF123"
}
```

### POST /liquidations/qr/dynamic

Generate dynamic QR code.

**Request Body:**
```json
{
  "amount": 1000,
  "merchantName": "Merchant Name",
  "reference": "REF123"
}
```

### POST /liquidations/qr/p2p

Generate P2P QR code.

**Request Body:**
```json
{
  "amount": 1000,
  "merchantName": "Merchant Name",
  "reference": "REF123"
}
```

### POST /liquidations/qr/penalty

Generate penalty QR code.

**Request Body:**
```json
{
  "amount": 1000,
  "merchantName": "Merchant Name",
  "reference": "REF123"
}
```

### GET /liquidations/qr/reference

Get QR reference data.

### GET /liquidations/qr/validate

Validate QR code.

**Query Parameters:**
- `qrData` (string) - QR code data to validate

## QR Data Endpoints

### GET /liquidations/data/with-qr

Get liquidations with QR codes.

**Query Parameters:**
- `page` (integer, default: 0)
- `size` (integer, default: 10)

### GET /liquidations/data/without-qr

Get liquidations without QR codes.

**Query Parameters:**
- `page` (integer, default: 0)
- `size` (integer, default: 10)

### GET /liquidations/data/type/{qrType}

Get liquidations by QR type.

**Path Parameters:**
- `qrType` (string) - QR type (STATIC, DYNAMIC, P2P, PENALTY)

### GET /liquidations/data/transaction/{transactionId}

Get liquidation by transaction ID.

**Path Parameters:**
- `transactionId` (string) - Transaction ID

### GET /liquidations/data/customer/{customerId}

Get liquidations by customer.

**Path Parameters:**
- `customerId` (long) - Customer ID

### GET /liquidations/data/status/{status}

Get liquidations by status.

**Path Parameters:**
- `status` (string) - Status (PENDING, PAID, OVERDUE)

### GET /liquidations/data/today

Get today's liquidations.

### GET /liquidations/data/this-week

Get this week's liquidations.

### GET /liquidations/data/this-month

Get this month's liquidations.

### GET /liquidations/data/with-penalties

Get liquidations with penalties.

### GET /liquidations/data/stats/count-by-type

Get statistics count by QR type.

**Response:**
```json
{
  "STATIC": 10,
  "DYNAMIC": 5,
  "P2P": 3,
  "PENALTY": 2
}
```

### GET /liquidations/data/stats/total-amount

Get total amount statistics.

**Response:**
```json
{
  "totalBaseAmount": 10000.00,
  "totalPenaltyAmount": 500.00,
  "totalAmount": 10500.00
}
```

### GET /liquidations/data/stats/total-penalties

Get total penalties statistics.

**Response:**
```json
{
  "totalPenaltyAmount": 500.00,
  "penaltyCount": 5
}
```

### DELETE /liquidations/data/{liquidationId}

Delete liquidation by ID.

**Path Parameters:**
- `liquidationId` (long) - Liquidation ID

### DELETE /liquidations/data/customer/{customerId}

Delete all liquidations for a customer.

**Path Parameters:**
- `customerId` (long) - Customer ID

### PUT /liquidations/data/{liquidationId}/update-total

Update liquidation total amount.

**Path Parameters:**
- `liquidationId` (long) - Liquidation ID

**Request Body:**
```json
{
  "totalAmount": 1050.00
}
```

### PUT /liquidations/data/update-all-totals

Update all liquidation totals.

### GET /liquidations/data/{liquidationId}/validate

Validate liquidation.

**Path Parameters:**
- `liquidationId` (long) - Liquidation ID

### GET /liquidations/data/transaction-exists/{transactionId}

Check if transaction exists.

**Path Parameters:**
- `transactionId` (string) - Transaction ID

**Response:**
```json
{
  "exists": true,
  "liquidationId": 1
}
```

## UEMOA QR Endpoints

### POST /uemoa/qr/generate-static

Generate static UEMOA QR code.

**Request Body:**
```json
{
  "amount": 1000,
  "merchantName": "Merchant Name"
}
```

### POST /uemoa/qr/generate-dynamic

Generate dynamic UEMOA QR code.

**Request Body:**
```json
{
  "amount": 1000,
  "merchantName": "Merchant Name",
  "reference": "REF123"
}
```

### POST /uemoa/qr/parse

Parse UEMOA QR code.

**Request Body:**
```json
{
  "qrData": "000201..."
}
```

**Response:**
```json
{
  "success": true,
  "parsedData": {
    "merchantName": "Merchant Name",
    "amount": 1000.00,
    "currency": "XOF",
    "countryCode": "CI"
  }
}
```

### GET /uemoa/qr/test

Generate test QR code.

### GET /uemoa/qr/health

Health check for UEMOA integration.

**Response:**
```json
{
  "status": "UP",
  "timestamp": "2025-01-01T12:00:00Z"
}
```

## Error Codes

### Common HTTP Status Codes

- `200` - Success
- `201` - Created
- `204` - No Content
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `409` - Conflict
- `422` - Unprocessable Entity
- `500` - Internal Server Error

### Application-Specific Errors

- `ERR_INVALID_AMOUNT` - Invalid amount provided
- `ERR_QR_GENERATION_FAILED` - QR code generation failed
- `ERR_CUSTOMER_NOT_FOUND` - Customer not found
- `ERR_LIQUIDATION_NOT_FOUND` - Liquidation not found
- `ERR_INSUFFICIENT_PERMISSIONS` - Insufficient permissions
- `ERR_UEMOA_INTEGRATION_ERROR` - UEMOA integration error

## Rate Limiting

API endpoints are rate limited to prevent abuse:

- **Authenticated endpoints**: 1000 requests per hour
- **Authentication endpoints**: 10 requests per minute
- **QR generation endpoints**: 100 requests per hour

## Pagination

All list endpoints support pagination:

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
  "totalPages": 1,
  "totalElements": 5,
  "last": true,
  "first": true,
  "numberOfElements": 5,
  "size": 10,
  "number": 0,
  "sort": {
    "sorted": false
  },
  "empty": false
}
```

## Data Types

### Customer

```typescript
interface Customer {
  id: number;
  lastName: string;
  firstName: string;
  address?: string;
  ifu?: string;
  phone?: string;
  email: string;
  createdAt: string;
  updatedAt: string;
}
```

### Liquidation

```typescript
interface Liquidation {
  id: number;
  customer: Customer;
  amount: number;
  status: 'PENDING' | 'PAID' | 'OVERDUE';
  qrCodeData?: string;
  qrImageBase64?: string;
  merchantChannel?: string;
  transactionId?: string;
  qrType?: 'STATIC' | 'DYNAMIC' | 'P2P' | 'PENALTY';
  qrGeneratedAt?: string;
  penaltyAmount: number;
  totalAmount: number;
  createdAt: string;
  updatedAt: string;
}
```

### QR Generation Request

```typescript
interface QRGenerationRequest {
  qrType: 'STATIC' | 'DYNAMIC' | 'P2P' | 'PENALTY';
  amount: number;
  merchantName?: string;
  reference?: string;
}
```

### QR Generation Response

```typescript
interface QRGenerationResponse {
  qrCodeData: string;
  qrImageBase64: string;
  transactionId: string;
  qrType: string;
  generatedAt: string;
}
```

## SDKs and Libraries

### JavaScript Client

```javascript
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add JWT token interceptor
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('liquidation_jwt_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default apiClient;
```

### cURL Examples

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

#### Get Customers
```bash
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Create Customer
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "lastName": "Doe",
    "firstName": "John",
    "email": "john.doe@example.com"
  }'
```

## Webhooks

The API supports webhooks for real-time notifications (planned feature):

- **Payment Completed**: Triggered when a liquidation is marked as paid
- **QR Code Generated**: Triggered when a new QR code is generated
- **Customer Created**: Triggered when a new customer is created

## Versioning

API versioning is handled through URL paths:

- **Current Version**: v1 (default)
- **Future Versions**: `/api/v2/`

## Changelog

### Version 1.0.0
- Initial API release
- Basic CRUD operations for customers and liquidations
- JWT authentication
- QR code generation
- UEMOA integration

### Planned Features
- API rate limiting enhancements
- Advanced filtering and search
- Bulk operations
- Export functionality
- Real-time notifications
- API documentation with Swagger