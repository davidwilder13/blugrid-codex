# 🚀 Code Generation Project Summary

You're building a structured, modular **full-stack Kotlin/TypeScript mono-repo** code generator based on the **JHipster JDL** format. The goal is to automate generation of Kotlin-based backend APIs, database migrations, GraphQL schemas, and client libraries, targeting a robust and consistent monorepo architecture.

---

## 🎯 Project Goals

* **Automate boilerplate code** generation for backend and frontend.
* **Standardize structure** across multiple **core domains**, **business domains**, and **application domains**.
* Leverage a **strongly-typed, domain-driven approach**.
* Ensure a **dry, reusable, and scalable** code structure.
* Easily integrate new **domains**, **modules**, and **entities**.

---

## 🏁 Getting Started

### Prerequisites

- **Node.js** >=22.x
- **pnpm** (package manager)

### Installation

```bash
git clone https://github.com/<your-org>/jdl-codegen-tool.git
cd jdl-codegen-tool
pnpm install
```

### Placing JDL files

Place your JDL files in the `jdl/` directory at the project root. For example:

```bash
jdl/
└── my-domain.jdl
```

### Running the generator

```bash
pnpm run generate
```

All generated Kotlin modules will be written to the `output/` directory.

### Linking the CLI globally

To use the generator in any project, build and link it globally:

```bash
pnpm run link:global
```

In another project directory, invoke:

```bash
api-codegen generate
```

(Replace `api-codegen` with your package name if different.)

---

## 🗃️ Mono Repo Structure

```
core-api/
└── core-<domain>-api/
    ├── core-<domain>-api/                 # REST API & services
    │   ├── controller/                  
    │   ├── service/                     
    │   ├── mapper/                      
    │   └── config/                      
    ├── core-<domain>-api-model/           # DTOs & shared models
    ├── core-<domain>-api-db/              # JPA entities, repos, flyway
    ├── core-<domain>-api-client/          # REST Feign clients
    ├── core-<domain>-api-graphql/         # GraphQL schemas & resolvers
    ├── core-<domain>-api-grpc/            # gRPC server
    ├── core-<domain>-api-grpc-client/     # gRPC clients
    └── core-<domain>-api-grpc-proto/      # gRPC proto files
```

### 🧩 Layering Strategy

| Tier                    | Prefix              | Example Module                 |
| ----------------------- | ------------------- | ------------------------------ |
| Core domain (generated) | `core-`             | `core-organisation-api-model`  |
| Shared runtime (infra)  | `common-`           | `common-db`, `common-security` |
| Service/business logic  | `svc-`              | `svc-access-control-api`       |
| Workflow/process layer  | `process-`          | `process-onboarding-api`       |
| Product/vertical apps   | `app-` or `domain-` | `app-property-management-api`  |

---

## 📜 Specification Language (JDL)

JHipster Domain Language (JDL) files structured per domain, for example:

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

/** Entity definitions **/
entity Organisation {
  parentOrganisationId Long required
  effectiveTimestamp LocalDate required
}
```

* Separate files per domain/app.
* Custom annotations via the `other` keyword for extended behaviors.

---

## 📐 Generated Artifacts

### Kotlin Backend:

* ✅ **REST Controllers** (`OrganisationController.kt`)
* ✅ **Service Layer** (`OrganisationStateService.kt`)
* ✅ **DTO Models** (`OrganisationCreate.kt`, `OrganisationUpdate.kt`)
* ✅ **JPA Entities** (`OrganisationEntity.kt`)
* ✅ **Flyway migrations** (`V2024_04_01__create_organisation_table.kt`)
* ✅ **DB Table definitions** (`OrganisationTableDefinition.kt`)
* ✅ **GraphQL resolvers** (`OrganisationResolver.kt`)
* ✅ **GraphQL schemas** (`organisation.graphqls`)
* ✅ **REST Feign Clients** (`OrganisationApiClient.kt`)
* ✅ **gRPC clients (optional)** (`OrganisationGrpcClient.kt`)

### Frontend/Client (TypeScript):

* ✅ **TypeScript types** generated from models
* ✅ **REST or GraphQL clients** (future feature)
* ✅ UI components: forms, lists (future feature)

---

## ⚙️ Current Codebase Structure

```
codegen/
├── src/
│   ├── generate.ts                          # Main entrypoint
│   ├── load-entities.ts                     # JDL parsing & loaders
│   ├── config/
│   │   └── codegen-config.ts                # Project-wide config
│   ├── domain/                              # Domain-specific TypeScript models
│   │   ├── JdlModule.ts
│   │   ├── JdlEntity.ts
│   │   ├── JdlField.ts
│   │   └── JdlAnnotation.ts
│   ├── model/                               # TypeScript model representations
│   │   ├── ResourceModel.ts
│   │   ├── DatabaseTableModel.ts
│   │   └── KotlinModule.ts
│   ├── generators/                          # Code generation routines
│   │   └── kotlin/
│   │       ├── generateKotlinResources.ts
│   │       ├── generateDbMigrationFiles.ts
│   │       ├── generateCommonModuleFiles.ts
│   │       └── generateKotlinEntityFile.ts
│   ├── template-components/                 # React-style Mustache template components
│   │   ├── db/
│   │   │   ├── KotlinEntityTemplate.ts
│   │   │   ├── CreateTableSQLTemplate.ts
│   │   │   ├── CreateViewSQLTemplate.ts
│   │   │   └── migrations/
│   │   │       ├── CreateRepeatableMigrationTemplate.ts
│   │   │       └── CreateVersionedMigrationTemplate.ts
│   │   ├── model/
│   │   │   └── KotlinResourceTemplate.ts
│   │   └── common/
│   │       ├── GradleBuildFileTemplate.ts
│   │       └── GradlePropertiesTemplate.ts
│   └── utils/
│       ├── type-mappers.ts
│       ├── resolveFromProjectRoot.ts
│       └── toMustacheList.ts
├── templates/                               # Static Mustache scripts
│   └── kotlin/
│       └── common/
│           ├── gradlew.mustache
│           └── gradlew.bat.mustache
└── jdl/                                     # JDL files
    ├── core-organisation.jdl
    └── core-user.jdl
```

---
### Template Components Structure
React-style Mustache templates for code generation, encapsulated in strongly-typed TypeScript modules:

```
template-components/
├── db/
│   ├── KotlinEntityTemplate.ts
│   ├── CreateTableSQLTemplate.ts
│   └── CreateViewSQLTemplate.ts
├── model/
│   └── KotlinResourceTemplate.ts
└── common/
├── GradleBuildFileTemplate.ts
└── GradlePropertiesTemplate.ts
```
Each component:
- Accepts typed props.
- Returns rendered code strings for Mustache.

---
