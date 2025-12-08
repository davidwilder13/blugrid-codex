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
layers/generate-model-layer.py (DTOs, resources)
layers/generate-db-layer.py (entities, migrations)
layers/generate-api-layer.py (controllers, services)
  ↓
atomic/kotlin/generate-resource.py
atomic/kotlin/generate-entity.py
atomic/sql/generate-table.py
  ↓
templates/kotlin/*.j2
templates/sql/*.j2
```

## OpenAPI Extensions

Custom `x-` extensions for code generation:

| Extension | Level | Description |
|-----------|-------|-------------|
| `x-base-package` | info | Base package name |
| `x-module-name` | info | Module name |
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

## Directory Structure

```
.claude/skills/blugrid-codegen/
├── SKILL.md              # This file
├── config/               # Type mappings, settings
├── parsers/              # JDL parser, OpenAPI validator
├── orchestrators/        # Entry points
├── layers/               # Layer-level generation
├── atomic/               # Single-file generators
├── templates/            # Jinja2 templates
├── utils/                # Shared utilities
└── tests/                # Test fixtures and runner
```
