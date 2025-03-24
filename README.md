# KMC (Key Management Console)

## Overview
KMC is a Spring-based web application that provides a comprehensive key management console. The application is built using Spring MVC architecture and follows a layered design pattern.

## Technology Stack
- Spring Framework 5.2.22
- Hibernate 5.4.25
- Log4j 2.17.1
- Jackson 2.13.2
- ESAPI 2.3.0.0
- Maven

## Project Structure
```
kmc/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── key/
│       │           └── kmc/
│       │               ├── controller/    # Web controllers
│       │               ├── service/       # Business services
│       │               ├── dao/          # Data access objects
│       │               ├── data/         # Data models
│       │               ├── business/     # Business logic
│       │               ├── security/     # Security components
│       │               ├── config/       # Configuration
│       │               ├── util/         # Utility classes
│       │               └── soa/          # Service-oriented architecture components
│       └── webapp/      # Web resources
```

## Application Architecture Overview
```mermaid
graph TD
    A[Client Browser] -->|HTTP Request| B[Controller Layer]
    B -->|Request Processing| C[Service Layer]
    C -->|Business Logic| D[Business Layer]
    D -->|Data Access| E[DAO Layer]
    E -->|Database Operations| F[Database]
    
    subgraph "Security Layer"
        G[Authentication]
        H[Authorization]
        I[ESAPI Security]
    end
    
    B -->|Security Check| G
    G -->|Validate| H
    H -->|Secure| I
    
    subgraph "Configuration Layer"
        J[Spring Config]
        K[App Config]
        L[Cache Management]
    end
    
    B -->|Config| J
    C -->|Config| K
    D -->|Cache| L
```

## Detailed Application Flows

### 1. Authentication Flow
```mermaid
sequenceDiagram
    participant Client
    participant Controller
    participant AuthService
    participant SecurityManager
    participant Cache

    Client->>Controller: POST /api/auth/login
    Controller->>AuthService: authenticateUser(credentials)
    AuthService->>SecurityManager: validateCredentials(credentials)
    SecurityManager->>Cache: checkUserSession(userId)
    alt Session Exists
        Cache-->>SecurityManager: return existing session
    else No Session
        SecurityManager->>SecurityManager: createNewSession(userId)
        SecurityManager->>Cache: storeSession(session)
    end
    SecurityManager-->>AuthService: return session token
    AuthService-->>Controller: return auth response
    Controller-->>Client: JWT Token + User Info
```

### 2. Key Management Flow
```mermaid
sequenceDiagram
    participant Client
    participant KeyController
    participant KeyService
    participant KeyBusiness
    participant KeyDAO
    participant DB

    Client->>KeyController: POST /api/keys
    KeyController->>KeyService: createKey(keyRequest)
    KeyService->>KeyBusiness: validateAndProcessKey(keyRequest)
    KeyBusiness->>KeyDAO: saveKey(keyData)
    KeyDAO->>DB: insert key record
    DB-->>KeyDAO: confirm insertion
    KeyDAO-->>KeyBusiness: return saved key
    KeyBusiness-->>KeyService: return processed key
    KeyService-->>KeyController: return key response
    KeyController-->>Client: return key details
```

### 3. Configuration Management Flow
```mermaid
sequenceDiagram
    participant Admin
    participant ConfigController
    participant ConfigService
    participant CacheManager
    participant DB

    Admin->>ConfigController: PUT /api/config/{id}
    ConfigController->>ConfigService: updateConfig(configData)
    ConfigService->>CacheManager: invalidateCache(configId)
    ConfigService->>DB: update config
    DB-->>ConfigService: confirm update
    ConfigService->>CacheManager: refreshCache(configId)
    ConfigService-->>ConfigController: return updated config
    ConfigController-->>Admin: return success response
```

### 4. Key Rotation Flow
```mermaid
sequenceDiagram
    participant Client
    participant KeyController
    participant KeyService
    participant KeyBusiness
    participant KeyDAO
    participant DB
    participant Cache

    Client->>KeyController: POST /api/keys/{id}/rotate
    KeyController->>KeyService: rotateKey(keyId)
    KeyService->>KeyBusiness: validateKeyRotation(keyId)
    KeyBusiness->>KeyDAO: getCurrentKey(keyId)
    KeyDAO->>DB: fetch key data
    DB-->>KeyDAO: return key
    KeyDAO-->>KeyBusiness: return key data
    KeyBusiness->>KeyBusiness: generateNewKeyVersion()
    KeyBusiness->>KeyDAO: saveNewKeyVersion(newKey)
    KeyDAO->>DB: update key record
    DB-->>KeyDAO: confirm update
    KeyDAO-->>KeyBusiness: return updated key
    KeyBusiness->>Cache: invalidateKeyCache(keyId)
    KeyBusiness-->>KeyService: return rotated key
    KeyService-->>KeyController: return key response
    KeyController-->>Client: return new key details
```

### 5. Key Revocation Flow
```mermaid
sequenceDiagram
    participant Client
    participant KeyController
    participant KeyService
    participant KeyBusiness
    participant KeyDAO
    participant DB
    participant AuditLogger

    Client->>KeyController: POST /api/keys/{id}/revoke
    KeyController->>KeyService: revokeKey(keyId)
    KeyService->>KeyBusiness: validateKeyRevocation(keyId)
    KeyBusiness->>KeyDAO: getKeyStatus(keyId)
    KeyDAO->>DB: fetch key status
    DB-->>KeyDAO: return status
    KeyDAO-->>KeyBusiness: return key status
    KeyBusiness->>KeyBusiness: markKeyAsRevoked()
    KeyBusiness->>KeyDAO: updateKeyStatus(REVOKED)
    KeyDAO->>DB: update status
    DB-->>KeyDAO: confirm update
    KeyBusiness->>AuditLogger: logRevocation(keyId, reason)
    KeyBusiness-->>KeyService: return revocation status
    KeyService-->>KeyController: return revocation response
    KeyController-->>Client: return revocation confirmation
```

### 6. Role Management Flow
```mermaid
sequenceDiagram
    participant Admin
    participant SecurityController
    participant SecurityService
    participant RoleManager
    participant UserDAO
    participant DB
    participant Cache

    Admin->>SecurityController: POST /api/security/roles
    SecurityController->>SecurityService: assignRole(userId, role)
    SecurityService->>RoleManager: validateRoleAssignment(userId, role)
    RoleManager->>UserDAO: getUserRoles(userId)
    UserDAO->>DB: fetch user roles
    DB-->>UserDAO: return roles
    UserDAO-->>RoleManager: return current roles
    RoleManager->>RoleManager: validateRoleHierarchy()
    RoleManager->>UserDAO: updateUserRoles(userId, newRoles)
    UserDAO->>DB: update roles
    DB-->>UserDAO: confirm update
    RoleManager->>Cache: invalidateUserPermissions(userId)
    RoleManager-->>SecurityService: return updated roles
    SecurityService-->>SecurityController: return role assignment response
    SecurityController-->>Admin: return success response
```

### 7. Audit Logging Flow
```mermaid
sequenceDiagram
    participant Client
    participant SecurityController
    participant SecurityService
    participant AuditService
    participant AuditDAO
    participant DB
    participant Cache

    Client->>SecurityController: GET /api/security/audit-logs
    SecurityController->>SecurityService: getAuditLogs(filters)
    SecurityService->>AuditService: retrieveLogs(filters)
    AuditService->>Cache: checkCachedLogs(filters)
    alt Logs in Cache
        Cache-->>AuditService: return cached logs
    else Logs not in Cache
        AuditService->>AuditDAO: fetchLogs(filters)
        AuditDAO->>DB: query audit logs
        DB-->>AuditDAO: return logs
        AuditDAO-->>AuditService: return log data
        AuditService->>Cache: cacheLogs(logs)
    end
    AuditService-->>SecurityService: return filtered logs
    SecurityService-->>SecurityController: return audit response
    SecurityController-->>Client: return audit logs
```

## API Endpoints

### Authentication Endpoints
- `POST /api/auth/login` - User authentication
- `POST /api/auth/logout` - User logout
- `GET /api/auth/validate` - Validate session token
- `POST /api/auth/refresh` - Refresh authentication token

### Key Management Endpoints
- `GET /api/keys` - List all keys
- `POST /api/keys` - Create new key
- `GET /api/keys/{id}` - Get key details
- `PUT /api/keys/{id}` - Update key
- `DELETE /api/keys/{id}` - Delete key
- `POST /api/keys/{id}/rotate` - Rotate key
- `POST /api/keys/{id}/revoke` - Revoke key

### Configuration Endpoints
- `GET /api/config` - Get all configurations
- `GET /api/config/{id}` - Get specific configuration
- `POST /api/config` - Create new configuration
- `PUT /api/config/{id}` - Update configuration
- `DELETE /api/config/{id}` - Delete configuration

### Security Endpoints
- `GET /api/security/audit-logs` - Get security audit logs
- `POST /api/security/validate-input` - Validate input data
- `GET /api/security/roles` - Get user roles
- `POST /api/security/roles` - Assign roles

## Key Components

### 1. Controller Layer
- Handles HTTP requests
- Manages request/response flow
- Implements REST endpoints
- Validates input data
- Routes requests to appropriate services
- Handles response formatting

### 2. Service Layer
- Implements business operations
- Manages transactions
- Coordinates between different components
- Handles complex business logic
- Implements caching strategies
- Manages service-level security

### 3. Business Layer
- Contains core business rules
- Implements domain logic
- Manages business validations
- Coordinates with services
- Handles business-level security
- Implements business workflows

### 4. DAO Layer
- Handles database operations
- Implements data access patterns
- Manages entity relationships
- Provides data persistence
- Implements query optimization
- Handles database transactions

### 5. Security Layer
- Implements authentication
- Manages authorization
- Provides ESAPI security features
- Handles security validations
- Manages session security
- Implements audit logging

### 6. Configuration Layer
- Manages Spring configuration
- Handles application properties
- Implements caching mechanisms
- Manages system settings
- Handles environment-specific configs
- Manages feature flags

## Getting Started

### Prerequisites
- Java JDK 8 or higher
- Maven 3.6 or higher
- Web Application Server (e.g., Tomcat, WebSphere)

### Building the Project
```bash
mvn clean install
```

### Running the Application
1. Deploy the generated WAR file to your web application server
2. Configure the application properties in `kmc-properties`
3. Start the application server

## Security Features
- ESAPI security implementation
- Authentication and authorization
- Input validation
- XSS protection
- CSRF protection
- Secure session management
- Role-based access control
- Audit logging
- Secure password handling
- API security

## Caching
The application implements caching mechanisms for:
- Application configuration
- User sessions
- Frequently accessed data
- System settings
- Key metadata
- User permissions
- Role mappings
- Audit logs

## Logging
- Comprehensive logging using Log4j2
- Different log levels for different environments
- Audit logging for security events
- Performance monitoring logs
- Error tracking
- Access logs
- Security audit trails
- System health monitoring

## Contributing
Please read CONTRIBUTING.md for details on our code of conduct and the process for submitting pull requests.

## License
This project is licensed under the [License Name] - see the LICENSE.md file for details 
