# Common Security Module

A comprehensive security framework for the Blugrid API platform, providing multi-tenant JWT authentication, session management, and access control for Micronaut-based microservices.

## Overview

The `common-api-security` module serves as the foundational security layer for the Blugrid platform, implementing a sophisticated multi-tenant authentication system with support for different authentication contexts (Guest, Tenant, and Business Unit levels).

## Purpose in the Common API Framework

Within the broader Blugrid Common API ecosystem, `common-api-security` serves as the **authentication and authorization backbone** that enables:

- **Unified Security Model**: Provides consistent authentication patterns across all generated APIs
- **Multi-Tenant Isolation**: Ensures data segregation at tenant and business unit levels
- **Framework Integration**: Seamlessly integrates with other common modules:
    - `common-api-model`: Provides security model interfaces and base classes
    - `common-api-jwt`: Handles JWT token generation and validation
    - `common-api-multitenant`: Implements multi-tenant request routing
    - `common-api-persistence`: Enforces data access controls at the persistence layer
    - `common-api-audit`: Captures security events for compliance and monitoring

### Design Philosophy

The security module follows these core principles:

**Domain-Driven Security**: Authentication contexts mirror business domains (Guest → Tenant → Business Unit)

**Zero-Trust Architecture**: Every request is authenticated and authorized regardless of source

**Evolutionary Design**: Authentication can be upgraded (Guest → Tenant → Business Unit) without breaking existing sessions

**Separation of Concerns**: Clear boundaries between authentication, authorization, and session management

**Fail-Safe Defaults**: Security-first approach with explicit opt-out mechanisms

## Architecture

### Core Components

#### Authentication Models
- **`DecoratedAuthentication`**: Base interface for all authentication types
- **`GuestAuthentication`**: Minimal authentication for guest users
- **`TenantAuthentication`**: Organization-scoped authentication
- **`BusinessUnitAuthentication`**: Business unit-scoped authentication within organizations

#### Session Management
- **`BaseAuthenticatedSession`**: Abstract session interface
- **`GuestSession`**: Guest-level session context
- **`TenantSession`**: Tenant-level session with organization context
- **`BusinessUnitSession`**: Business unit-level session with enhanced scope

#### Context Management
- **`CurrentRequestContext`**: Thread-safe request context provider
- **`RequestContextProvider`**: Interface for accessing current authentication context
- **Context Overrides**: Support for tenant and business unit context switching

## Key Features

### Multi-Tenant Authentication
- **Hierarchical authentication levels**: Guest → Tenant → Business Unit
- **Seamless context switching** between authentication scopes
- **Organization-aware security** with automatic tenant resolution

### JWT Token Management
- **RSA-based token signing** with configurable key management
- **JWKS endpoint support** for token verification
- **Token transformation** between different authentication types
- **Expiration handling** and token refresh capabilities

### Session Context
- **Request-scoped authentication** with automatic cleanup
- **Context inheritance** for authentication upgrades
- **Override mechanisms** for administrative operations

### Security Configuration
- **Auth0 integration** for external authentication
- **Cookie-based session management** with configurable settings
- **Redirect configuration** for authentication flows

## 📁 Directory Structure

The security module follows a clean architecture pattern with clear separation of concerns:

```
common-api-security/
├── build.gradle.kts                    # Build configuration and dependencies
├── README.md                           # This documentation
├── scripts/                            # Key management utilities
│   ├── readme.md                       # Key generation instructions
│   ├── PublicKeyExtractor.java         # JWKS generation utility
│   ├── PublicKeyExtractor.class        # Compiled extractor
│   └── publickey.cert                  # Exported public key certificate
└── src/main/
    ├── kotlin/net/blugrid/api/security/
    │   ├── config/                     # Configuration classes
    │   │   ├── RedirectProps.kt        # OAuth/Auth0 redirect configuration
    │   │   └── SecurityProps.kt        # Security settings (Auth0, cookies)
    │   ├── context/                    # Request context management
    │   │   ├── CurrentRequestContext.kt # Thread-safe context provider
    │   │   └── RequestContextOverride.kt # Context override utilities
    │   ├── mapping/                    # Object transformation utilities
    │   │   ├── AuthenticatedOrganisationMapper.kt # Org model mappings
    │   │   ├── AuthenticatedUserMapper.kt # User model mappings
    │   │   ├── AuthenticationMappers.kt # Session context mappings
    │   │   ├── HttpRequestAuthenticationExt.kt # HTTP request extensions
    │   │   ├── JwtAuthenticationDecoder.kt # JWT parsing utilities
    │   │   ├── JwtAuthenticationMapper.kt # JWT token mappings
    │   │   ├── OrganisationMappers.kt  # Organisation transformations
    │   │   ├── SessionMappers.kt       # Session model mappings
    │   │   └── UserIdentityMappers.kt  # User identity transformations
    │   └── model/                      # Core security models
    │       ├── AuthenticatedOrganisationModel.kt # Organisation implementation
    │       ├── AuthenticatedUserIdentity.kt # User identity implementation
    │       ├── BusinessUnitAuthentication.kt # Business unit authentication
    │       ├── GuestAuthentication.kt  # Guest-level authentication
    │       └── TenantAuthentication.kt # Tenant-level authentication
    └── resources/
        ├── jwks.json                   # JSON Web Key Set for token validation
        └── keystore.jks                # Private key storage for token signing
```

## 🛠️ Dependencies

### Runtime Dependencies

**Core Platform**
- `common-api-model`: Provides base security interfaces and model contracts
- `common-api-jwt`: JWT token generation and validation utilities
- `common-api-json`: JSON processing with custom object mappers

**Micronaut Framework**
- `micronaut-bom`: Platform BOM for version management
- `micronaut-core`: Core framework capabilities
- `micronaut-security`: Security framework integration
- `micronaut-reactor`: Reactive programming support
- `micronaut-jackson`: JSON serialization/deserialization

**Security Libraries**
- `nimbus-jose-jwt`: JWT/JOSE token processing
- `jackson-databind`: JSON object mapping
- `reactor-core`: Reactive streams implementation

**AWS Integration**
- `aws-bom`: AWS SDK version management (for potential cloud integrations)

### Development Dependencies

**Build Tools**
- `kotlin-gradle-plugin`: Kotlin compilation support
- `kapt`: Annotation processing for Micronaut
- `allopen-plugin`: Kotlin class opening for frameworks

**Testing**
- `micronaut-test-junit5`: Test framework integration
- `junit-jupiter`: Testing engine
- `mockito-kotlin`: Mocking framework

## Getting Started

### Dependencies

Add the security module to your `build.gradle.kts`:

```kotlin
dependencies {
    // Core security module
    implementation(project(":common:common-kotlin:common-api:common-api-security"))
    
    // Required common modules (auto-included via transitive dependencies)
    // implementation(project(":common:common-kotlin:common-api:common-api-model"))
    // implementation(project(":common:common-kotlin:common-api:common-api-jwt"))
    // implementation(project(":common:common-kotlin:common-api:common-api-json"))
}
```

### Integration with Generated APIs

When using the JDL code generator, security integration is automatically configured:

```kotlin
// Generated in core-*-api modules
@Controller("/api/organisations")
@Secured(SecurityRule.IS_AUTHENTICATED)
class OrganisationController {
    // Security context automatically available
}

// Generated in core-*-api-db modules  
@Singleton
class OrganisationQueryServiceDbImpl : OrganisationQueryService {
    // Automatic tenant/business unit scoping
}
```

### Multi-Module Integration

The security module works seamlessly with other common modules:

```kotlin
// In your service layer
@Singleton
class MyBusinessService(
    private val auditService: AuditEventPublisherService,  // common-api-audit
    private val persistenceService: GenericQueryService    // common-api-persistence
) {
    
    fun performSecureOperation() {
        // Current security context automatically available
        val currentUser = CurrentRequestContext.currentUser
        val currentTenant = CurrentRequestContext.currentTenantId
        
        // Audit events automatically capture security context
        auditService.publishEvent(MyAuditEvent())
        
        // Persistence layer automatically applies security scoping
        val results = persistenceService.findAll(MyEntity::class)
    }
}
```

### Configuration

Configure security properties in your `application.yml`:

```yaml
security:
  auth0:
    audience: "your-api-audience"
    auth0Domain: "your-domain.auth0.com"
    clientId: "your-client-id"
  cookies:
    jwt: "JWT"
    oauthPkce: "OAUTH2_PKCE"
    oathState: "OAUTH2_STATE"
    oathNonce: "OPENID_NONCE"
    maxAge: 300000

redirect:
  loginCallbackUrl: "http://localhost:8080/callback"
  loginSuccessUrl: "http://localhost:8080/dashboard"
  loginFailureUrl: "http://localhost:8080/login?error=true"
  logoutCallbackUrl: "http://localhost:8080/logout"
  logoutUrl: "http://localhost:8080/"
  registrationUrl: "http://localhost:8080/register"
  registrationCallbackUrl: "http://localhost:8080/register/callback"

micronaut:
  security:
    token:
      jwt:
        enabled: true
        signatures:
          jwks-static:
            selfSigned:
              path: "jwks.json"
```

### Usage Examples

#### Accessing Current Authentication Context

```kotlin
@Controller("/api/secure")
class SecureController {
    
    @Get("/user-info")
    fun getUserInfo(): Any {
        val currentUser = CurrentRequestContext.currentUser
        val currentSession = CurrentRequestContext.currentSession
        val currentOrganisation = CurrentRequestContext.currentOrganisation
        
        return mapOf(
            "user" to currentUser,
            "session" to currentSession,
            "organisation" to currentOrganisation
        )
    }
}
```

#### Working with Context Overrides

```kotlin
class AdminService {
    
    fun performAdminOperation(targetTenantId: String) {
        TenantIdOverride(targetTenantId).use { override ->
            // Operations here will use the overridden tenant context
            val targetTenant = CurrentRequestContext.currentTenantId
            // Perform admin operations...
        }
        // Context automatically restored after use block
    }
}
```

#### Custom Authentication Resolution

```kotlin
@Singleton
class CustomAuthenticationService {
    
    fun upgradeToBusinessUnit(
        tenantAuth: TenantAuthentication,
        businessUnitContext: BusinessUnitSessionContext
    ): BusinessUnitAuthentication {
        return tenantAuth.toBusinessUnitAuthentication(businessUnitContext)
    }
}
```

## Authentication Flow

### Guest Authentication Flow
1. **Initial Request**: Anonymous user accesses public endpoints
2. **Guest Token**: System generates guest-level JWT token
3. **Limited Access**: User can access public resources only

### Tenant Authentication Flow
1. **Authentication**: User authenticates through Auth0 or similar
2. **Tenant Resolution**: System determines user's organization context
3. **Tenant Token**: JWT token includes organization information
4. **Tenant Access**: User can access organization-scoped resources

### Business Unit Authentication Flow
1. **Tenant Authentication**: User must first be tenant-authenticated
2. **Business Unit Selection**: User selects specific business unit
3. **Context Upgrade**: Authentication upgraded to business unit level
4. **Enhanced Access**: User can access business unit-specific resources

## Key Management

### Generating Keys

Follow the guide in `/scripts/readme.md` to generate RSA keys:

```bash
# Generate keystore
keytool -genkeypair -alias test-alias -keyalg RSA -keysize 2048 \
    -keystore keystore.jks -storetype JKS -validity 3650

# Export public key
keytool -exportcert -alias test-alias -keystore keystore.jks \
    -rfc -file publickey.cert

# Generate JWKS
java -cp scripts PublicKeyExtractor
```

### Key Rotation

The module supports key rotation through:
- **Multiple key support** in JWKS configuration
- **Graceful key transitions** with overlapping validity periods
- **Automated key loading** from configured sources

## Security Considerations

### Token Security
- **RSA-2048 encryption** for token signing
- **Short token lifetimes** with refresh capability
- **Secure cookie configuration** with HttpOnly and Secure flags

### Context Security
- **Request-scoped contexts** prevent cross-request contamination
- **Automatic context cleanup** after request completion
- **Override tracking** for audit and security monitoring

### Multi-Tenant Isolation
- **Tenant-scoped data access** enforced at authentication level
- **Business unit boundaries** respected in all operations
- **Context validation** prevents unauthorized access

## Testing

### Unit Tests
```kotlin
@Test
fun `should resolve current tenant context`() {
    val tenantAuth = createTenantAuthentication()
    
    doInRequestContext {
        // Set up authentication in request context
        val currentTenant = CurrentRequestContext.currentTenantId
        assertEquals(expectedTenantId, currentTenant)
    }
}
```

### Integration Tests
```kotlin
@MicronautTest
class SecurityIntegrationTest {
    
    @Test
    fun `should authenticate with valid JWT token`() {
        // Test JWT token validation and context resolution
    }
}
```

## Troubleshooting

### Common Issues

**Token Validation Failures**
- Verify JWKS configuration matches keystore
- Check token expiration times
- Ensure proper RSA key format

**Context Resolution Problems**
- Verify request context is properly established
- Check for missing authentication attributes
- Ensure proper session mapping

**Multi-Tenant Access Issues**
- Verify tenant ID resolution logic
- Check organization context mapping
- Ensure proper authentication level

### Debug Configuration

Enable debug logging for security operations:

```yaml
logger:
  levels:
    net.blugrid.api.security: DEBUG
    io.micronaut.security: DEBUG
```

## Performance Considerations

### Token Processing
- **Efficient JWT parsing** with cached key validation using Nimbus JOSE library
- **Minimal token transformation** overhead through direct object mapping
- **Optimized context resolution** with request-scoped caching

### Context Management
- **Lightweight context objects** for minimal memory usage (< 1KB per context)
- **Request-scoped lifecycles** prevent memory leaks with automatic cleanup
- **Lazy initialization** of expensive operations (organisation resolution, user details)

### Security Operations
- **RSA key caching** eliminates repeated key loading operations
- **Session validation** optimized with O(1) lookup times
- **Context switching** minimizes object creation through immutable design

### Scaling Characteristics
- **Stateless design** enables horizontal scaling without session affinity
- **Thread-safe operations** support high-concurrency applications
- **Minimal database impact** through request-scoped context management

## Common Module Ecosystem Integration

The security module is designed to work seamlessly within the broader common module ecosystem:

### Integration Points

**With `common-api-model`**
- Implements security interfaces defined in the model layer
- Provides concrete authentication and session implementations
- Extends base resource types with security context

**With `common-api-jwt`**
- Utilizes JWT generation and validation services
- Provides security-specific token transformations
- Integrates with keystore management utilities

**With `common-api-multitenant`**
- Supplies authentication context for tenant resolution
- Enables automatic request routing based on security context
- Supports context upgrades during request processing

**With `common-api-persistence`**
- Provides security context for automatic data scoping
- Enables row-level security enforcement
- Supports audit trail generation with security information

**With `common-api-audit`**
- Captures security events for compliance monitoring
- Provides user context for audit trail generation
- Enables security-related event processing

### Generated Code Integration

When using the JDL code generator, security integration is automatically configured:

```kotlin
// Generated controller with security annotations
@Controller("/api/organisations")
@Secured(SecurityRule.IS_AUTHENTICATED)
class OrganisationController(
    private val queryService: OrganisationQueryService
) {
    
    @Get("/{id}")
    fun findById(@PathVariable id: Long): OrganisationResource {
        // Security context automatically available
        return queryService.findById(id)
    }
}

// Generated service with automatic security scoping
@Singleton
class OrganisationQueryServiceDbImpl(
    private val repository: OrganisationRepository
) : OrganisationQueryService {
    
    override fun findAll(): List<OrganisationResource> {
        // Automatic tenant/business unit scoping applied
        return repository.findAll().map { it.toResource() }
    }
}
```

# Common Security Testing Strategy

A comprehensive testing approach for the `common-api-security` module that helps new developers understand the codebase and prevents regressions across authentication, authorization, and multi-tenant functionality.

## Test Structure Overview

```
src/test/kotlin/net/blugrid/api/security/
├── unit/                                    # Pure unit tests
│   ├── config/                             # Configuration tests
│   ├── context/                            # Context management tests
│   ├── mapping/                            # Object mapping tests
│   └── model/                              # Model behavior tests
├── integration/                            # Integration tests
│   ├── authentication/                     # Authentication flow tests
│   ├── authorization/                      # Authorization tests
│   ├── jwt/                               # JWT token processing tests
│   └── multitenant/                       # Multi-tenant isolation tests
├── contract/                               # Contract tests
│   ├── session/                           # Session contract tests
│   └── authentication/                    # Authentication contract tests
└── support/                               # Test utilities and fixtures
    ├── fixtures/                          # Test data fixtures
    ├── builders/                          # Test object builders
    └── matchers/                          # Custom assertion matchers
```

## 1. Unit Tests (Core Logic & Reasoning)

### 1.1 Authentication Model Tests

**Purpose**: Help developers understand authentication hierarchies and transformations

```kotlin
// src/test/kotlin/net/blugrid/api/security/unit/model/AuthenticationModelTest.kt
@Test
fun `guest authentication should contain minimal required fields`() {
    val guestAuth = GuestAuthentication(
        providerId = "auth0",
        principalName = "guest_user",
        principalEmail = "guest@example.com",
        sessionId = "session123",
        userId = "user456",
        user = createTestUser(),
        session = createTestGuestSession()
    )
    
    assertThat(guestAuth.authenticationType).isEqualTo(AuthenticationType.GUEST)
    assertThat(guestAuth.attributes).containsKeys("userId", "sessionId", "webApplicationId")
    assertThat(guestAuth.attributes).doesNotContainKey("tenantId")
}

@Test
fun `tenant authentication should upgrade from guest authentication`() {
    val guestAuth = createGuestAuthentication()
    val tenantContext = createTenantSessionContext()
    
    val tenantAuth = guestAuth.upgradeToTenant(tenantContext)
    
    assertThat(tenantAuth.authenticationType).isEqualTo(AuthenticationType.TENANT)
    assertThat(tenantAuth.userId).isEqualTo(guestAuth.userId)
    assertThat(tenantAuth.organisation).isNotNull()
    assertThat(tenantAuth.attributes).containsKey("tenantId")
}

@Test
fun `business unit authentication should require tenant authentication`() {
    val guestAuth = createGuestAuthentication()
    val businessUnitContext = createBusinessUnitSessionContext()
    
    assertThatThrownBy { guestAuth.upgradeToBusinessUnit(businessUnitContext) }
        .isInstanceOf(IllegalArgumentException::class.java)
        .hasMessage("Business unit authentication requires tenant authentication")
}
```

### 1.2 Context Management Tests

**Purpose**: Demonstrate how request context works and prevent context leaks

```kotlin
// src/test/kotlin/net/blugrid/api/security/unit/context/CurrentRequestContextTest.kt
@Test
fun `should resolve current tenant ID from tenant authentication`() {
    val tenantAuth = createTenantAuthentication(tenantId = "tenant123")
    
    doInRequestContext {
        mockAuthentication(tenantAuth)
        
        val currentTenantId = CurrentRequestContext.currentTenantId
        assertThat(currentTenantId).isEqualTo("tenant123")
    }
}

@Test
fun `should override tenant context temporarily`() {
    val originalAuth = createTenantAuthentication(tenantId = "tenant123")
    
    doInRequestContext {
        mockAuthentication(originalAuth)
        
        TenantIdOverride("tenant456").use { override ->
            assertThat(CurrentRequestContext.currentTenantId).isEqualTo("tenant456")
        }
        
        // Context should be restored after override
        assertThat(CurrentRequestContext.currentTenantId).isEqualTo("tenant123")
    }
}

@Test
fun `should handle unscoped context correctly`() {
    val tenantAuth = createTenantAuthentication()
    
    doInRequestContext {
        mockAuthentication(tenantAuth)
        
        IsUnscoped.value = true
        
        assertThat(CurrentRequestContext.currentIsUnscoped).isTrue()
        assertThat(CurrentRequestContext.currentTenantId).isNull()
    }
}
```

### 1.3 Mapping Tests

**Purpose**: Show how objects transform and validate mapping logic

```kotlin
// src/test/kotlin/net/blugrid/api/security/unit/mapping/AuthenticationMappersTest.kt
@Test
fun `should map guest session context to guest authentication`() {
    val context = GuestSessionContext(
        id = 1L,
        user = createTestUserIdentity(),
        webApplicationId = 100001L
    )
    
    val authentication = context.toAuthentication()
    
    assertThat(authentication).isInstanceOf(GuestAuthentication::class.java)
    assertThat(authentication.sessionId).isEqualTo("1")
    assertThat(authentication.user.userIdentityId).isEqualTo(context.user.id.toString())
}

@Test
fun `should map JWT token to tenant authentication`() {
    val jwtToken = JwtToken(
        authenticationType = AuthenticationType.TENANT,
        user = createTestAuthenticatedUser(),
        session = createTestTenantSession(),
        organisation = createTestOrganisation()
    )
    
    val authentication = jwtToken.toAuthentication(AuthenticationType.TENANT)
    
    assertThat(authentication).isInstanceOf(TenantAuthentication::class.java)
    val tenantAuth = authentication as TenantAuthentication
    assertThat(tenantAuth.organisation.tenantId).isEqualTo(jwtToken.organisation?.tenantId)
}
```

## 2. Integration Tests (End-to-End Flows)

### 2.1 Authentication Flow Tests

**Purpose**: Validate complete authentication flows and JWT processing

```kotlin
// src/test/kotlin/net/blugrid/api/security/integration/authentication/AuthenticationFlowTest.kt
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationFlowTest {
    
    @Inject
    lateinit var jwtDecoder: JwtDecoder
    
    @Inject
    lateinit var jwtEncoder: JwtEncoder
    
    @Test
    fun `should authenticate with valid JWT token`() {
        val jwtToken = createValidJwtToken()
        val encodedToken = jwtEncoder.encode(jwtToken)
        
        val authentication = jwtDecoder.decode(encodedToken)
        
        assertThat(authentication).isNotNull()
        assertThat(authentication.authenticationType).isEqualTo(AuthenticationType.TENANT)
    }
    
    @Test
    fun `should reject expired JWT token`() {
        val expiredToken = createExpiredJwtToken()
        val encodedToken = jwtEncoder.encode(expiredToken)
        
        assertThatThrownBy { jwtDecoder.decode(encodedToken) }
            .isInstanceOf(JwtException::class.java)
            .hasMessageContaining("Token expired")
    }
    
    @Test
    fun `should validate JWT signature correctly`() {
        val token = createValidJwtToken()
        val encodedToken = jwtEncoder.encode(token)
        val tamperedToken = encodedToken.substring(0, encodedToken.length - 10) + "tampered123"
        
        assertThatThrownBy { jwtDecoder.decode(tamperedToken) }
            .isInstanceOf(JwtException::class.java)
            .hasMessageContaining("Invalid signature")
    }
}
```

### 2.2 Multi-Tenant Isolation Tests

**Purpose**: Ensure tenant data isolation and prevent cross-tenant access

```kotlin
// src/test/kotlin/net/blugrid/api/security/integration/multitenant/MultiTenantIsolationTest.kt
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MultiTenantIsolationTest {
    
    @Test
    fun `should isolate tenant data access`() {
        val tenant1Auth = createTenantAuthentication(tenantId = "tenant1")
        val tenant2Auth = createTenantAuthentication(tenantId = "tenant2")
        
        // Test tenant 1 access
        withAuthenticationContext(tenant1Auth) {
            val tenant1Data = someDataService.findAll()
            assertThat(tenant1Data).allMatch { it.tenantId == "tenant1" }
        }
        
        // Test tenant 2 access
        withAuthenticationContext(tenant2Auth) {
            val tenant2Data = someDataService.findAll()
            assertThat(tenant2Data).allMatch { it.tenantId == "tenant2" }
        }
    }
    
    @Test
    fun `should prevent cross-tenant data access`() {
        val tenant1Auth = createTenantAuthentication(tenantId = "tenant1")
        val tenant2Resource = createResourceForTenant("tenant2")
        
        withAuthenticationContext(tenant1Auth) {
            assertThatThrownBy { resourceService.findById(tenant2Resource.id) }
                .isInstanceOf(AccessDeniedException::class.java)
        }
    }
}
```

### 2.3 Session Management Tests

**Purpose**: Test session lifecycle and context switching

```kotlin
// src/test/kotlin/net/blugrid/api/security/integration/session/SessionManagementTest.kt
@Test
fun `should maintain session context across requests`() {
    val sessionId = "session123"
    val authentication = createTenantAuthentication(sessionId = sessionId)
    
    withAuthenticationContext(authentication) {
        assertThat(CurrentRequestContext.currentSessionId).isEqualTo(sessionId.toLong())
        
        // Simulate nested service calls
        businessService.performOperation()
        
        // Context should be maintained
        assertThat(CurrentRequestContext.currentSessionId).isEqualTo(sessionId.toLong())
    }
}

@Test
fun `should upgrade session context from guest to tenant`() {
    val guestAuth = createGuestAuthentication()
    val tenantContext = createTenantSessionContext()
    
    withAuthenticationContext(guestAuth) {
        assertThat(CurrentRequestContext.currentTenantId).isNull()
        
        val upgradeService = getBean<AuthenticationUpgradeService>()
        val tenantAuth = upgradeService.upgradeToTenant(tenantContext)
        
        // Context should be upgraded
        assertThat(CurrentRequestContext.currentTenantId).isEqualTo(tenantAuth.organisation.tenantId)
    }
}
```

## 3. Contract Tests (API Contracts)

### 3.1 Security Context Contract Tests

**Purpose**: Ensure consistent security context across all modules

```kotlin
// src/test/kotlin/net/blugrid/api/security/contract/SecurityContextContractTest.kt
@Test
fun `security context should provide consistent tenant resolution`() {
    val testCases = listOf(
        TestCase(AuthenticationType.GUEST, expectedTenantId = null),
        TestCase(AuthenticationType.TENANT, expectedTenantId = "tenant123"),
        TestCase(AuthenticationType.BUSINESS_UNIT, expectedTenantId = "tenant456")
    )
    
    testCases.forEach { testCase ->
        val authentication = createAuthentication(testCase.type, testCase.expectedTenantId)
        
        withAuthenticationContext(authentication) {
            val resolvedTenantId = CurrentRequestContext.currentTenantId
            assertThat(resolvedTenantId).isEqualTo(testCase.expectedTenantId)
        }
    }
}
```

### 3.2 JWT Token Contract Tests

**Purpose**: Ensure JWT tokens are correctly formatted and processable

```kotlin
// src/test/kotlin/net/blugrid/api/security/contract/JwtTokenContractTest.kt
@Test
fun `JWT tokens should contain required claims`() {
    val authenticationTypes = listOf(
        AuthenticationType.GUEST,
        AuthenticationType.TENANT,
        AuthenticationType.BUSINESS_UNIT
    )
    
    authenticationTypes.forEach { authType ->
        val token = createJwtToken(authType)
        val encodedToken = jwtEncoder.encode(token)
        
        val claims = jwtDecoder.extractClaims(encodedToken)
        
        assertThat(claims).containsKeys("authentication_type", "user", "session")
        
        if (authType != AuthenticationType.GUEST) {
            assertThat(claims).containsKey("organisation")
        }
    }
}
```

## 4. Performance Tests

### 4.1 Context Resolution Performance

**Purpose**: Ensure security operations don't introduce performance bottlenecks

```kotlin
// src/test/kotlin/net/blugrid/api/security/performance/ContextResolutionPerformanceTest.kt
@Test
fun `context resolution should perform within acceptable limits`() {
    val authentication = createTenantAuthentication()
    
    val executionTime = measureTimeMillis {
        repeat(10000) {
            withAuthenticationContext(authentication) {
                CurrentRequestContext.currentTenantId
                CurrentRequestContext.currentUser
                CurrentRequestContext.currentSession
            }
        }
    }
    
    // Should complete 10,000 operations in under 100ms
    assertThat(executionTime).isLessThan(100)
}
```

### 4.2 JWT Processing Performance

**Purpose**: Validate JWT processing performance under load

```kotlin
@Test
fun `JWT token processing should handle concurrent requests`() {
    val tokens = (1..100).map { createValidJwtToken() }
    
    val executionTime = measureTimeMillis {
        tokens.parallelStream().forEach { token ->
            val encodedToken = jwtEncoder.encode(token)
            val decodedAuth = jwtDecoder.decode(encodedToken)
            assertThat(decodedAuth).isNotNull()
        }
    }
    
    // Should process 100 tokens in under 500ms
    assertThat(executionTime).isLessThan(500)
}
```

## 5. Security Tests

### 5.1 Token Tampering Tests

**Purpose**: Ensure security measures work correctly

```kotlin
// src/test/kotlin/net/blugrid/api/security/security/TokenSecurityTest.kt
@Test
fun `should reject tokens with tampered payload`() {
    val validToken = createValidJwtToken()
    val encodedToken = jwtEncoder.encode(validToken)
    
    val tokenParts = encodedToken.split(".")
    val tamperedPayload = Base64.getEncoder().encodeToString(
        """{"authentication_type":"ADMIN","user":{"userIdentityId":"hacker"}}""".toByteArray()
    )
    val tamperedToken = "${tokenParts[0]}.$tamperedPayload.${tokenParts[2]}"
    
    assertThatThrownBy { jwtDecoder.decode(tamperedToken) }
        .isInstanceOf(JwtException::class.java)
        .hasMessageContaining("Invalid signature")
}

@Test
fun `should validate token expiration strictly`() {
    val clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
    val expiredToken = createTokenWithExpiration(clock.instant().minusSeconds(1))
    
    assertThatThrownBy { jwtDecoder.decode(expiredToken) }
        .isInstanceOf(JwtException::class.java)
        .hasMessageContaining("Token expired")
}
```

### 5.2 Authorization Bypass Tests

**Purpose**: Ensure authorization cannot be bypassed

```kotlin
@Test
fun `should prevent privilege escalation through context override`() {
    val guestAuth = createGuestAuthentication()
    
    withAuthenticationContext(guestAuth) {
        assertThatThrownBy {
            // Attempt to override to admin context
            TenantIdOverride("admin-tenant").use {
                adminService.performAdminOperation()
            }
        }.isInstanceOf(AccessDeniedException::class.java)
    }
}
```

## 6. Test Support Infrastructure

### 6.1 Test Fixtures and Builders

**Purpose**: Provide consistent test data creation

```kotlin
// src/test/kotlin/net/blugrid/api/security/support/fixtures/AuthenticationFixtures.kt
object AuthenticationFixtures {
    
    fun createGuestAuthentication(
        sessionId: String = "session123",
        userId: String = "user456"
    ): GuestAuthentication = GuestAuthentication(
        providerId = "auth0",
        principalName = "guest_user",
        principalEmail = "guest@example.com",
        sessionId = sessionId,
        userId = userId,
        user = createTestUser(userId),
        session = createTestGuestSession(sessionId)
    )
    
    fun createTenantAuthentication(
        tenantId: String = "tenant123",
        sessionId: String = "session123"
    ): TenantAuthentication = TenantAuthentication(
        providerId = "auth0",
        principalName = "tenant_user",
        principalEmail = "user@tenant.com",
        sessionId = sessionId,
        userId = "user456",
        organisation = createTestOrganisation(tenantId),
        session = createTestTenantSession(sessionId, tenantId),
        user = createTestUser()
    )
}
```

### 6.2 Test Utilities

**Purpose**: Provide helper methods for common test scenarios

```kotlin
// src/test/kotlin/net/blugrid/api/security/support/TestUtils.kt
fun <T> withAuthenticationContext(
    authentication: DecoratedAuthentication<*>,
    block: () -> T
): T {
    return doInRequestContext {
        mockAuthentication(authentication)
        block()
    }
}

fun mockAuthentication(authentication: DecoratedAuthentication<*>) {
    val request = HttpRequest.GET<Any>("/test")
    request.setAttribute(SecurityFilter.AUTHENTICATION, authentication)
    // Set up request context
}
```

## 7. Regression Prevention Strategy

### 7.1 Critical Path Tests

**Purpose**: Ensure core functionality never breaks

```kotlin
// src/test/kotlin/net/blugrid/api/security/regression/CriticalPathTest.kt
@Test
fun `critical path - guest to tenant authentication upgrade`() {
    val guestAuth = createGuestAuthentication()
    val tenantContext = createTenantSessionContext()
    
    // This flow must never break
    val tenantAuth = guestAuth.upgradeToTenant(tenantContext)
    
    assertThat(tenantAuth.authenticationType).isEqualTo(AuthenticationType.TENANT)
    assertThat(tenantAuth.userId).isEqualTo(guestAuth.userId)
    assertThat(tenantAuth.organisation).isNotNull()
}
```

### 7.2 Boundary Condition Tests

**Purpose**: Test edge cases and boundary conditions

```kotlin
@Test
fun `should handle null and empty values gracefully`() {
    assertThatThrownBy { createTenantAuthentication(tenantId = "") }
        .isInstanceOf(IllegalArgumentException::class.java)
        
    assertThatThrownBy { createTenantAuthentication(tenantId = null) }
        .isInstanceOf(IllegalArgumentException::class.java)
}

@Test
fun `should handle extremely long tenant IDs`() {
    val longTenantId = "a".repeat(1000)
    
    assertThatNoException().isThrownBy {
        createTenantAuthentication(tenantId = longTenantId)
    }
}
```

## 8. Test Configuration

### 8.1 Test Properties

```yaml
# src/test/resources/application-test.yml
security:
  test:
    jwt:
      expiration: 3600
      test-key-id: "test-key-id"
    auth0:
      mock-enabled: true
      test-domain: "test.auth0.com"
```

### 8.2 Test Dependencies

```kotlin
// build.gradle.kts test dependencies
dependencies {
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.assertj:assertj-core")
    testImplementation("io.mockk:mockk")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.github.tomakehurst:wiremock-jre8")
}
```

## Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test categories
./gradlew test --tests "*.unit.*"
./gradlew test --tests "*.integration.*"
./gradlew test --tests "*.contract.*"

# Run with coverage
./gradlew test jacocoTestReport

# Run performance tests
./gradlew test --tests "*.performance.*"
```

This comprehensive testing strategy ensures that new developers can understand the security module through executable examples while providing robust regression protection for all critical security functionality.

## License

This module is part of the Blugrid platform and subject to proprietary licensing terms.

## Support

For support with the security module:
- Review troubleshooting section above
- Check project documentation
- Contact the platform team for assistance
