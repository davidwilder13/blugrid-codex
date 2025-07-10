# 🚀 JDL-to-Kotlin Codegen Agent: Complete Developer & AI Agent Guide

Welcome to the **JDL-to-Kotlin Codegen** project—an autonomous code generation system that transforms JHipster Domain Language (JDL) models into production-ready, full-stack Kotlin microservices with TypeScript tooling.

## 🎯 Mission & Vision

**Mission**: Eliminate boilerplate and standardize architecture across domains by generating consistent, maintainable, and scalable Kotlin-based APIs from declarative JDL specifications.

**Vision**: Enable teams to focus on business logic while the agent handles infrastructure concerns, database schemas, REST APIs, GraphQL resolvers, and client libraries.

---

## 🏗️ System Architecture Overview

This is a **model-driven, multi-target code generator** built with TypeScript that produces:

- **Backend**: Kotlin microservices (Micronaut framework)
- **Database**: JPA entities, repositories, Flyway migrations, table definitions
- **API Layer**: REST controllers, service interfaces, gRPC services
- **Client Libraries**: gRPC clients, mapping services
- **Testing**: Unit test scaffolding and integration test setup

### Core Principles

1. **Domain-Driven Design**: Each domain becomes a self-contained module
2. **Separation of Concerns**: Clear layering between API, service, data, and client layers
3. **Type Safety**: Strong typing throughout the entire stack
4. **Evolutionary Architecture**: Easy to extend with new traits and patterns
5. **Production-Ready**: Generated code follows enterprise patterns and best practices

---

## 🗂️ Project Structure Deep Dive

```
jdl-codegen-tool/
├── codegen/                                    # TypeScript code generation engine
│   └── src/
│       ├── main.ts                            # 🚀 CLI entrypoint
│       ├── config/
│       │   └── codegen-config.ts              # 🔧 Global configuration
│       ├── generators/kotlin/                 # 🏭 Kotlin code generation engines
│       │   ├── file-generators/               # File-specific generators
│       │   │   ├── common/
│       │   │   │   ├── generateModuleFiles.ts # Gradle build files
│       │   │   │   └── index.ts
│       │   │   ├── db/
│       │   │   │   ├── generateKotlinDbMigrationFiles.ts
│       │   │   │   ├── generateKotlinEntityFile.ts
│       │   │   │   ├── generateKotlinGenericCrudRepositoryFile.ts
│       │   │   │   ├── generateKotlinMappingExtensionsFile.ts
│       │   │   │   ├── generateKotlinMappingServiceFile.ts
│       │   │   │   ├── generateKotlinStateServiceDbImplFile.ts
│       │   │   │   └── index.ts
│       │   │   ├── model/
│       │   │   │   ├── generateKotlinResources.ts     # DTO generation
│       │   │   │   ├── generateKotlinServiceInterfaceFile.ts
│       │   │   │   └── index.ts
│       │   │   └── index.ts
│       │   ├── model/
│       │   │   └── KotlinModule.ts            # Kotlin module abstraction
│       │   └── templates/                     # 🧩 Mustache template components
│       │       ├── api/service/
│       │       │   └── KotlinCrudServiceTemplate.ts
│       │       ├── common/
│       │       │   ├── GradleBuildFileTemplate.ts    # Gradle build scripts
│       │       │   ├── GradlePropertiesTemplate.ts   # Module properties
│       │       │   ├── gradlew.mustache
│       │       │   └── gradlew.bat.mustache
│       │       ├── db/
│       │       │   ├── mapping/
│       │       │   │   ├── KotlinMappingExtensionsTemplate.ts
│       │       │   │   └── KotlinMappingServiceTemplate.ts
│       │       │   ├── migrations/
│       │       │   │   ├── RepeatableViewMigrationTemplate.ts
│       │       │   │   └── VersionedMigrationTemplate.ts
│       │       │   ├── repository/
│       │       │   │   ├── KotlinEntityTemplate.ts    # JPA @Entity classes
│       │       │   │   └── KotlinGenericCrudRepositoryTemplate.ts
│       │       │   ├── service/
│       │       │   │   └── KotlinStateServiceDbImplTemplate.ts
│       │       │   └── sql/
│       │       │       ├── CreateTableSQLTemplate.ts  # DDL generation
│       │       │       ├── CreateViewSQLTemplate.ts   # Database views
│       │       │       └── index.ts
│       │       └── model/
│       │           ├── KotlinResourceTemplate.ts      # DTO & REST models
│       │           └── KotlinStateServiceInterfaceTemplate.ts
│       ├── jdl/                               # 📖 JDL parsing & loading
│       │   ├── load-entities.ts
│       │   ├── load-modules.ts
│       │   └── models/
│       │       ├── JdlEntity.ts               # Entity model with traits
│       │       ├── JdlEntityOption.ts         # JDL annotation options
│       │       ├── JdlModule.ts               # Module abstraction
│       │       ├── JdlModuleConfig.ts         # Module configuration
│       │       └── index.ts
│       ├── mapper/                            # 🎭 Model transformation
│       │   ├── JdlModuleToKotlinModule.ts     # JDL → Kotlin mapping
│       │   ├── JdlToCodegenEntityMapper.ts    # Entity transformation
│       │   └── index.ts
│       ├── model/                             # 🏗️ Intermediate models
│       │   ├── CodegenEntityFieldModel.ts     # Field definitions
│       │   ├── CodegenEntityModel.ts          # Entity model
│       │   └── index.ts
│       ├── types/
│       │   └── jhipster-core.d.ts             # JHipster type definitions
│       └── utils/                             # 🔧 Utilities
│           ├── commonjs-loader.ts
│           ├── resolveFromProjectRoot.ts
│           ├── resolve-template.ts
│           ├── to-mustache-list.ts
│           └── type-mappers/
│               ├── javadoc.ts
│               └── kotlin-type-imports.ts
├── common/common-kotlin/common-api/           # 🔧 Shared infrastructure modules
│   ├── common-api-audit/                     # Audit logging infrastructure
│   ├── common-api-client/                    # HTTP client utilities  
│   ├── common-api-domain/                    # Core domain types
│   ├── common-api-grpc/                      # gRPC common utilities
│   ├── common-api-grpc-proto/                # Proto definitions
│   ├── common-api-json/                      # JSON serialization
│   ├── common-api-jwt/                       # JWT utilities
│   ├── common-api-logging/                   # Logging configuration
│   ├── common-api-model/                     # Base model classes
│   ├── common-api-multitenant/               # Multi-tenancy support
│   ├── common-api-persistence/               # JPA/Hibernate base classes
│   ├── common-api-security/                  # Security & authentication
│   ├── common-api-test/                      # Testing utilities
│   └── common-api-web/                       # Web utilities
├── jdl/                                      # 📝 JDL domain specifications
│   └── core-organisation.jdl
├── output/                                   # 🎯 Generated Kotlin modules
├── examples/                                 # 🔍 Reference implementations
│   └── organisations/
│       ├── generated/core-organisation-api/   # Complete generated example
│       └── jdl/core-organisation.jdl         # Source JDL
├── docs/                                     # 📚 Documentation & guides
└── gradle/                                   # ⚙️ Gradle version catalog
    └── libs.versions.toml
```

---

## 🛠️ Technology Stack & Dependencies

### TypeScript Codegen Engine

Based on the `package.json`, our core dependencies:

```json
{
  "name": "@blugrid/api-codegen",
  "version": "1.0.0",
  "type": "module",
  "dependencies": {
    "commander": "^14.0.0",        // CLI framework for command parsing
    "fs-extra": "^11.3.0",        // Enhanced file system operations
    "jhipster-core": "^7.3.4",    // JDL parsing & AST generation
    "mustache": "^4.2.0"          // Logic-less template engine
  },
  "devDependencies": {
    "lodash-es": "^4.17.21",      // Functional utilities (ES modules)
    "typescript": "^5.8.3",       // TypeScript compiler
    "ts-node": "^10.9.2"          // TypeScript execution
  },
  "scripts": {
    "build": "tsc --project codegen/tsconfig.json",
    "generate": "node --import ./ts-register.mjs ./codegen/src/generate.ts",
    "generate:all": "pnpm run generate && ./scripts/link-generated-kotlin.sh"
  }
}
```

### Generated Kotlin Stack

Based on `gradle/libs.versions.toml`, the generated code uses:

```toml
[versions]
kotlin = "1.9.23"
micronaut = "4.4.3"
java = "17"
jackson = "2.17.2"
hibernate = "6.4.1.Final"
flyway = "10.22.0"
grpc = "1.62.2"
protobuf = "4.31.1"
mapstruct = "1.5.3.Final"

[bundles]
micronautCore = [
    "micronaut-core", "micronaut-inject", "micronaut-runtime",
    "micronaut-kotlin-runtime", "micronaut-kotlin-extensions"
]
micronautWeb = [
    "micronaut-http-server", "micronaut-http-client", 
    "micronaut-jackson", "micronaut-validation"
]
micronautData = [
    "micronaut-data-hibernate", "micronaut-hibernate-jpa",
    "micronaut-jdbc-hikari", "micronaut-flyway"
]
grpcCore = [
    "grpc-kotlin-stub", "grpc-protobuf", "grpc-stub",
    "protobuf-java", "protobuf-kotlin"
]
```

---

## 🚀 Getting Started

### Prerequisites

- **Node.js** ≥22.x (ES modules support)
- **pnpm** ≥8.x (package manager)
- **Java** 17+ (for generated Kotlin code)
- **Gradle** 8+ (for building generated modules)

### Quick Start

```bash
# Clone and setup
git clone <your-repo>
cd jdl-codegen-tool
pnpm install

# Place your JDL files
echo 'entity Organisation { name String required }' > jdl/my-domain.jdl

# Generate code
pnpm run generate

# Review output
ls -la output/core-organisation-api/
```

### Global CLI Installation

```bash
# Build and link globally
pnpm run build
pnpm link --global

# Use anywhere
cd /path/to/another/project
api-codegen generate --input ./my-domains.jdl
```

---

## 📝 JDL Specification Guide

### Basic Entity Definition

```jdl
application {
  config {
    baseName core_organisation_api
    packageName net.blugrid.core.organisation
    applicationType microservice
  }
  entities Organisation
  dto Organisation with mapstruct
  service Organisation with serviceClass
  paginate Organisation with pagination
  other Organisation with auditable, searchable, resourceType(UnscopedResource)
}

entity Organisation {
  parentOrganisationId Long required
  effectiveTimestamp LocalDate required
  name String required minlength(1) maxlength(255)
  description String maxlength(1000)
  isActive Boolean required
}
```

### Custom Traits via `other` Keyword

```jdl
other Organisation with auditable, searchable, resourceType(UnscopedResource)
other User with permissioned, cacheable, resourceType(ScopedResource)
```

**Supported Traits:**
- `auditable`: Adds `createdAt`, `updatedAt`, `createdBy`, `updatedBy`
- `searchable`: Enables full-text search capabilities
- `permissioned`: Adds access control fields
- `cacheable`: Enables caching annotations
- `resourceType(X)`: Defines resource scoping strategy

### Relationships

```jdl
relationship OneToMany {
  Organisation to User{organisation required}
}

relationship ManyToOne {
  User{role required} to Role
}
```

---

## 🏭 Generated Artifacts Overview

For each domain entity, the generator produces a complete module structure:

### Kotlin Backend Structure

```
output/core-organisation-api/
├── core-organisation-api/                    # Main API module (REST endpoints)
│   ├── build.gradle.kts
│   ├── gradle.properties
│   ├── gradlew, gradlew.bat
│   └── src/
│       ├── main/kotlin/net/blugrid/api/core/organisation/
│       │   ├── Application.kt                # Micronaut application
│       │   └── controller/
│       │       └── OrganisationController.kt # REST endpoints
│       └── test/
│           ├── kotlin/.../controller/
│           │   └── OrganisationControllerIntegTest.kt
│           └── resources/application-test.yml
├── core-organisation-api-model/              # DTOs & service interfaces
│   └── src/main/kotlin/net/blugrid/api/core/organisation/
│       ├── model/
│       │   ├── IOrganisation.kt             # Interface definition
│       │   ├── OrganisationCreate.kt        # Create DTO
│       │   ├── OrganisationResource.kt      # Response DTO
│       │   ├── OrganisationUpdate.kt        # Update DTO
│       │   └── OrganisationFilter.kt        # Query filter
│       └── service/
│           ├── OrganisationCommandService.kt # Command interface
│           └── OrganisationQueryService.kt   # Query interface
├── core-organisation-api-db/                 # Database layer
│   └── src/main/kotlin/net/blugrid/api/core/organisation/
│       ├── mapping/
│       │   ├── OrganisationMappingExtensions.kt # DTO ↔ Entity mapping
│       │   └── OrganisationMappingService.kt
│       ├── migration/
│       │   ├── R__5_table_organisation.kt    # Repeatable table migration
│       │   └── R__6_view_organisation.kt     # Database view migration
│       ├── repository/
│       │   ├── model/OrganisationEntity.kt   # JPA @Entity
│       │   ├── OrganisationRepository.kt     # CRUD repository
│       │   └── OrganisationSpecifications.kt # JPA Criteria queries
│       └── service/
│           ├── OrganisationCommandServiceDbImpl.kt # Command implementation
│           └── OrganisationQueryServiceDbImpl.kt   # Query implementation
├── core-organisation-api-grpc/               # gRPC server
│   └── src/main/kotlin/net/blugrid/api/core/organisation/grpc/
│       ├── Application.kt
│       ├── OrganisationGrpcService.kt        # gRPC service implementation
│       └── OrganisationGrpcMappingExtensions.kt
├── core-organisation-api-grpc-client/        # gRPC client
│   └── src/main/kotlin/net/blugrid/api/core/organisation/grpc/
│       ├── client/
│       │   ├── OrganisationGrpcClient.kt     # Client interface
│       │   ├── OrganisationGrpcClientFactory.kt
│       │   └── OrganisationCommandServiceGrpcClientImpl.kt
│       └── OrganisationProtoMappers.kt       # Proto ↔ Model mapping
├── core-organisation-api-grpc-proto/         # Protocol Buffers
│   └── src/main/proto/organisation.proto    # gRPC service definition
└── core-organisation-api-test/               # Test utilities
    └── src/main/kotlin/net/blugrid/api/core/organisation/
        ├── assertion/OrganisationAssertions.kt
        └── factory/OrganisationTestFactory.kt
```

### Sample Generated Files

#### REST Controller
```kotlin
@Controller("/api/v1/organisations")
@Validated
class OrganisationController(
    private val organisationService: OrganisationStateService
) {
    
    @Get("/{id}")
    fun getById(@PathVariable id: Long): OrganisationResponse {
        return organisationService.findById(id)
    }
    
    @Post
    fun create(@Valid @Body request: OrganisationCreate): OrganisationResponse {
        return organisationService.create(request)
    }
    
    @Put("/{id}")
    fun update(
        @PathVariable id: Long, 
        @Valid @Body request: OrganisationUpdate
    ): OrganisationResponse {
        return organisationService.update(id, request)
    }
}
```

#### JPA Entity
```kotlin
@Entity
@Table(name = "organisation")
class OrganisationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organisation_seq")
    @SequenceGenerator(name = "organisation_seq", sequenceName = "organisation_seq", allocationSize = 1)
    val id: Long? = null,
    
    @Column(name = "parent_organisation_id")
    val parentOrganisationId: Long?,
    
    @Column(name = "effective_timestamp", nullable = false)
    val effectiveTimestamp: LocalDate,
    
    @Column(name = "name", nullable = false, length = 255)
    val name: String
) : UnscopedPersistable, AuditablePersistable
```

#### Service Interface
```kotlin
interface OrganisationStateService {
    fun findById(id: Long): OrganisationResource
    fun create(request: OrganisationCreate): OrganisationResource
    fun update(id: Long, request: OrganisationUpdate): OrganisationResource
    fun delete(id: Long)
    fun findAll(filter: OrganisationFilter, page: PageRequest): Page<OrganisationResource>
}
```

---

## 🧠 Agentic Development Workflow

### 1. 🔍 Discovery Phase

The agent analyzes JDL input through structured loaders:

```typescript
// jdl/load-entities.ts
export function loadEntitiesFromJdl(jdlContent: string): JdlModule[] {
  const jdlObject = JDLReader.parse(jdlContent)
  return jdlObject.entities.map(entity => ({
    name: entity.name,
    fields: extractFields(entity),
    options: extractEntityOptions(entity),  // @Auditable, @Searchable, etc.
    relationships: extractRelationships(entity)
  }))
}

// jdl/models/JdlEntity.ts
export interface JdlEntity {
  name: string
  fields: JdlEntityField[]
  options: JdlEntityOption[]
  relationships: JdlRelationship[]
}
```

### 2. 📋 Planning Phase

Transforms JDL models into generation targets:

```typescript
// mapper/JdlModuleToKotlinModule.ts
export function mapJdlModuleToKotlinModule(jdlModule: JdlModule): KotlinModule {
  return {
    moduleName: jdlModule.config.baseName,
    packageName: jdlModule.config.packageName,
    entities: jdlModule.entities.map(mapJdlEntityToCodegenEntity),
    subModules: [
      'core-api',           // REST controllers & main app
      'core-api-model',     // DTOs & service interfaces  
      'core-api-db',        // JPA entities & repositories
      'core-api-grpc',      // gRPC server implementation
      'core-api-grpc-client', // gRPC client
      'core-api-grpc-proto',  // Protocol buffer definitions
      'core-api-test'       // Test factories & assertions
    ]
  }
}
```

### 3. 🏗️ Build Phase (Iterative)

Uses modular file generators for each artifact type:

```typescript
// generators/kotlin/file-generators/db/generateKotlinEntityFile.ts
export async function generateKotlinEntityFile(
  entity: CodegenEntityModel,
  module: KotlinModule
): Promise<void> {
  const template = new KotlinEntityTemplate({
    className: `${entity.name}Entity`,
    packageName: `${module.packageName}.repository.model`,
    fields: entity.fields,
    isAuditable: entity.hasOption('auditable'),
    resourceType: entity.getResourceType()
  })
  
  await writeFile(
    `${module.outputPath}/core-${entity.kebabName}-api-db/src/main/kotlin/${packagePath}/${entity.name}Entity.kt`,
    template.render()
  )
}
```

### 4. ✅ Validation Phase

Multi-layered verification approach:

```typescript
// Template validation through examples
describe('Generated Organisation Module', () => {
  it('should match reference implementation', () => {
    const jdlContent = fs.readFileSync('examples/organisations/jdl/core-organisation.jdl', 'utf8')
    const generated = generateFromJdl(jdlContent)
    const expected = fs.readFileSync('examples/organisations/generated/core-organisation-api/core-organisation-api-model/src/main/kotlin/net/blugrid/api/core/organisation/model/OrganisationResource.kt', 'utf8')
    
    expect(normalizeWhitespace(generated.model.resources.Organisation)).toBe(normalizeWhitespace(expected))
  })
})
```

---

## 🔧 Developer Workflows

### Adding a New Entity Trait

**Example: Adding `@Versionable` trait for optimistic locking**

#### Step 1: Update JDL Models

```typescript
// jdl/models/JdlEntityOption.ts
export interface JdlEntityOption {
  name: 'auditable' | 'searchable' | 'permissioned' | 'versionable'
  parameters?: Record<string, any>
}

// mapper/JdlToCodegenEntityMapper.ts
export function mapJdlEntityToCodegenEntity(jdlEntity: JdlEntity): CodegenEntityModel {
  return {
    name: jdlEntity.name,
    fields: jdlEntity.fields.map(mapField),
    options: {
      isAuditable: hasOption(jdlEntity, 'auditable'),
      isSearchable: hasOption(jdlEntity, 'searchable'),
      isVersionable: hasOption(jdlEntity, 'versionable'), // NEW
      resourceType: getResourceType(jdlEntity)
    }
  }
}
```

#### Step 2: Update Entity Template

```typescript
// generators/kotlin/templates/db/repository/KotlinEntityTemplate.ts
export class KotlinEntityTemplate {
  constructor(private props: {
    className: string
    packageName: string
    fields: EntityField[]
    isAuditable: boolean
    isVersionable: boolean  // NEW
    resourceType: string
  }) {}

  render(): string {
    return mustache.render(`
@Entity
@Table(name = "{{tableName}}")
class {{className}}(
{{#fields}}
    @Column(name = "{{columnName}}")
    val {{name}}: {{type}}{{^isLast}},{{/isLast}}
{{/fields}}
{{#isVersionable}}
    @Version
    val version: Long = 0
{{/isVersionable}}
) {{#isAuditable}}: AuditableEntity{{/isAuditable}}
`, this.props)
  }
}
```

#### Step 3: Update File Generator

```typescript
// generators/kotlin/file-generators/db/generateKotlinEntityFile.ts
export async function generateKotlinEntityFile(
  entity: CodegenEntityModel,
  module: KotlinModule
): Promise<void> {
  const template = new KotlinEntityTemplate({
    className: `${entity.name}Entity`,
    packageName: `${module.packageName}.repository.model`,
    fields: entity.fields,
    isAuditable: entity.options.isAuditable,
    isVersionable: entity.options.isVersionable,  // NEW
    resourceType: entity.options.resourceType
  })
  
  await writeFile(getEntityPath(entity, module), template.render())
}
```

#### Step 4: Add Example & Test

```jdl
// examples/organisations/jdl/test-versionable.jdl
entity Product {
  name String required
  price BigDecimal required
}
other Product with versionable
```

#### Step 5: Document Changes

```markdown
# docs/changelog/2024-07-10__add-versionable-trait.md

## 🧠 Feature: Support for @Versionable Trait

### 🔍 Summary
Enable optimistic locking via JPA @Version annotation.

### 🧭 Intent
Prevent concurrent modification issues in multi-user scenarios.

### ⚒️ Steps
- Updated `JdlEntityOption` to include `versionable`
- Added `isVersionable` to `CodegenEntityModel`
- Modified `KotlinEntityTemplate` to include @Version field
- Updated `generateKotlinEntityFile` to pass version flag

### 🔎 Verification
- Example: `examples/organisations/jdl/test-versionable.jdl`
- Output: Generates @Version field in entity classes
```

---

## 🧪 Testing Strategies

### 1. Template Snapshot Testing

Ensures consistent output across changes:

```typescript
// tests/template-components/
describe('KotlinEntityTemplate', () => {
  it('should match snapshots for all entity variations', () => {
    const variations = [
      { name: 'simple', options: {} },
      { name: 'auditable', options: { isAuditable: true } },
      { name: 'searchable', options: { isSearchable: true } },
      { name: 'versionable', options: { isVersionable: true } },
      { name: 'complex', options: { isAuditable: true, isSearchable: true, isVersionable: true } }
    ]
    
    variations.forEach(({ name, options }) => {
      const result = new KotlinEntityTemplate({ ...baseProps, ...options }).render()
      expect(result).toMatchSnapshot(`entity-${name}`)
    })
  })
})
```

### 2. Integration Testing

Validates end-to-end generation:

```bash
# Generate test domains and verify they compile
pnpm run generate
cd output/core-organisation-api && gradle build test
```

### 3. Example-Based Contract Testing

Uses `examples/` as golden reference:

```typescript
// tests/contract/
it('generated output matches examples', () => {
  const jdlContent = fs.readFileSync('jdl/core-organisation.jdl', 'utf8')
  const generated = generateFromJdl(jdlContent)
  const expected = fs.readFileSync('examples/organisations/generated/core-organisation-api/core-organisation-api-model/src/main/kotlin/net/blugrid/api/core/organisation/model/OrganisationResource.kt', 'utf8')
  
  expect(normalizeWhitespace(generated.model.OrganisationResource)).toBe(normalizeWhitespace(expected))
})
```

---

## 🎯 Best Practices for AI Agents

### Code Generation Philosophy

1. **Fail Fast on Ambiguity**: Never generate code from incomplete or unclear JDL
2. **Validate Before Generate**: Check for naming conflicts, missing relationships
3. **Incremental Generation**: Support partial regeneration of modified entities
4. **Idempotent Output**: Multiple runs should produce identical results

### Template Design Patterns

```typescript
// ✅ Good: Strongly typed template props
interface EntityTemplateProps {
  className: string
  packageName: string
  fields: EntityField[]
  options: EntityOptions
}

class KotlinEntityTemplate {
  constructor(private props: EntityTemplateProps) {}
  render(): string { /* ... */ }
}

// ❌ Bad: Loosely typed props
function template(props: any): string

// ✅ Good: Composable sub-templates
function renderFields(fields: EntityField[]): string
function renderAnnotations(options: EntityOptions): string

// ❌ Bad: Monolithic templates
function renderEverything(entity: any): string
```

### Error Handling Strategy

```typescript
export class CodegenError extends Error {
  constructor(
    message: string,
    public readonly phase: 'parse' | 'map' | 'generate' | 'validate',
    public readonly entity?: string,
    public readonly field?: string
  ) {
    super(`[${phase}] ${entity ? `${entity}.${field}: ` : ''}${message}`)
  }
}

// Usage in generators
if (!entity.name) {
  throw new CodegenError('Entity name is required', 'parse', entity.name)
}
```

### File Organization Patterns

```typescript
// ✅ Good: Organized by responsibility
generators/kotlin/
├── file-generators/          # What files to generate
│   ├── common/              # Gradle, properties files
│   ├── db/                  # Database-related files
│   └── model/               # DTOs and interfaces
├── templates/               # How to generate them
│   ├── db/repository/       # Entity templates
│   ├── db/migrations/       # SQL migration templates
│   └── model/               # DTO templates
└── model/                   # Data structures
    └── KotlinModule.ts

// ❌ Bad: Mixed concerns
generators/
├── everything-in-one-folder/
```

---

## 🚦 Common Infrastructure Integration

The generated modules integrate seamlessly with the shared infrastructure:

### Base Classes from `common-api-model`

```kotlin
// Generated entities extend base classes
class OrganisationEntity : UnscopedPersistable, AuditablePersistable

// Generated DTOs extend base resources  
data class OrganisationResource : BaseAuditedResource
data class OrganisationCreate : BaseCreateResource
```

### Security Context from `common-api-security`

```kotlin
// Generated services use security context
@Service
class OrganisationQueryServiceDbImpl(
    private val repository: OrganisationRepository,
    private val contextProvider: RequestContextProvider
) : OrganisationQueryService {
    
    fun findAll(filter: OrganisationFilter): List<OrganisationResource> {
        val context = contextProvider.getCurrentContext()
        return repository.findAllByScope(context.scope, filter)
    }
}
```

### Database Utilities from `common-api-persistence`

```kotlin
// Generated repositories extend common base
interface OrganisationRepository : GenericEntityRepository<OrganisationEntity, Long> {
    fun findByNameContaining(name: String): List<OrganisationEntity>
}
```

---

## 🛣️ Roadmap & Extension Points

### Near-term Enhancements

1. **Complete gRPC Generation**: Full proto, server, and client generation
2. **Advanced Relationships**: Complex many-to-many with join tables
3. **Event Sourcing**: CQRS patterns and event stores
4. **TypeScript Client Generation**: Type-safe API clients for frontend

### Long-term Vision

1. **Multi-language Targets**: Generate Go, C#, Python services
2. **UI Component Generation**: React forms and data tables
3. **Cloud-Native Deployment**: Kubernetes manifests and Helm charts
4. **Advanced Testing**: Generate comprehensive test suites

### Extension Architecture

```typescript
// Future plugin system
interface CodegenPlugin {
  name: string
  targets: ('entity' | 'controller' | 'service' | 'repository')[]
  generate(entity: CodegenEntityModel, context: GenerationContext): Promise<GeneratedFile[]>
}

// Usage
const plugins = [
  new SwaggerDocPlugin(),
  new EventSourcingPlugin(),
  new ReactFormPlugin()
]
```

---

## 🤝 Contributing Guidelines

### For Human Developers

1. **Start with Examples**: Create hand-written examples in `examples/` first
2. **Test-Driven Development**: Write tests before implementation
3. **Document Changes**: Use structured changelog format in `docs/`
4. **Incremental PRs**: Small, focused changes with clear intent

### For AI Agents

1. **Understand Before Modify**: Analyze existing patterns before suggesting changes
2. **Preserve Consistency**: Maintain naming conventions and architectural patterns
3. **Validate Output**: Always check generated code compiles and passes tests
4. **Update Documentation**: Reflect changes in README and changelogs

### Code Review Checklist

- [ ] JDL changes are backward compatible
- [ ] Generated code follows Kotlin/Micronaut conventions
- [ ] Templates are strongly typed with proper interfaces
- [ ] Tests cover new functionality with snapshots
- [ ] Examples demonstrate new features
- [ ] Changelog documents intent and verification steps
- [ ] Integration with `common-api-*` modules maintained
