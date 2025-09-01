# Testing Documentation

## Overview

The Liquidation App implements a comprehensive testing strategy covering unit tests, integration tests, and end-to-end tests. The testing framework is built around JUnit 5 for backend testing and includes both automated and manual testing approaches.

## Testing Strategy

### Test Pyramid

```
End-to-End Tests (E2E)
    ↕️
Integration Tests
    ↕️
Unit Tests
```

### Testing Types

1. **Unit Tests**: Test individual components in isolation
2. **Integration Tests**: Test component interactions
3. **End-to-End Tests**: Test complete user workflows
4. **Manual Tests**: Exploratory and user acceptance testing

## Backend Testing

### Test Framework

- **JUnit 5**: Primary testing framework
- **Spring Boot Test**: Spring-specific testing support
- **Mockito**: Mocking framework for dependencies
- **AssertJ**: Fluent assertions
- **H2 Database**: In-memory database for tests

### Test Structure

```
src/test/java/com/example/demoQrcode/
├── DemoQrcodeApplicationTests.java          # Application context tests
├── LiquidationControllerQRTest.java         # Controller tests
├── LiquidationEntityQRSimpleTest.java       # Entity tests
├── LiquidationEntityQRTest.java             # Entity tests
├── LiquidationQRServiceTest.java            # Service tests
├── UemoaQRIntegrationTest.java              # Integration tests
└── UemoaQRModuleTest.java                   # Module tests
```

### Running Tests

#### Run All Tests

```bash
mvn test
```

#### Run Specific Test Class

```bash
mvn test -Dtest=LiquidationQRServiceTest
```

#### Run Tests with Coverage

```bash
mvn test jacoco:report
```

#### Run Tests in IDE

Most IDEs (IntelliJ IDEA, Eclipse, VS Code) support running JUnit tests directly from the editor.

### Test Categories

#### Unit Tests

**DemoQrcodeApplicationTests.java**
- Tests Spring Boot application context loading
- Verifies bean creation and dependency injection

```java
@SpringBootTest
class DemoQrcodeApplicationTests {

    @Test
    void contextLoads() {
        // Test that application context loads successfully
    }
}
```

**LiquidationEntityQRTest.java**
- Tests JPA entity mappings
- Validates entity relationships
- Tests entity business logic

```java
@EntityTest
class LiquidationEntityQRTest {

    @Test
    void testLiquidationCreation() {
        // Test entity creation and validation
    }

    @Test
    void testQRCodeFields() {
        // Test QR code related fields
    }
}
```

#### Service Tests

**LiquidationQRServiceTest.java**
- Tests QR code generation service
- Mocks external dependencies
- Validates service business logic

```java
@ExtendWith(MockitoExtension.class)
class LiquidationQRServiceTest {

    @Mock
    private LiquidationRepository liquidationRepository;

    @InjectMocks
    private LiquidationQRService liquidationQRService;

    @Test
    void testGenerateQRCode() {
        // Test QR code generation
    }
}
```

#### Integration Tests

**UemoaQRIntegrationTest.java**
- Tests UEMOA QR module integration
- Tests real database interactions
- Validates end-to-end QR generation

```java
@SpringBootTest
@ActiveProfiles("test")
class UemoaQRIntegrationTest {

    @Autowired
    private UemoaQRIntegrationService uemoaService;

    @Test
    void testStaticQRGeneration() {
        // Test complete QR generation workflow
    }
}
```

#### Controller Tests

**LiquidationControllerQRTest.java**
- Tests REST API endpoints
- Uses MockMvc for HTTP request simulation
- Validates request/response handling

```java
@WebMvcTest(LiquidationController.class)
class LiquidationControllerQRTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LiquidationService liquidationService;

    @Test
    void testGenerateQRCode() throws Exception {
        mockMvc.perform(post("/api/liquidations/{id}/generate-qr", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"qrType\":\"STATIC\",\"amount\":1000}"))
                .andExpect(status().isOk());
    }
}
```

### Test Configuration

#### Test Properties

```properties
# Test-specific configuration
spring.profiles.active=test
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
logging.level.org.hibernate.SQL=ERROR
```

#### Test Annotations

```java
@SpringBootTest                    // Full application context
@WebMvcTest                        // Web layer only
@DataJpaTest                       // JPA layer only
@ExtendWith(MockitoExtension.class) // Mockito support
@ActiveProfiles("test")           // Test profile
```

### Mocking Strategies

#### Service Layer Mocking

```java
@Mock
private UemoaQRService uemoaQRService;

@InjectMocks
private LiquidationQRService liquidationQRService;

@BeforeEach
void setUp() {
    Mockito.when(uemoaQRService.generateStaticQR(any()))
           .thenReturn("mocked-qr-data");
}
```

#### Repository Mocking

```java
@Mock
private LiquidationRepository liquidationRepository;

when(liquidationRepository.findById(1L))
    .thenReturn(Optional.of(testLiquidation));
```

### Test Data Management

#### Test Data Builders

```java
public class TestDataBuilder {
    public static Customer createTestCustomer() {
        return Customer.builder()
                .lastName("Doe")
                .firstName("John")
                .email("john.doe@test.com")
                .build();
    }

    public static Liquidation createTestLiquidation() {
        return Liquidation.builder()
                .customer(createTestCustomer())
                .amount(new BigDecimal("1000.00"))
                .status(LiquidationStatus.PENDING)
                .build();
    }
}
```

#### Database Cleanup

```java
@AfterEach
void tearDown() {
    liquidationRepository.deleteAll();
    customerRepository.deleteAll();
}
```

## Frontend Testing

### Testing Framework (Planned)

- **Jest**: JavaScript testing framework
- **React Testing Library**: React component testing
- **Vitest**: Vite-native testing framework

### Test Structure (Planned)

```
src/
├── components/
│   ├── __tests__/
│   │   ├── Login.test.jsx
│   │   ├── CustomerPage.test.jsx
│   │   └── QRCodeDisplay.test.jsx
├── services/
│   ├── __tests__/
│   │   ├── api.test.js
│   │   └── customerService.test.js
└── utils/
    ├── __tests__/
    │   └── validation.test.js
```

### Component Testing Example

```javascript
import { render, screen, fireEvent } from '@testing-library/react';
import Login from '../Login';

test('renders login form', () => {
  render(<Login />);
  expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
  expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
});

test('handles login submission', async () => {
  render(<Login />);
  fireEvent.change(screen.getByLabelText(/username/i), {
    target: { value: 'testuser' }
  });
  fireEvent.click(screen.getByRole('button', { name: /login/i }));
  // Assert expected behavior
});
```

### API Testing Example

```javascript
import { customerService } from '../customerService';
import axios from 'axios';

jest.mock('axios');
const mockedAxios = axios;

test('fetches customers successfully', async () => {
  const mockData = [{ id: 1, name: 'John Doe' }];
  mockedAxios.get.mockResolvedValue({ data: mockData });

  const result = await customerService.list();
  expect(result).toEqual(mockData);
});
```

## End-to-End Testing

### Testing Framework (Planned)

- **Playwright**: Modern E2E testing framework
- **Cypress**: Alternative E2E testing framework

### E2E Test Scenarios

1. **User Registration and Login**
2. **Customer Management Workflow**
3. **Liquidation Creation and QR Generation**
4. **Payment Processing Simulation**
5. **Admin Dashboard Functionality**

### E2E Test Example

```javascript
test('complete liquidation workflow', async ({ page }) => {
  // Login
  await page.goto('http://localhost:5173/login');
  await page.fill('[data-testid="username"]', 'admin');
  await page.fill('[data-testid="password"]', 'password');
  await page.click('[data-testid="login-button"]');

  // Create customer
  await page.click('[data-testid="customers-menu"]');
  await page.click('[data-testid="add-customer"]');
  await page.fill('[data-testid="lastName"]', 'Doe');
  await page.fill('[data-testid="firstName"]', 'John');
  await page.click('[data-testid="save-customer"]');

  // Create liquidation
  await page.click('[data-testid="liquidations-menu"]');
  await page.click('[data-testid="add-liquidation"]');
  // ... continue workflow
});
```

## Test Automation

### CI/CD Integration

#### GitHub Actions Example

```yaml
name: CI
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run tests
        run: mvn test
```

#### Jenkins Pipeline Example

```groovy
pipeline {
    agent any
    stages {
        stage('Test') {
            steps {
                sh 'mvn test'
                sh 'npm test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    publishCoverage adapters: [jacocoAdapter('**/target/site/jacoco/jacoco.xml')]
                }
            }
        }
    }
}
```

### Code Coverage

#### JaCoCo Configuration

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### Coverage Goals

- **Unit Tests**: 80%+ coverage
- **Integration Tests**: 70%+ coverage
- **Critical Paths**: 90%+ coverage

### Test Reporting

#### Surefire Reports

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0-M9</version>
    <configuration>
        <reportsDirectory>${project.build.directory}/surefire-reports</reportsDirectory>
    </configuration>
</plugin>
```

## Manual Testing

### Test Cases

#### Authentication Testing

1. **Valid Login**
   - Enter correct username/password
   - Verify successful login and redirect

2. **Invalid Login**
   - Enter incorrect credentials
   - Verify error message display

3. **Session Management**
   - Login and navigate
   - Refresh page
   - Verify session persistence

#### Customer Management Testing

1. **Create Customer**
   - Fill all required fields
   - Submit form
   - Verify customer creation

2. **Search Customers**
   - Enter search term
   - Verify filtered results

3. **Update Customer**
   - Edit customer details
   - Save changes
   - Verify updates

#### QR Code Testing

1. **Generate QR Code**
   - Create liquidation
   - Generate QR code
   - Verify QR display

2. **QR Code Validation**
   - Scan generated QR
   - Verify payment data

3. **Different QR Types**
   - Test static, dynamic, P2P QR codes
   - Verify correct generation

### Exploratory Testing

- **UI/UX Testing**: Visual design, responsiveness
- **Performance Testing**: Load times, memory usage
- **Security Testing**: Input validation, authentication
- **Browser Compatibility**: Chrome, Firefox, Safari, Edge

## Performance Testing

### Load Testing

#### JMeter Configuration

```xml
<!-- Test Plan for API Load Testing -->
<jmeterTestPlan>
    <hashTree>
        <ThreadGroup>
            <stringProp name="ThreadGroup.num_threads">100</stringProp>
            <stringProp name="ThreadGroup.ramp_time">60</stringProp>
            <HTTPSamplerProxy>
                <stringProp name="HTTPSampler.domain">localhost</stringProp>
                <stringProp name="HTTPSampler.port">8080</stringProp>
                <stringProp name="HTTPSampler.path">/api/customers</stringProp>
            </HTTPSamplerProxy>
        </ThreadGroup>
    </hashTree>
</jmeterTestPlan>
```

### Performance Benchmarks

- **API Response Time**: < 200ms for simple queries
- **QR Generation Time**: < 500ms
- **Page Load Time**: < 2 seconds
- **Concurrent Users**: Support 100+ simultaneous users

## Security Testing

### Authentication Testing

- **JWT Token Validation**
- **Session Timeout**
- **Password Policies**
- **Brute Force Protection**

### Authorization Testing

- **Role-Based Access Control**
- **Admin-Only Features**
- **Data Isolation**

### Input Validation Testing

- **SQL Injection Prevention**
- **XSS Prevention**
- **Input Sanitization**
- **File Upload Security**

## Test Data Management

### Test Data Strategy

1. **Static Test Data**: Predefined data for consistent testing
2. **Dynamic Test Data**: Generated data for variable scenarios
3. **Clean Up**: Automatic cleanup after test execution

### Test Database

```sql
-- Test data setup
INSERT INTO customers (last_name, first_name, email) VALUES
('Test', 'User1', 'test1@example.com'),
('Test', 'User2', 'test2@example.com');

INSERT INTO users (username, password, email) VALUES
('admin', '$2a$10$...', 'admin@example.com'),
('user', '$2a$10$...', 'user@example.com');
```

## Continuous Testing

### Test Automation Pipeline

1. **Unit Tests**: Run on every commit
2. **Integration Tests**: Run on pull requests
3. **E2E Tests**: Run nightly
4. **Performance Tests**: Run weekly

### Quality Gates

- **Code Coverage**: Minimum 80%
- **Test Success Rate**: 100%
- **Performance Benchmarks**: Meet SLAs
- **Security Scan**: Pass vulnerability checks

## Debugging Tests

### Common Test Issues

#### Test Failures

```bash
# Run failed tests only
mvn test -Dtest.failure.ignore=false

# Debug test execution
mvn test -DforkCount=1 -DreuseForks=false
```

#### Database Issues

```java
@Test
@Transactional
void testWithTransaction() {
    // Test code that modifies database
    // Transaction will rollback automatically
}
```

#### Mock Issues

```java
@Mock
private Dependency dependency;

@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
    when(dependency.method()).thenReturn(expectedValue);
}
```

## Best Practices

### Test Naming Conventions

```java
// Good
@Test
void shouldReturnCustomerWhenValidIdProvided()

// Bad
@Test
void test1()
```

### Test Organization

```java
@DisplayName("Customer Service")
class CustomerServiceTest {

    @Nested
    @DisplayName("Create Customer")
    class CreateCustomerTests {

        @Test
        @DisplayName("should create customer with valid data")
        void shouldCreateCustomerWithValidData() {
            // Test implementation
        }
    }
}
```

### Test Data Isolation

```java
@BeforeEach
void setUp() {
    // Clean up before each test
    testEntityManager.clear();

    // Create fresh test data
    customer = createTestCustomer();
    testEntityManager.persist(customer);
}
```

## Future Testing Enhancements

### Planned Improvements

1. **Frontend Testing Suite**
   - Complete Jest/React Testing Library setup
   - Component testing for all UI components
   - Integration tests for user workflows

2. **E2E Testing Framework**
   - Playwright or Cypress implementation
   - Cross-browser testing
   - Mobile device testing

3. **Performance Testing**
   - Automated load testing
   - Memory leak detection
   - Database performance monitoring

4. **Security Testing**
   - Automated vulnerability scanning
   - Penetration testing
   - Compliance testing

5. **Visual Regression Testing**
   - Screenshot comparison
   - UI consistency validation
   - Cross-browser visual testing

### Test Metrics and Reporting

- **Test Execution Time**: Track and optimize
- **Flaky Test Detection**: Identify unreliable tests
- **Test Coverage Trends**: Monitor coverage over time
- **Defect Leakage**: Track bugs found in production

## Resources

### Testing Tools

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Jest Documentation](https://jestjs.io/docs/getting-started)
- [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/)

### Best Practices

- [Testing Pyramid](https://martinfowler.com/bliki/TestPyramid.html)
- [Unit Testing Best Practices](https://docs.microsoft.com/en-us/dotnet/core/testing/unit-testing-best-practices)
- [Integration Testing Guide](https://martinfowler.com/articles/integration-testing.html)

This comprehensive testing documentation ensures the Liquidation App maintains high quality and reliability through systematic testing practices.