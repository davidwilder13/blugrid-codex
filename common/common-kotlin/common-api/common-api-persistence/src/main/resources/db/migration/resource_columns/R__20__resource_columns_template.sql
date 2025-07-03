-- -----------------------------------------------------------------------------
-- Table: _common_resource_columns
-- Description: Template for base resource columns (id, uuid, type) for general resources.
-- -----------------------------------------------------------------------------
-- Example usage (see examples/.../R__5_table_organisation.kt):
--   CREATE TABLE my_resource (
--       ...
--   ) INHERITS (
--       _common_resource_columns,
--       _common_audit_columns,
--       my_resource_columns
--   );
create table if not exists _common_resource_columns
(
    id   BIGINT       not null,
    uuid uuid default gen_random_uuid() not null,
    type VARCHAR(255) not null
) without OIDS;
