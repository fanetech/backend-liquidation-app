# Setup and Installation Guide

## Prerequisites

Before setting up the Liquidation App, ensure you have the following software installed:

### System Requirements

- **Operating System**: Linux, macOS, or Windows
- **Java**: JDK 17 or higher
- **Node.js**: Version 18 or higher
- **npm**: Latest version (comes with Node.js)
- **PostgreSQL**: Version 12 or higher
- **Maven**: Version 3.6+ (usually comes with Java)
- **Git**: For version control

### Hardware Requirements

- **RAM**: Minimum 4GB, recommended 8GB+
- **Disk Space**: 2GB free space
- **CPU**: 2+ cores recommended

## Backend Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd liquidation-app/backend-liquidation-app
```

### 2. Database Setup

#### Install PostgreSQL

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
```

**macOS (with Homebrew):**
```bash
brew install postgresql
brew services start postgresql
```

**Windows:**
Download and install from [postgresql.org](https://www.postgresql.org/download/)

#### Create Database

```bash
# Connect to PostgreSQL
sudo -u postgres psql

# Create database and user
CREATE DATABASE qr_demo_db;
CREATE USER qr_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE qr_demo_db TO qr_user;
\q
```

### 3. Environment Configuration

Create a `.env` file in the backend root directory:

```bash
# JWT Secret (generate a secure 256-bit key)
JWT_SECRET=myVerySecureSecretKey1234567890123456789012345678901234567890ABCD

# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=qr_demo_db
DB_USERNAME=qr_user
DB_PASSWORD=your_password
```

### 4. Maven Build

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the application
mvn package
```

### 5. Application Configuration

Update `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/qr_demo_db
spring.datasource.username=qr_user
spring.datasource.password=your_password

# JWT
jwt.secret=${JWT_SECRET:myVerySecureSecretKey1234567890123456789012345678901234567890ABCD}
jwt.expiration=3600000

# Server
server.port=8080

# UEMOA Configuration
uemoa.qr.country-code=CI
uemoa.qr.currency=XOF
uemoa.qr.merchant-name=LIQUIDATION APP
```

### 6. Run the Backend

```bash
# Development mode
mvn spring-boot:run

# Production mode
java -jar target/demoQrcode-0.0.1-SNAPSHOT.jar
```

The backend will be available at `http://localhost:8080`

## Frontend Setup

### 1. Navigate to Frontend Directory

```bash
cd ../frontend-liquidation-app
```

### 2. Install Dependencies

```bash
npm install
```

### 3. Environment Configuration

Create a `.env.local` file:

```bash
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_NAME=Liquidation App
```

### 4. Development Server

```bash
# Start development server
npm run dev
```

The frontend will be available at `http://localhost:5173`

### 5. Production Build

```bash
# Build for production
npm run build

# Preview production build
npm run preview
```

## Docker Setup (Alternative)

### Using Docker Compose

Create a `docker-compose.yml` file in the project root:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: qr_demo_db
      POSTGRES_USER: qr_user
      POSTGRES_PASSWORD: your_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  backend:
    build: ./backend-liquidation-app
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/qr_demo_db
      SPRING_DATASOURCE_USERNAME: qr_user
      SPRING_DATASOURCE_PASSWORD: your_password
      JWT_SECRET: myVerySecureSecretKey1234567890123456789012345678901234567890ABCD
    depends_on:
      - postgres

  frontend:
    build: ./frontend-liquidation-app
    ports:
      - "5173:5173"
    depends_on:
      - backend

volumes:
  postgres_data:
```

### Docker Build and Run

```bash
# Build and start all services
docker-compose up --build

# Run in background
docker-compose up -d

# Stop services
docker-compose down
```

## Manual Installation Steps

### Step-by-Step Backend Installation

1. **Install Java 17**
   ```bash
   # Ubuntu
   sudo apt install openjdk-17-jdk

   # Verify installation
   java -version
   ```

2. **Install Maven**
   ```bash
   # Ubuntu
   sudo apt install maven

   # Verify installation
   mvn -version
   ```

3. **Install PostgreSQL**
   ```bash
   sudo apt install postgresql postgresql-contrib
   sudo systemctl start postgresql
   sudo systemctl enable postgresql
   ```

4. **Setup Database**
   ```bash
   sudo -u postgres createuser --interactive --pwprompt qr_user
   sudo -u postgres createdb qr_demo_db
   sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE qr_demo_db TO qr_user;"
   ```

5. **Configure Application**
   - Update `application.properties` with database credentials
   - Set JWT secret in environment variables

6. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

### Step-by-Step Frontend Installation

1. **Install Node.js**
   ```bash
   # Using Node Version Manager (recommended)
   curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
   source ~/.bashrc
   nvm install 18
   nvm use 18
   ```

2. **Install Dependencies**
   ```bash
   npm install
   ```

3. **Configure Environment**
   - Create `.env.local` with API base URL

4. **Start Development Server**
   ```bash
   npm run dev
   ```

## Configuration Options

### Backend Configuration

#### Database Configuration

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/qr_demo_db
spring.datasource.username=qr_user
spring.datasource.password=your_password

# H2 (for testing)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
```

#### JWT Configuration

```properties
jwt.secret=myVerySecureSecretKey1234567890123456789012345678901234567890ABCD
jwt.expiration=3600000  # 1 hour in milliseconds
```

#### Server Configuration

```properties
server.port=8080
server.servlet.context-path=/api
```

#### UEMOA Configuration

```properties
uemoa.qr.country-code=CI
uemoa.qr.currency=XOF
uemoa.qr.merchant-name=LIQUIDATION APP
uemoa.qr.default-amount=100
uemoa.qr.min-amount=1
uemoa.qr.max-amount=999999
```

### Frontend Configuration

#### Environment Variables

```bash
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_NAME=Liquidation App
VITE_DEBUG=true
```

#### Vite Configuration

```javascript
// vite.config.js
export default defineConfig({
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

## Troubleshooting

### Common Backend Issues

#### Database Connection Issues

**Error:** `Connection refused`
**Solution:**
- Ensure PostgreSQL is running: `sudo systemctl status postgresql`
- Check database credentials in `application.properties`
- Verify database exists: `psql -U qr_user -d qr_demo_db`

#### Port Already in Use

**Error:** `Port 8080 is already in use`
**Solution:**
- Change port in `application.properties`: `server.port=8081`
- Find process using port: `lsof -i :8080`
- Kill process: `kill -9 <PID>`

#### JWT Secret Issues

**Error:** `JWT signature does not match`
**Solution:**
- Ensure JWT secret is consistent across restarts
- Use a secure random key (256 bits minimum)
- Check environment variable: `echo $JWT_SECRET`

### Common Frontend Issues

#### API Connection Issues

**Error:** `Failed to fetch`
**Solution:**
- Verify backend is running on correct port
- Check CORS configuration
- Ensure API base URL is correct in `.env.local`

#### Build Issues

**Error:** `Module not found`
**Solution:**
- Clear node_modules: `rm -rf node_modules`
- Reinstall dependencies: `npm install`
- Check Node.js version: `node --version`

#### Port Issues

**Error:** `Port 5173 is already in use`
**Solution:**
- Change port in `vite.config.js`
- Kill process using port: `lsof -ti:5173 | xargs kill`

### Database Issues

#### Migration Issues

**Error:** `Migration checksum mismatch`
**Solution:**
- Clean database and rerun migrations
- Check migration files for changes
- Use `mvn flyway:clean flyway:migrate`

#### Permission Issues

**Error:** `Permission denied for table`
**Solution:**
- Grant proper permissions to database user
- Check user roles in PostgreSQL
- Verify database ownership

## Development Workflow

### Backend Development

1. **Code Changes**
   ```bash
   # Make changes to Java files
   mvn compile
   ```

2. **Run Tests**
   ```bash
   mvn test
   ```

3. **Hot Reload**
   ```bash
   mvn spring-boot:run
   ```

### Frontend Development

1. **Code Changes**
   ```bash
   # Vite provides automatic hot reload
   npm run dev
   ```

2. **Linting**
   ```bash
   npm run lint
   ```

3. **Build**
   ```bash
   npm run build
   ```

## Production Deployment

### Backend Deployment

1. **Build JAR**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Create Systemd Service**
   ```bash
   sudo nano /etc/systemd/system/liquidation-app.service
   ```

   ```ini
   [Unit]
   Description=Liquidation App Backend
   After=network.target

   [Service]
   Type=simple
   User=appuser
   WorkingDirectory=/opt/liquidation-app
   ExecStart=/usr/bin/java -jar backend-liquidation-app/target/demoQrcode-0.0.1-SNAPSHOT.jar
   Restart=always

   [Install]
   WantedBy=multi-user.target
   ```

3. **Start Service**
   ```bash
   sudo systemctl daemon-reload
   sudo systemctl start liquidation-app
   sudo systemctl enable liquidation-app
   ```

### Frontend Deployment

1. **Build Production Bundle**
   ```bash
   npm run build
   ```

2. **Serve with Nginx**
   ```nginx
   server {
       listen 80;
       server_name your-domain.com;
       root /opt/liquidation-app/frontend/dist;
       index index.html;

       location / {
           try_files $uri $uri/ /index.html;
       }

       location /api {
           proxy_pass http://localhost:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ```

## Monitoring and Logging

### Backend Logging

```properties
logging.level.root=INFO
logging.level.com.example.demoQrcode=DEBUG
logging.file.name=logs/demoqrcode.log
logging.pattern.console=%d{HH:mm:ss} %-5level %logger{36} - %msg%n
```

### Application Health

Access health endpoint: `http://localhost:8080/actuator/health`

### Database Monitoring

```sql
-- Check active connections
SELECT * FROM pg_stat_activity;

-- Check table sizes
SELECT schemaname, tablename, pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

## Backup and Recovery

### Database Backup

```bash
# Create backup
pg_dump -U qr_user -h localhost qr_demo_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore backup
psql -U qr_user -h localhost qr_demo_db < backup_file.sql
```

### Application Backup

```bash
# Backup application files
tar -czf backup_$(date +%Y%m%d_%H%M%S).tar.gz /opt/liquidation-app/
```

## Security Considerations

### Environment Variables

- Store sensitive data in environment variables
- Use strong, unique passwords
- Rotate JWT secrets regularly

### Network Security

- Use HTTPS in production
- Configure firewall rules
- Limit database access to application server

### Application Security

- Keep dependencies updated
- Use parameterized queries
- Implement proper input validation
- Enable CSRF protection

## Support and Resources

### Documentation

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://reactjs.org/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Vite Documentation](https://vitejs.dev/)

### Community Resources

- [Stack Overflow](https://stackoverflow.com/questions/tagged/spring-boot)
- [React Community](https://reactjs.org/community/support.html)
- [PostgreSQL Community](https://www.postgresql.org/community/)

### Getting Help

1. Check application logs
2. Verify configuration files
3. Test database connectivity
4. Check firewall settings
5. Consult documentation
6. Search community forums

## Quick Start Checklist

- [ ] Install Java 17
- [ ] Install Node.js 18+
- [ ] Install PostgreSQL
- [ ] Create database and user
- [ ] Clone repository
- [ ] Configure backend environment
- [ ] Build and run backend
- [ ] Configure frontend environment
- [ ] Install frontend dependencies
- [ ] Start frontend development server
- [ ] Test application functionality

## Next Steps

After successful setup:

1. **Explore the Application**
   - Access frontend at `http://localhost:5173`
   - Login with default credentials
   - Create sample customers and liquidations

2. **Test QR Code Generation**
   - Generate QR codes for liquidations
   - Test different QR types (static, dynamic, P2P)

3. **Customize Configuration**
   - Update UEMOA settings
   - Configure email notifications
   - Setup production deployment

4. **Development**
   - Review codebase structure
   - Understand API endpoints
   - Plan feature enhancements

The application is now ready for development and testing!