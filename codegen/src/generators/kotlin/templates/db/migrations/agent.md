# Template Components: DB Migrations

This directory provides TypeScript modules for generating Flyway migration scripts, including both versioned and repeatable view migrations.

## Overview

- **VersionedMigrationTemplate.ts**: Template for versioned schema migrations (`V<version>__<description>.kt`).
- **RepeatableViewMigrationTemplate.ts**: Template for repeatable view migrations (`R<viewName>__<timestamp>.kt`).
- **index.ts**: Re-exports all migration templates for convenient imports.

## Coding Standards

- **Props Interface**: Define a `<Name>Props` interface capturing all inputs (e.g., version, description, table/view names).
- **Naming**: Template files must end with `Template.ts` and export a single rendering function per file.
- **Template Literals**: Use `String.raw` for multi-line templates to preserve SQL and Kotlin formatting.
- **Index File**: Keep an `index.ts` that re-exports all template modules in this folder.
- **Mustache Logic**: Keep templates focused on presentation (loops and conditionals); perform data transformations in mappers or models.
- **Determinism**: Templates should produce identical output given the same props.