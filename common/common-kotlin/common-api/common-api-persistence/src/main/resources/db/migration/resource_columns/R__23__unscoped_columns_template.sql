-- -----------------------------------------------------------------------------
-- Table: _common_unscoped_resource_columns
-- Description: Template for unscoped resource columns (id, uuid, type) for global resources.
-- -----------------------------------------------------------------------------
-- Example usage (see examples/.../R__5_table_organisation.kt):
--   CREATE TABLE organisation (
--       ...
--   ) INHERITS (
--       _common_unscoped_resource_columns,
--       _common_audit_columns,
--       organisation_columns
--   );
create table if not exists _common_unscoped_resource_columns
(
    id   bigint       not null,
    uuid uuid not null,
    type varchar(255) not null
) without OIDS;
