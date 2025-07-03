# Template Components: DB SQL

This directory holds TypeScript modules for generating raw SQL scripts used in creating database tables and views.

## Overview

- **CreateTableSQLTemplate.ts**: Template for `CREATE TABLE` SQL statements.
- **CreateViewSQLTemplate.ts**: Template for `CREATE VIEW` SQL statements.
- **index.ts**: Aggregates and exports all SQL templates in this folder.

## Coding Standards

- **Props Interface**: Define a `<Name>Props` interface capturing table/view names, columns, types, and constraints.
- **String.raw**: Use `String.raw` for preserving SQL formatting and line breaks.
- **Index File**: Ensure `index.ts` exports every template module present.
- **Minimal Logic**: Templates should handle only simple Mustache loops or conditionals; complex transformations belong in mappers.
- **SQL Formatting**: Use uppercase SQL keywords and consistent indentation for readability.