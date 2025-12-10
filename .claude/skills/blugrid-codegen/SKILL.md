---
name: blugrid-codegen
description: Generate Kotlin backend modules from JDL, natural language, or design docs. Produces REST APIs, database migrations, GraphQL, and gRPC services.
---

# Blugrid Code Generator

## Overview

Generate production-ready Kotlin backend code from various inputs:
- JDL (JHipster Domain Language) files
- Natural language descriptions
- Design documents
- Existing OpenAPI schemas

All inputs are normalized to OpenAPI 3.x with custom extensions, then processed by hierarchical Python skills.

## Quick Start

**From JDL:**
```bash
# Parse JDL to JSON
python parsers/jdl-parser.py --input design.jdl --output /tmp/parsed.json

# Claude enriches to OpenAPI (done by Claude)

# Generate complete module
python orchestrators/generate-module.py --spec intermediate.yaml --output ./output/
```

**From natural language:**
Describe your entities to Claude. Claude will generate the OpenAPI spec, then invoke the generation skills.

## Skill Hierarchy

```
Claude (orchestrates)
  ↓
orchestrators/generate-module.py (entry point)
  ↓
layers/generate_model_layer.py (DTOs, resources, services)
layers/generate_db_layer.py (entities, repositories, migrations, mappings)
layers/generate_rest_layer.py (controllers, application, configs)
layers/generate_test_layer.py (test factories, assertions)
layers/generate_grpc_layer.py (gRPC proto, server, client modules)
  ↓
atomic/kotlin/generate_resource.py
atomic/kotlin/generate_entity.py
atomic/kotlin/generate_repository.py
atomic/kotlin/generate_specifications.py
atomic/kotlin/generate_mapping_service.py
atomic/kotlin/generate_mapping_extensions.py
atomic/kotlin/generate_query_service_db_impl.py
atomic/kotlin/generate_command_service_db_impl.py
atomic/kotlin/generate_db_migration.py
atomic/kotlin/generate_db_integration_test.py
atomic/kotlin/generate_controller.py
atomic/kotlin/generate_application.py
atomic/kotlin/generate_controller_test.py
atomic/kotlin/generate_test_factory.py
atomic/kotlin/generate_assertions.py
atomic/kotlin/generate_grpc_service.py
atomic/kotlin/generate_grpc_mapping.py
atomic/kotlin/generate_grpc_client.py
atomic/proto/generate_proto.py
  ↓
templates/kotlin/*.j2
templates/proto/*.j2
templates/config/*.j2
templates/gradle/*.j2
```

## OpenAPI Extensions

Custom `x-` extensions for code generation:

| Extension | Level | Description |
|-----------|-------|-------------|
| `x-base-package` | info | Base package name |
| `x-module-name` | info | Module name |
| `x-model-module-path` | info | Path to model module dependency |
| `x-test-module-path` | info | Path to test module dependency |
| `x-db-module-path` | info | Path to DB module dependency |
| `x-resource-type` | schema | UnscopedResource, TenantResource, etc. |
| `x-auditable` | schema | Add audit fields |
| `x-db-table` | schema | Override table name |
| `x-db-domain` | property | PostgreSQL domain type |
| `x-kotlin-type` | property | Override Kotlin type |
| `x-generated` | property | Auto-generated field (id, uuid) |

## Escalation Pattern

When templates can't handle something, they inject markers:

```kotlin
// ESCALATE: Unknown type mapping
// Field: customField
// Source type: string (format: custom)
// Context: A field with custom logic
// END_ESCALATE
```

Claude scans output, fills in custom code, patches files.

## Testing

Three-stage pipeline:

```bash
python tests/run-tests.py --fixture fixtures/core-organisation.yaml
```

1. **Golden diff** - Compare against expected output
2. **Build** - Run `./gradlew build`
3. **Tests** - Run `./gradlew test`

## DB Layer Generation

The DB layer generator produces a complete database access module:

```bash
python layers/generate_db_layer.py --spec openapi.yaml --output ./output/core-entity-api-db/
```

**Generated files:**
- `src/main/kotlin/<package>/repository/model/<Entity>Entity.kt` - JPA entity with audit support
- `src/main/kotlin/<package>/repository/<Entity>Repository.kt` - Micronaut Data repository interface
- `src/main/kotlin/<package>/repository/<Entity>Specifications.kt` - JPA Specification builders for filtering
- `src/main/kotlin/<package>/mapping/<Entity>MappingService.kt` - Entity <-> Resource mapper service
- `src/main/kotlin/<package>/mapping/<Entity>MappingExtensions.kt` - Extension functions for mapping
- `src/main/kotlin/<package>/service/<Entity>QueryServiceDbImpl.kt` - Query service implementation
- `src/main/kotlin/<package>/service/<Entity>CommandServiceDbImpl.kt` - Command service implementation
- `src/main/kotlin/<package>/migration/R__5_table_<entity>.kt` - Kotlin-based Flyway table migration
- `src/main/kotlin/<package>/migration/R__6_view_<entity>.kt` - Kotlin-based Flyway view migration
- `src/test/kotlin/<package>/service/<Entity>StateServiceDbImplIntegTest.kt` - Integration tests
- `src/test/resources/logback.xml` - Test logging configuration
- `build.gradle.kts` - Gradle build file with Micronaut Data dependencies
- `gradle.properties` - Version properties

**Entity features:**
- JPA annotations with view-based table mapping (`vw_<entity>`)
- Global tenant sequence generator for IDs
- Embedded audit fields with `@PrePersist` and `@PreUpdate` hooks
- Implements `UnscopedPersistable`, `TenantPersistable`, etc. based on resource type
- Custom `kotlinEquals` for proper equality checking

**Repository features:**
- Extends `GenericEntityRepository` with CRUD operations
- JPA Specification support for complex filtering
- ID and UUID-based lookups

**Service features:**
- Query service with pagination and filtering via specifications
- Command service for create, update, delete operations
- Integration with mapping service for entity <-> resource conversion

**Migration features:**
- Kotlin-based Flyway migrations (repeatable)
- Table creation with inheritance from common columns
- View creation with insert/delete triggers
- Automatic audit column triggers

## REST Layer Generation

The REST layer generator produces a complete Micronaut REST API module:

```bash
python layers/generate_rest_layer.py --spec openapi.yaml --output ./output/core-entity-api/
```

**Generated files:**
- `src/main/kotlin/<package>/Application.kt` - Micronaut entry point with OpenAPI annotations
- `src/main/kotlin/<package>/controller/<Entity>Controller.kt` - REST controller with CRUD operations
- `src/main/resources/application.yml` - Micronaut configuration
- `src/test/kotlin/<package>/controller/<Entity>ControllerIntegTest.kt` - Integration tests
- `src/test/resources/application-test.yml` - Test configuration
- `src/test/resources/logback.xml` - Logging configuration
- `build.gradle.kts` - Gradle build file
- `gradle.properties` - Version properties

**Controller features:**
- Full CRUD operations (create, update, delete, getById, getAll)
- Pagination support via `/page` endpoint
- UUID-based lookups via `/uuid/{uuid}`
- Filter-based queries via POST `/query`
- OpenAPI/Swagger annotations
- Service layer delegation

## Test Layer Generation

The test layer generator produces test utilities required for integration tests:

```bash
python layers/generate_test_layer.py --spec openapi.yaml --output ./output/core-entity-api-test/
```

**Generated files:**
- `src/main/kotlin/<package>/factory/<Entity>TestFactory.kt` - Test factories for Create, Update, and Resource models
- `src/main/kotlin/<package>/assertion/<Entity>Assertions.kt` - Assertion utilities for testing
- `build.gradle.kts` - Gradle build file
- `gradle.properties` - Version properties

**Test Factory features:**
- `<Entity>CreateFactory` - Factory for creating test instances with random/default values
- `<Entity>UpdateFactory` - Factory for update models
- `<Entity>Factory` - Factory for resource models

**Assertions features:**
- `<Entity>.assert(id, uuid, ...)` - Assert specific field values
- `<Entity>.assertEqualTo(expected)` - Assert equality with another instance

## gRPC Layer Generation

The gRPC layer generator produces three complete modules for gRPC communication:

```bash
python layers/generate_grpc_layer.py --spec openapi.yaml --output ./output/core-entity-api/
```

This generates three modules:
1. **grpc-proto** - Protocol Buffer definitions
2. **grpc** - gRPC server implementation
3. **grpc-client** - gRPC client library

### Module 1: gRPC Proto (`-grpc-proto`)

**Generated files:**
- `src/main/proto/<entity>.proto` - Protocol Buffer service definition with CRUD operations
- `build.gradle.kts` - Gradle build with protobuf plugin
- `gradle.properties` - Version properties

### Module 2: gRPC Server (`-grpc`)

**Generated files:**
- `src/main/kotlin/<package>/Application.kt` - Micronaut gRPC server entry point
- `src/main/kotlin/<package>/grpc/<Entity>GrpcService.kt` - gRPC service implementation
- `src/main/kotlin/<package>/grpc/<Entity>GrpcMappingExtensions.kt` - Domain <-> Proto mapping
- `src/main/kotlin/<package>/grpc/GrpcContextConfiguration.kt` - Coroutine dispatcher config
- `src/main/resources/application.yml` - Server configuration
- `Dockerfile` - Container configuration
- `build.gradle.kts` - Gradle build file
- `gradle.properties` - Version properties

### Module 3: gRPC Client (`-grpc-client`)

**Generated files:**
- `src/main/kotlin/<package>/grpc/<Entity>GrpcClient.kt` - Client wrapper class
- `src/main/kotlin/<package>/grpc/<Entity>GrpcClientFactory.kt` - Factory with service discovery
- `src/main/kotlin/<package>/grpc/<Entity>CommandServiceGrpcClientImpl.kt` - CommandService implementation
- `src/main/kotlin/<package>/grpc/<Entity>ProtoMappers.kt` - Client-side proto mappers
- `src/test/kotlin/<package>/grpc/<Entity>GrpcResponseAssertions.kt` - Response assertions for testing
- `build.gradle.kts` - Gradle build file
- `gradle.properties` - Version properties

## Directory Structure

```
.claude/skills/blugrid-codegen/
├── SKILL.md              # This file
├── config/               # Type mappings, settings
├── parsers/              # JDL parser, OpenAPI validator
├── orchestrators/        # Entry points
├── layers/               # Layer-level generation
│   ├── generate_model_layer.py
│   ├── generate_db_layer.py
│   ├── generate_rest_layer.py
│   ├── generate_test_layer.py
│   └── generate_grpc_layer.py
├── atomic/               # Single-file generators
│   ├── kotlin/
│   │   ├── generate_resource.py
│   │   ├── generate_entity.py
│   │   ├── generate_repository.py
│   │   ├── generate_specifications.py
│   │   ├── generate_mapping_service.py
│   │   ├── generate_mapping_extensions.py
│   │   ├── generate_query_service_db_impl.py
│   │   ├── generate_command_service_db_impl.py
│   │   ├── generate_db_migration.py
│   │   ├── generate_db_integration_test.py
│   │   ├── generate_controller.py
│   │   ├── generate_application.py
│   │   ├── generate_controller_test.py
│   │   ├── generate_test_factory.py
│   │   ├── generate_assertions.py
│   │   ├── generate_grpc_service.py
│   │   ├── generate_grpc_mapping.py
│   │   └── generate_grpc_client.py
│   ├── proto/
│   │   └── generate_proto.py
│   └── sql/
│       └── generate_table.py
├── templates/            # Jinja2 templates
│   ├── kotlin/
│   │   ├── resource.kt.j2
│   │   ├── entity.kt.j2
│   │   ├── repository.kt.j2
│   │   ├── specifications.kt.j2
│   │   ├── mapping_service.kt.j2
│   │   ├── mapping_extensions.kt.j2
│   │   ├── query_service_db_impl.kt.j2
│   │   ├── command_service_db_impl.kt.j2
│   │   ├── db_migration_table.kt.j2
│   │   ├── db_migration_view.kt.j2
│   │   ├── db_integration_test.kt.j2
│   │   ├── controller.kt.j2
│   │   ├── application.kt.j2
│   │   ├── controller_test.kt.j2
│   │   ├── test_factory.kt.j2
│   │   ├── assertions.kt.j2
│   │   ├── grpc_service.kt.j2
│   │   ├── grpc_mapping_extensions.kt.j2
│   │   ├── grpc_application.kt.j2
│   │   ├── grpc_context_config.kt.j2
│   │   ├── grpc_client.kt.j2
│   │   ├── grpc_client_factory.kt.j2
│   │   ├── grpc_service_impl.kt.j2
│   │   ├── grpc_proto_mappers.kt.j2
│   │   └── grpc_response_assertions.kt.j2
│   ├── proto/
│   │   └── service.proto.j2
│   ├── config/
│   │   ├── application.yml.j2
│   │   ├── application-test.yml.j2
│   │   ├── logback.xml.j2
│   │   └── logback-test.xml.j2
│   ├── gradle/
│   │   ├── build.gradle.kts.j2
│   │   ├── build.gradle.kts.db.j2
│   │   ├── build.gradle.kts.test.j2
│   │   ├── build.gradle.kts.grpc-proto.j2
│   │   ├── build.gradle.kts.grpc.j2
│   │   ├── build.gradle.kts.grpc-client.j2
│   │   └── gradle.properties.j2
│   └── sql/
│       └── table.sql.j2
├── utils/                # Shared utilities
└── tests/                # Test fixtures and runner
```
