# Claude Skills Migration Design

> **Date:** 2025-12-08
> **Status:** Approved
> **Priority Rankings:** Token Efficiency > LLM-native Workflow > Extensibility

## Overview

Migrate the TypeScript-based JDL code generator to a Claude skills/agents ecosystem where:
- Deterministic Python scripts handle known patterns (token efficient)
- Claude orchestrates skills and handles edge cases via escalation
- OpenAPI 3.x serves as the intermediate model between input and generation
- Any input (JDL, natural language, design docs) converges to OpenAPI

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         INPUT LAYER                             │
│  JDL files │ Natural language │ Design docs │ Existing schemas  │
└─────────────────────────────┬───────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    CLAUDE (Intent Interpreter)                  │
│  • Understands any input format                                 │
│  • Enriches with context, infers intent                         │
│  • Outputs standardized OpenAPI + x-extensions                  │
│  • Fills ESCALATE placeholders in generated code                │
└─────────────────────────────┬───────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                 INTERMEDIATE MODEL (OpenAPI 3.x)                │
│  • Standard schemas, paths, components                          │
│  • x-db-*, x-grpc-*, x-graphql-* extensions                    │
│  • x-deployment-* (future)                                      │
└─────────────────────────────┬───────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    SKILL HIERARCHY                              │
│  Orchestrator ──→ Layer Skills ──→ Atomic Skills ──→ Templates │
│  (Python)          (Python)         (Python)        (Jinja2)    │
└─────────────────────────────┬───────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      OUTPUT LAYER                               │
│  Kotlin │ SQL │ GraphQL │ gRPC │ TypeScript │ React │ Mobile   │
└─────────────────────────────────────────────────────────────────┘
```

## Key Decisions

### 1. Token Efficiency Strategy: Hybrid with Escalation
- Scripts handle known patterns deterministically (80% case)
- When templates can't handle something, inject `ESCALATE:` markers
- Claude fills in custom code in a second pass

### 2. Intermediate Model: OpenAPI 3.x with Extensions
- Industry standard, extensive tooling
- Custom `x-` extensions for code generation metadata:
  - `x-db-*` - Database generation
  - `x-kotlin-*` - Kotlin-specific overrides
  - `x-grpc-*` - gRPC configuration
  - `x-graphql-*` - GraphQL hints
  - `x-resource-*` - Resource scoping
  - `x-deployment-*` - Future deployment config

### 3. Implementation Language: Python + Jinja2
- Better alignment with Claude/AI ecosystem
- Simpler scripting model for skills
- Jinja2 provides powerful templating (inheritance, macros, filters)

### 4. Skill Invocation: Standalone Python Scripts
- Claude invokes via Bash: `python skill.py --input ... --output ...`
- Extensible to MCP or CLI packaging later
- Scripts are stateless, run to completion

### 5. Skill Organization: Hierarchical
```
Claude
  ↓ invokes
Orchestrator Skill (generate-module.py)
  ↓ calls
Layer Skills (generate-db-layer.py, generate-api-layer.py)
  ↓ calls
Atomic Skills (generate-entity.py, generate-migration.py)
  ↓ uses
Jinja2 Templates
```

### 6. JDL Parsing: Keep jhipster-core
- Deterministic parsing via existing library
- Output JSON, Claude enriches to OpenAPI
- Token efficient - parser handles syntax, Claude handles semantics

### 7. Testing: Three-Stage Pipeline
1. **Golden file diff** - git diff against human-maintained examples
2. **Build verification** - `./gradlew build` on generated code
3. **Generated test execution** - Run tests that skills also generate

## Directory Structure

```
.claude/skills/blugrid-codegen/
├── SKILL.md                          # Main skill manifest
├── config/
│   └── type-mappings.yaml            # JDL→Kotlin→SQL type maps
│
├── parsers/                          # Input parsers (deterministic)
│   ├── jdl-parser.py                 # JDL → JSON (wraps jhipster-core)
│   └── openapi-validator.py          # Validates intermediate model
│
├── orchestrators/                    # High-level entry points
│   ├── generate-module.py            # Full module generation
│   └── generate-from-input.py        # Routes any input → OpenAPI → generation
│
├── layers/                           # Layer-level skills
│   ├── generate-model-layer.py       # DTOs, resources, interfaces
│   ├── generate-db-layer.py          # Entities, repos, migrations, mappings
│   ├── generate-api-layer.py         # Controllers, services
│   ├── generate-grpc-layer.py        # Proto, server, client
│   └── generate-graphql-layer.py     # Schema, resolvers
│
├── atomic/                           # Single-file generators
│   ├── kotlin/
│   │   ├── generate-resource.py
│   │   ├── generate-entity.py
│   │   ├── generate-repository.py
│   │   ├── generate-mapping-service.py
│   │   └── generate-crud-service.py
│   ├── sql/
│   │   ├── generate-table.py
│   │   ├── generate-view.py
│   │   └── generate-migration.py
│   └── gradle/
│       └── generate-build-files.py
│
├── templates/                        # Jinja2 templates
│   ├── kotlin/
│   │   ├── resource.kt.j2
│   │   ├── entity.kt.j2
│   │   └── ...
│   ├── sql/
│   │   ├── table.sql.j2
│   │   └── ...
│   └── gradle/
│       └── build.gradle.kts.j2
│
└── tests/
    ├── golden/                       # Expected output snapshots
    ├── fixtures/                     # Test input files
    └── run-tests.py                  # Test runner
```

## OpenAPI Intermediate Model Example

```yaml
openapi: 3.0.3
info:
  title: core-organisation-api
  version: 0.1.0
  x-base-package: net.blugrid.core.organisation
  x-group: net.blugrid.api

components:
  schemas:
    Organisation:
      type: object
      description: An organisation represents a legal or operational entity.
      x-resource-type: UnscopedResource
      x-auditable: true
      x-searchable: false
      x-db-table: organisation
      required:
        - parentOrganisationId
        - effectiveTimestamp
      properties:
        id:
          type: integer
          format: int64
          x-generated: true
        uuid:
          type: string
          format: uuid
          x-generated: true
        parentOrganisationId:
          type: integer
          format: int64
          description: The ID of the parent organisation.
          x-db-domain: bigint
        effectiveTimestamp:
          type: string
          format: date-time
          description: The date and time the organisation becomes active.
          x-db-domain: t_datetime
          x-kotlin-type: LocalDateTime
```

## Escalation Pattern

Templates inject markers when they can't handle something:

```jinja2
{% if field.x_kotlin_type in known_types %}
    val {{ field.name }}: {{ map_type(field.x_kotlin_type) }}
{% else %}
// ESCALATE: Unknown type mapping
// Field: {{ field.name }}
// Source type: {{ field.type }}
// Context: {{ field.description }}
// END_ESCALATE
{% endif %}
```

Claude scans output, finds `ESCALATE:...END_ESCALATE` blocks, generates custom code, patches files.

## Migration Summary

| Current (TypeScript) | Target (Python Skills) |
|---------------------|------------------------|
| `codegen/src/jdl/` | `parsers/jdl-parser.py` |
| `CodegenEntityModel` | OpenAPI + x-extensions |
| `codegen/src/mapper/` | Claude enrichment |
| `codegen/src/generators/` | `orchestrators/` + `layers/` + `atomic/` |
| Mustache templates | Jinja2 templates |
| `codegen/src/main.ts` | Claude invokes skills |
| Manual testing | Three-stage pipeline |

## Future Work

- TypeScript/React generators
- iOS/Android native generators
- MCP server packaging
- Docker distribution
- Deployment configuration extensions
