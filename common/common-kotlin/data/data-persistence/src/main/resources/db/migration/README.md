# Database Migrations

This directory contains Flyway migrations for the **common-api-persistence** module.

## Directory Structure & File Registry

Migrations are organized by category under subdirectories. All filenames prefixed with `R__` are **Repeatable Migrations**; versioned migrations (prefix `V`) go in the root.

### Top‑Level Files

- `before_migrate.sql` — executed before any versioned or repeatable migrations.

### extensions/

- `R__00__enable_pg_extensions.sql`

### settings/

- `R__05__baseline_settings.sql`

### domains/

- `R__08__common_type_domains.sql`

### enums/

- `R__10__common_enums.sql`
- `R__15__access_control_permission_enum.sql`

### audit/

- `R__40__audit_trigger_and_version_fn.sql`

### util/

- `R__80__text_to_bigint.sql`
- `R__100__copy_table_index.sql`
- `R__101__create_tenant_table.sql`
- `R__102__get_new_table_index_name.sql`
- `R__103__get_new_table_index_statement.sql`
- `R__104__get_new_table_short_code.sql`
- `R__105__get_short_code.sql`
- `R__106__get_table_primary_key.sql`
- `R__107__get_tenant_table_name.sql`
- `R__108__is_nil.sql`
- `R__109__is_not_nil.sql`
- `R__110__setvar.sql`
- `R__111__strip_schema_name.sql`
- `R__112__table_column_exists.sql`
- `R__113__table_exists.sql`

### sequence/

- `R__114__tenant_nextval.sql`
- `R__115__tenant_sequence_details.sql`
- `R__116__unscoped_nextval.sql`

### resource_columns/

- `R__20__resource_columns_template.sql`
- `R__21__tenant_resource_columns_template.sql`
- `R__22__business_unit_columns_template.sql`
- `R__23__unscoped_columns_template.sql`

### scope/

- `R__60__get_business_unit_tenant_scope.sql`
- `R__61__get_business_unit_scope.sql`
- `R__62__get_tenant_scope.sql`
- `R__63__get_operator_party_scope.sql`
- `R__64__get_session_scope.sql`
- `R__65__reset_request_scope.sql`
- `R__66__set_request_scope.sql`
- `R__69__vw_request_scope.sql`

### archived/

- `R__2_proc_create_tenant_partition_table.sql`
- `R__2_proc_get_tenant_partition_table_name.sql`
- `R__2_proc_partition_table_column_exists.sql`
- `R__2_proc_partition_table_exists.sql`
- `V2019.04.01.09.01.00__multitenant_table.sql`

## Migration Types & Numbering

### Versioned Migrations

Versioned migrations run once in order and use the `V` prefix:

```
V<YYYY.MM.DD.HHMMSS>__description.sql
```

*Example:* `V2021.06.15.103000__add_new_table.sql`

Place versioned scripts in the root of this folder. Flyway applies them in ascending version order.

### Repeatable Migrations

Repeatable migrations use the `R__` prefix and are re-applied whenever their checksum changes:

```
R__NN__description.sql
```

- `NN` (numeric) controls execution ordering within repeatable migrations.
- `description` should be lowercase, underscore-separated.

Examples of repeatable categories:

- **Utility functions:** `util/`
- **Scope management:** `scope/`
- **Sequence helpers:** `sequence/`
- **Resource column templates:** `resource_columns/`

## Adding New Migrations

1. **Versioned Migration**: Create a new `V...__<desc>.sql` in this folder.
2. **Repeatable Migration**: Create `R__<NN>__<desc>.sql` under the appropriate subdirectory.
3. Add your DDL/DML statements (idempotent where applicable).
4. Commit and push; Flyway will detect and apply changes on the next run.

For examples, see `examples/.../R__5_table_organisation.kt` demonstrating inheritance of `resource_columns` templates.
