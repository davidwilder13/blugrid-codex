# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a JDL-based code generation tool that creates a full-stack Kotlin/TypeScript monorepo from JHipster Domain Language (JDL) files. The generator produces production-ready backend APIs, database migrations, GraphQL schemas, gRPC services, and client libraries with a strongly-typed, domain-driven approach.

## Common Commands

### Code Generation
```bash
# Install dependencies
pnpm install

# Generate code from JDL files in jdl/ directory
pnpm run generate

# Generate and link Kotlin code
pnpm run generate:all
```

### Build Commands
```bash
# TypeScript/Generator
pnpm run build        # Compile TypeScript code generator

# Kotlin/Generated Code
./gradlew build       # Build all Kotlin modules
./gradlew clean build # Clean build
```

### Testing
```bash
# Run all Kotlin tests
./gradlew test

# Run tests for a specific module
./gradlew :<module-path>:test

# Run with detailed output
./gradlew test --info

# Note: No TypeScript tests are configured
```

### Code Quality
```bash
# Run Kotlin linter
./gradlew ktlint

# Auto-format Kotlin code
./gradlew ktlintFormat
```

## Architecture

### Directory Structure
- `codegen/`: TypeScript code generator implementation
  - `src/generators/`: Template-based code generators
  - `src/jdl/`: JDL parser and model
  - `src/mapper/`: Entity-to-template prop mappers
- `common/`: Shared Kotlin libraries and infrastructure
- `jdl/`: Input JDL files defining domain models
- `output/`: Generated code output directory
- `examples/`: Example generated projects

### Module Naming Conventions
- `core-<name>-api`: Generated core domain modules
- `common-<name>`: Shared runtime infrastructure
- `svc-<name>`: Service/business logic modules
- `process-<name>`: Workflow/process layer
- `app-<name>` or `domain-<name>`: Product verticals

### Template System
The generator uses Mustache templates with TypeScript wrappers:
- Each template has a corresponding `<Name>Props` interface
- Templates use `String.raw` for multi-line strings
- One rendering function per template file
- Templates must be deterministic and minimal in logic

### Custom JDL Annotations
- `@resourceType(UnscopedResource)`: Defines resource access patterns
- `@Auditable`: Enables audit fields (createdBy, updatedBy, etc.)
- `@dbDomain(<type>)`: Specifies custom PostgreSQL domain types
- `@Id`: Marks an ID field explicitly
- Custom annotations via `other` keyword

### Type Mapping
JDL types map to Kotlin and PostgreSQL types with support for custom database domains. Common mappings:
- `String` → `kotlin.String` → `text`
- `Integer` → `kotlin.Int` → `integer`
- `UUID` → `java.util.UUID` → `uuid`
- Custom types via `@dbDomain` annotation

## Development Workflow

1. Define domain models in JDL files under `jdl/`
2. Run `pnpm run generate` to generate code
3. Generated code appears in `output/` directory
4. Run `./gradlew build` to verify generated code compiles
5. Run `./gradlew ktlint` to ensure code style compliance

## Key Technical Details

- **Template Engine**: Mustache with TypeScript wrappers for type safety
- **Build Systems**: Gradle (Kotlin DSL) for Java/Kotlin, pnpm for TypeScript
- **Database**: PostgreSQL with Flyway migrations
- **API Styles**: REST, GraphQL, and gRPC support
- **Testing**: JUnit 5 for Kotlin, kotlin-faker for test data
- **Minimum Requirements**: Node.js >= 22, Kotlin 17 target

## Important Notes

- All module path resolution is centralized in `KotlinModule` class
- Templates should produce identical output given same props (deterministic)
- Generated code follows Spring Boot conventions
- Database migrations use Flyway versioning (V<version>__<description>.sql)
- GraphQL schemas are generated alongside REST APIs
- gRPC support includes protocol buffer definitions