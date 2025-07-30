# API Framework Error Handling Strategy

## Overview

This framework implements a comprehensive, layered error handling strategy that provides consistent error management across REST APIs, gRPC services, and domain logic. The design follows **separation of concerns** principles with clear boundaries between domain exceptions, API exceptions, and protocol-specific error representations.

## Architecture Principles

### 1. **Layered Exception Strategy**
```
Domain Layer     →  API Layer        →  Protocol Layer
DomainException  →  APIException     →  HTTP/gRPC Response
(Business Logic)    (API Contract)      (Transport Protocol)
```

### 2. **Rich Error Metadata**
Every exception carries structured metadata including:
- Error codes and severity levels
- Correlation and trace IDs
- Contextual information
- Retry guidance
- Timestamps

### 3. **Protocol Agnostic Design**
Domain exceptions are independent of transport protocols, enabling consistent behavior across REST, gRPC, and future protocols.

## Core Components

### Domain Layer (`common-domain`)

**Location**: `./common/common-kotlin/common/common-domain/src/main/kotlin/net/blugrid/common/domain/exception/`

The foundational exception hierarchy for business logic:

```kotlin
// Base domain exception with rich metadata
abstract class DomainException(
    message: String,
    cause: Throwable? = null,
    val metadata: ErrorMetadata
) : RuntimeException(message, cause)

// Rich error context
data class ErrorMetadata(
    val code: String,
    val severity: ErrorSeverity = ErrorSeverity.ERROR,
    val retryable: Boolean = false,
    val correlationId: String? = null,
    val context: Map<String, Any> = emptyMap(),
    val timestamp: Instant = Instant.now(),
    val traceId: String = UUID.randomUUID().toString()
)
```

**Key Exception Types**:
- `NotFoundException` - Resource not found scenarios
- `ValidationException` - Field and business validation errors
- `AccessDeniedException` - Authorization failures
- `BusinessRuleException` - Business rule violations
- `ResourceConflictException` - Resource state conflicts
- `TenantContextException` - Multi-tenant context issues
- `InternalServerException` - System-level errors

**Usage Pattern**:
```kotlin
// Service layer business logic
class OrganisationService {
    fun findById(id: Long): Organisation {
        return repository.findById(id) 
            ?: throw "Organisation".notFound(id)
    }
    
    fun validateAndCreate(request: CreateRequest): Organisation {
        validation {
            required("name")
            invalid("email", "Invalid format", request.email)
        }
        // Business logic continues...
    }
}
```

### API Layer (`common-model`)

**Location**: `./common/common-kotlin/common/common-model/src/main/kotlin/net/blugrid/common/`

Protocol-neutral API exceptions with HTTP status code semantics:

```kotlin
// Base API exception
open class APIException : RuntimeException {
    val code: String
    var statusCode: String? = null
    var correlationId: String? = null
    var params: Array<out Any>? = null
}

// Hierarchy
abstract class APIClientException : APIException    // 4xx equivalent  
abstract class APIServerException : APIException    // 5xx equivalent
```

**Generic API Exception Types** (`common/api/exception/`):
- `ResourceNotFoundAPIException` - `@ResponseStatus(NOT_FOUND)`
- `ResourceValidationAPIException` - `@ResponseStatus(BAD_REQUEST)`
- `ResourceAccessDeniedAPIException` - `@ResponseStatus(FORBIDDEN)`
- `BusinessRuleViolationAPIException` - `@ResponseStatus(UNPROCESSABLE_ENTITY)`
- `InternalServerErrorAPIException` - `@ResponseStatus(INTERNAL_SERVER_ERROR)`

**Mapping Layer**:
```kotlin
@Singleton
class APIExceptionMapper {
    fun mapToAPIException(throwable: Throwable, operation: String?): APIException {
        return when {
            throwable is APIException -> throwable
            throwable is NotFoundException -> mapNotFoundException(throwable)
            throwable is ValidationException -> mapValidationException(throwable)
            // Framework exception mapping via reflection
            else -> mapByClassName(throwable, operation)
        }
    }
}
```

### Protocol Layer

#### REST API (`server-rest`)
**Location**: `./common/common-kotlin/server/server-rest/src/main/kotlin/net/blugrid/server/rest/exceptions/`

```kotlin
@Controller
class GlobalAPIExceptionHandler(
    private val apiExceptionMapper: APIExceptionMapper,
    private val errorLogger: ErrorLogger
) {
    @Error(global = true, exception = APIException::class)
    fun handleAPIException(
        request: HttpRequest<*>,
        exception: APIException
    ): HttpResponse<APIErrorResponse> {
        errorLogger.logError(exception, "${request.method} ${request.path}")
        return mapAPIExceptionToHttp(exception, request)
    }
}
```

**Response Format** (RFC 7807 Problem Details):
```json
{
  "error": {
    "type": "resource-not-found",
    "title": "Resource Not Found",
    "message": "Organisation with id '123' was not found",
    "status": 404
  },
  "details": [
    {
      "field": "id",
      "code": "NOT_FOUND",
      "message": "Organisation not found"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z",
  "traceId": "550e8400-e29b-41d4-a716-446655440000",
  "path": "/api/organisations/123",
  "method": "GET"
}
```

#### gRPC (`integration-grpc`)
**Location**: `./common/common-kotlin/integration/integration-grpc/src/main/kotlin/net/blugrid/integration/grpc/`

```kotlin
@Singleton
class GrpcErrorMapper {
    fun mapToGrpcException(
        exception: Exception,
        grpcMethod: String,
        correlationId: String? = null
    ): StatusException {
        val apiException = when (exception) {
            is APIException -> exception
            is DomainException -> mapDomainToApiException(exception)
            else -> InternalServerErrorAPIException("Unexpected error")
        }
        
        return mapApiExceptionToGrpc(apiException, grpcMethod, correlationId)
    }
}
```

**gRPC Error Details**:
- Uses `google.rpc.Status` with rich error details
- Structured error information via protobuf `Any` messages
- Maps to appropriate gRPC status codes (`NOT_FOUND`, `INVALID_ARGUMENT`, etc.)

### Logging Layer (`platform-logging`)

**Location**: `./common/common-kotlin/platform/platform-logging/src/main/kotlin/net/blugrid/platform/logging/api/`

Structured error logging with correlation:

```kotlin
@Singleton
class ErrorLogger {
    fun logError(
        throwable: Throwable,
        context: String,
        operation: String? = null,
        metadata: Map<String, Any?> = emptyMap()
    ) {
        val logContext = buildLogContext(throwable, context, operation, metadata)
        val message = buildLogMessage(throwable, context, operation)
        log.error(message, logContext, throwable)
    }
}

@Singleton  
class ErrorCorrelationService {
    fun withCorrelation(operation: (String) -> Unit) {
        val correlationId = generateCorrelationId()
        MDC.put("correlationId", correlationId)
        try {
            operation(correlationId)
        } finally {
            MDC.remove("correlationId")
        }
    }
}
```

## Error Flow Examples

### 1. Domain Exception → REST API Response

```kotlin
// Service layer
class OrganisationService {
    fun findById(id: Long): Organisation {
        return repository.findById(id) 
            ?: throw NotFoundException("Organisation", id)  // Domain exception
    }
}

// Controller (automatic handling)
@Get("/{id}")
fun getOrganisation(@PathVariable id: Long): Organisation {
    return organisationService.findById(id)  // May throw NotFoundException
}

// Global exception handler automatically:
// 1. Maps NotFoundException → ResourceNotFoundAPIException
// 2. Logs structured error with correlation ID
// 3. Returns HTTP 404 with RFC 7807 problem details
```

### 2. Validation Exception → gRPC Response

```kotlin
// Domain validation
fun validateCreate(request: CreateOrganisationRequest) {
    validation {
        if (request.name.isBlank()) required("name") 
        if (request.email.isInvalid()) invalid("email", "Invalid format")
    }  // Throws MultipleFieldValidationException
}

// gRPC service
override suspend fun createOrganisation(request: CreateOrganisationRequest): Organisation {
    validateCreate(request)  // May throw ValidationException
    return organisationService.create(request)
}

// GrpcErrorMapper automatically:
// 1. Maps ValidationException → ResourceValidationAPIException  
// 2. Creates gRPC Status with INVALID_ARGUMENT code
// 3. Adds ValidationError details for each field violation
// 4. Includes rich ErrorMetadata with correlation tracking
```

## Extension Guide

### Adding New Domain Exception Types

**Step 1**: Create the domain exception
```kotlin
// ./common/common-domain/src/main/kotlin/net/blugrid/common/domain/exception/
class QuotaExceededException(
    resourceType: String,
    currentUsage: Long,
    maxAllowed: Long,
    cause: Throwable? = null
) : DomainException(
    message = "$resourceType quota exceeded: $currentUsage/$maxAllowed",
    cause = cause,
    metadata = ErrorMetadata(
        code = "QUOTA_EXCEEDED",
        severity = ErrorSeverity.ERROR,
        retryable = false,
        context = mapOf(
            "resourceType" to resourceType,
            "currentUsage" to currentUsage,
            "maxAllowed" to maxAllowed
        )
    )
)
```

**Step 2**: Create corresponding API exception
```kotlin
// ./common/common-model/src/main/kotlin/net/blugrid/common/api/exception/
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
class QuotaExceededAPIException : APIClientException {
    companion object {
        const val CODE = "quota.exceeded"
    }

    constructor(resourceType: String, currentUsage: Long, maxAllowed: Long) 
        : super(CODE, resourceType, currentUsage, maxAllowed)
    
    constructor(error: APIError) : super(error)
}
```

**Step 3**: Add to factory classes
```kotlin
// DomainExceptions factory
object DomainExceptions {
    fun quotaExceeded(resourceType: String, current: Long, max: Long) =
        QuotaExceededException(resourceType, current, max)
}

// APIExceptions factory  
object APIExceptions {
    fun quotaExceeded(resourceType: String, current: Long, max: Long) =
        QuotaExceededAPIException(resourceType, current, max)
}
```

**Step 4**: Update mapping layer
```kotlin
// APIExceptionMapper
class APIExceptionMapper {
    fun mapToAPIException(throwable: Throwable, operation: String?): APIException {
        return when {
            throwable is QuotaExceededException -> mapQuotaExceeded(throwable)
            // ... existing mappings
        }
    }
    
    private fun mapQuotaExceeded(ex: QuotaExceededException): APIException {
        val resourceType = ex.metadata.context["resourceType"] as String
        val current = ex.metadata.context["currentUsage"] as Long  
        val max = ex.metadata.context["maxAllowed"] as Long
        return QuotaExceededAPIException(resourceType, current, max)
    }
}
```

**Step 5**: Add gRPC mapping (if using gRPC)
```kotlin
// GrpcErrorMapper
class GrpcErrorMapper {
    private fun mapToGrpcCode(apiException: APIException): GrpcStatus {
        return when (apiException) {
            is QuotaExceededAPIException -> GrpcStatus.RESOURCE_EXHAUSTED
            // ... existing mappings
        }
    }
}
```

**Step 6**: Add to error converter (for HTTP clients)
```kotlin
// APIErrorConverter
object APIErrorConverter {
    fun convert(error: APIError): APIException? =
        when (error.type) {
            QuotaExceededAPIException.CODE -> QuotaExceededAPIException(error)
            // ... existing mappings
        }
}
```

### Adding New Protocol Support

**Step 1**: Create protocol-specific mapper
```kotlin
// ./common/integration/integration-graphql/
@Singleton
class GraphQLErrorMapper {
    fun mapToGraphQLError(apiException: APIException): GraphQLError {
        return when (apiException) {
            is ResourceNotFoundAPIException -> createNotFoundError(apiException)
            is ResourceValidationAPIException -> createValidationError(apiException)
            // ... other mappings
        }
    }
}
```

**Step 2**: Create global error handler
```kotlin
@Component
class GraphQLExceptionHandler : DataFetcherExceptionHandler {
    override fun onException(
        handlerParameters: DataFetcherExceptionHandlerParameters
    ): DataFetcherExceptionHandlerResult {
        val exception = handlerParameters.exception
        val apiException = apiExceptionMapper.mapToAPIException(exception)
        val graphqlError = graphqlErrorMapper.mapToGraphQLError(apiException)
        
        errorLogger.logError(exception, "GraphQL Operation")
        
        return DataFetcherExceptionHandlerResult.newResult()
            .error(graphqlError)
            .build()
    }
}
```

### Adding Business-Specific Exception Types

For domain-specific exceptions (e.g., property management):

**Step 1**: Create business domain exceptions
```kotlin
// ./business/property-management/src/main/kotlin/exceptions/
class PropertyUnavailableException(
    propertyId: String,
    fromDate: LocalDate,
    toDate: LocalDate,
    conflictingBookingId: String? = null
) : DomainException(
    message = "Property $propertyId unavailable from $fromDate to $toDate",
    metadata = ErrorMetadata(
        code = "PROPERTY_UNAVAILABLE", 
        severity = ErrorSeverity.ERROR,
        retryable = false,
        context = mapOf(
            "propertyId" to propertyId,
            "fromDate" to fromDate,
            "toDate" to toDate,
            "conflictingBookingId" to conflictingBookingId
        )
    )
)
```

**Step 2**: Create API exception
```kotlin
@ResponseStatus(HttpStatus.CONFLICT)
class PropertyUnavailableAPIException : APIClientException {
    companion object {
        const val CODE = "property.unavailable"
    }

    constructor(propertyId: String, fromDate: LocalDate, toDate: LocalDate) 
        : super(CODE, propertyId, fromDate, toDate)
}
```

**Step 3**: Add domain-specific mapper in the business module
```kotlin
// In your property management service module
@Singleton
class PropertyExceptionMapper : ExceptionMapper {
    override fun canHandle(throwable: Throwable): Boolean {
        return throwable is PropertyUnavailableException
    }
    
    override fun mapToAPIException(throwable: Throwable): APIException {
        return when (throwable) {
            is PropertyUnavailableException -> mapPropertyUnavailable(throwable)
            else -> throw IllegalArgumentException("Cannot handle ${throwable::class}")
        }
    }
}
```

## Best Practices

### 1. **Exception Creation**
```kotlin
// ✅ Good: Use factory methods
throw DomainExceptions.notFound("Organisation", id)
throw APIExceptions.validationFailed("User", "email", "Invalid format")

// ✅ Good: Use extension functions  
throw "Organisation".notFound(id)

// ✅ Good: Use DSL for complex validation
validation {
    required("name")
    invalid("email", "Must be valid email", request.email)
    tooLong("description", 500, request.description.length)
}
```

### 2. **Context Enrichment**
```kotlin
// ✅ Good: Add rich context
domainContext(
    resourceType = "Organisation",
    identifier = id,
    operation = "update",
    correlationId = getCurrentCorrelationId()
).validation {
    required("name")
    invalid("status", "Invalid transition", newStatus)
}
```

### 3. **Logging Integration**
```kotlin
// ✅ Good: Use correlation service
errorCorrelationService.withCorrelation { correlationId ->
    try {
        performOperation()
    } catch (ex: DomainException) {
        errorLogger.logError(
            throwable = ex,
            context = "Organisation.update",
            operation = "updateOrganisation",
            metadata = mapOf("organisationId" to id)
        )
        throw ex
    }
}
```

### 4. **Protocol-Specific Handling**
```kotlin
// ✅ Good: Let global handlers manage protocol concerns
// Service layer stays clean
class OrganisationService {
    fun create(request: CreateRequest): Organisation {
        validateRequest(request)  // May throw ValidationException
        return repository.save(request.toEntity())
    }
}

// Global handlers automatically convert to appropriate protocol response
```

## Module Dependencies

```
Domain Exceptions (common-domain)
    ↓
API Exceptions (common-model) 
    ↓  
Protocol Mappers (server-rest, integration-grpc)
    ↓
Application Services (business modules)
```

This strategy ensures:
- **Clean separation** between business logic and protocol concerns
- **Consistent error handling** across all transport protocols
- **Rich error context** for debugging and monitoring
- **Easy extensibility** for new exception types and protocols
- **Structured logging** with correlation tracking
- **Protocol-agnostic** domain layer

The framework handles the complexity of error translation while keeping business code focused on domain logic.
