# Error Mapping Architecture Strategy

## Current Problem
Putting all error mapping in `integration-grpc` creates:
- **Tight coupling** between gRPC and domain exceptions
- **Duplication** when REST needs similar mapping
- **Violation** of dependency direction (infrastructure depending on domain)

## Implemented Architecture

### 1. Core Error Abstractions (`common-model`)
**Location**: `common/common-kotlin/common/common-model/src/main/kotlin/net/blugrid/common/model/exception/`

✅ **Implemented**: Abstract error contracts and base types
- `ErrorMapper.kt`: Generic protocol mapping interfaces
- `TenantContextException.kt`: Domain-specific exception example

```kotlin
// Enhanced domain exceptions with protocol-agnostic metadata
abstract class DomainException(
    message: String,
    cause: Throwable? = null,
    val metadata: ErrorMetadata
) : RuntimeException(message, cause)

data class ErrorMetadata(
    val code: String,
    val severity: ErrorSeverity = ErrorSeverity.ERROR,
    val retryable: Boolean = false,
    val correlationId: String? = null,
    val context: Map<String, Any> = emptyMap()
)
```

### 2. HTTP Error Mapping (`server-rest`)
**Location**: `common/common-kotlin/server/server-rest/src/main/kotlin/net/blugrid/server/rest/exceptions/`

✅ **Implemented**: REST-specific error handling
- `RestErrorMapper.kt`: Maps domain exceptions to HTTP responses
- `GlobalExceptionHandler.kt`: Updated to use new architecture

```kotlin
@Singleton
class RestErrorMapper : ProtocolErrorMapper<HttpResponse<APIErrorResponse>> {
    
    override fun mapDomainException(exception: DomainException): HttpResponse<APIErrorResponse> {
        val httpStatus = when (exception.metadata.code) {
            "TENANT_CONTEXT_MISSING" -> HttpStatus.PRECONDITION_FAILED
            "VALIDATION_ERROR" -> HttpStatus.BAD_REQUEST
            "NOT_FOUND" -> HttpStatus.NOT_FOUND
            "ACCESS_DENIED" -> HttpStatus.FORBIDDEN
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        
        return HttpResponse.status<APIErrorResponse>(httpStatus)
            .body(createErrorResponse(exception))
    }
}
```

### 3. gRPC Error Mapping (`integration-grpc`)
**Location**: `common/common-kotlin/integration/integration-grpc/src/main/kotlin/net/blugrid/integration/grpc/exceptions/`

✅ **Implemented**: gRPC-specific error handling only
- `GrpcErrorMapper.kt`: Maps domain exceptions to gRPC Status codes
- `GrpcMetadataBuilder.kt`: Builds structured gRPC metadata
- Updated `GrpcServiceBase.kt`: Uses new error handling approach

```kotlin
@Singleton
class GrpcErrorMapper : ProtocolErrorMapper<Status> {
    
    private val statusMapping = mapOf(
        "TENANT_CONTEXT_MISSING" to Status.Code.FAILED_PRECONDITION,
        "VALIDATION_ERROR" to Status.Code.INVALID_ARGUMENT,
        "NOT_FOUND" to Status.Code.NOT_FOUND,
        "ACCESS_DENIED" to Status.Code.PERMISSION_DENIED
    )
    
    override fun mapDomainException(exception: DomainException): Status {
        val grpcCode = statusMapping[exception.metadata.code] ?: Status.Code.INTERNAL
        
        return Status.fromCode(grpcCode)
            .withDescription(exception.message)
            .withCause(exception)
    }
}
```

### 4. Platform Error Utilities (`platform-logging`)
**Location**: `common/common-kotlin/platform/platform-logging/src/main/kotlin/net/blugrid/platform/logging/api/`

✅ **Implemented**: Cross-cutting error concerns
- `ErrorCorrelationService.kt`: Manages correlation IDs across requests
- `StructuredErrorLogger.kt`: Provides consistent error logging

```kotlin
@Singleton
class StructuredErrorLogger {
    
    fun logError(
        exception: Exception,
        protocol: String,
        context: Map<String, Any> = emptyMap()
    ) {
        val errorInfo = mutableMapOf<String, Any>(
            "protocol" to protocol,
            "exceptionType" to exception.javaClass.simpleName,
            "message" to (exception.message ?: "Unknown error")
        ).apply { putAll(context) }
        
        if (exception is DomainException) {
            errorInfo["errorCode"] = exception.metadata.code
            errorInfo["severity"] = exception.metadata.severity
            errorInfo["retryable"] = exception.metadata.retryable
            errorInfo.putAll(exception.metadata.context)
        }
        
        logger.error("Error occurred: {}", errorInfo, exception)
    }
}
```

## Architecture Benefits

### ✅ **Proper Separation of Concerns**
- Domain exceptions in domain modules
- Protocol mapping in protocol modules  
- Shared contracts in common modules

### ✅ **Dependency Direction**
```
Domain Layer (exceptions) ← Infrastructure Layer (mappers) ← Application Layer (services)
```

### ✅ **Extensibility**
Easy to add new protocols (GraphQL, WebSocket, etc.) without touching existing code

### ✅ **Testability**
Each mapper can be unit tested independently

### ✅ **Consistency**
All protocols share the same error metadata structure

## File Organization Summary

```
common/
├── common-model/               # Error contracts & base exceptions
│   └── exception/
│       ├── ErrorMapper.kt
│       └── TenantContextException.kt
├── platform-logging/          # Cross-cutting error utilities
│   └── api/
│       ├── ErrorCorrelationService.kt
│       └── StructuredErrorLogger.kt

server/
├── server-rest/               # REST-specific error handling
│   └── exceptions/
│       ├── RestErrorMapper.kt
│       └── GlobalExceptionHandler.kt

integration/
├── integration-grpc/          # gRPC-specific error handling
│   └── exceptions/
│       ├── GrpcErrorMapper.kt
│       ├── GrpcMetadataBuilder.kt
│       └── service/GrpcServiceBase.kt

core-organisation-api/
├── core-organisation-api-model/ # Domain-specific exceptions (future)
│   └── exception/
```

## Usage Examples

### Domain Layer Usage
```kotlin
// In a service
throw TenantContextException(
    message = "Invalid tenant ID: $tenantId",
    correlationId = correlationService.generateCorrelationId()
)
```

### gRPC Service Usage
```kotlin
class OrganisationGrpcService(
    errorMapper: GrpcErrorMapper,
    metadataBuilder: GrpcMetadataBuilder,
    errorLogger: StructuredErrorLogger
) : GrpcServiceBase(errorMapper, metadataBuilder, errorLogger) {
    
    override suspend fun getOrganisation(request: GetOrganisationRequest): Organisation {
        return handleGrpcCall(
            operation = { organisationService.findById(request.id) },
            context = mapOf("organisationId" to request.id)
        )
    }
}
```

### REST Controller Usage
```kotlin
// Automatic handling via GlobalRestExceptionHandler
// No changes needed in controllers - exceptions are mapped automatically
```

This architecture distributes responsibilities properly while maintaining clean dependencies and enabling protocol-specific optimizations.

## Status: ✅ COMPLETED

All components have been implemented according to the architectural strategy:
- Core abstractions in `common-model` ✅
- REST error mapping in `server-rest` ✅  
- gRPC error mapping in `integration-grpc` ✅
- Platform utilities in `platform-logging` ✅
- Documentation updated ✅