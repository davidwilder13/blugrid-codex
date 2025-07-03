# Template Components: Model

This directory contains TypeScript modules for generating Kotlin resource model classes, including data classes for CRUD operations and interface definitions.

## Overview

- **KotlinResourceTemplate.ts**: Provides Mustache templates for multiple resource variants:
  - `model`: full resource data class.
  - `create`: DTO for create operations.
  - `update`: DTO for update operations.
  - `interface`: Kotlin interface definition.

## Coding Standards

- **Variants**: Use a discriminated union `KotlinResourceTemplateVariant` for all template variants in one module.
- **Props Interface**: Define `KotlinResourceTemplateProps` including all template inputs (fields, imports, naming metadata).
- **Template Mapping**: Maintain a single `templates` record keyed by variant; avoid splitting variants into multiple files.
- **Rendering**: Export a single function `KotlinResourceTemplate(props)` that calls `Mustache.render(template, context)`.
- **Multi-line Literals**: Use `String.raw` for each variant template to preserve code formatting.
- **File Naming**: The file name must match the exported function name (`KotlinResourceTemplate.ts`).