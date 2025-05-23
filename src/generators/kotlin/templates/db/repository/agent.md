# Template Components: DB Repository

This folder contains TypeScript modules for generating JPA repository interfaces and entity class definitions for database access in Kotlin.

## Overview

- **KotlinEntityTemplate.ts**: Mustache template for JPA entity classes (`<Entity>Name.kt`).
- **KotlinGenericCrudRepositoryTemplate.ts**: Template for generic CRUD repository interfaces.

## Coding Standards

- **Single Entry Point**: Each file must export one rendering function along with a typed props interface.
- **Props Definition**: Define a `<Name>Props` interface including package names, class names, fields metadata, and imports.
- **Multi-line Templates**: Use `String.raw` for code blocks to maintain indentation and formatting consistency.
- **Naming Conventions**: Files should be named `<Thing>Template.ts` to clearly indicate their purpose.
- **Imports and Scoping**: Include only necessary imports via Mustache sections; avoid hardcoding package paths beyond props.