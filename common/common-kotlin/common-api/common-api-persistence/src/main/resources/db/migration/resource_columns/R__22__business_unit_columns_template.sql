-- -----------------------------------------------------------------------------
-- Table: _common_business_unit_resource_columns
-- Description: Template for business-unit-scoped resource columns (id, uuid, type, tenant_id, business_unit_id).
-- -----------------------------------------------------------------------------
-- Example usage:
--   CREATE TABLE bu_resource (
--       ...
--   ) INHERITS (
--       _common_business_unit_resource_columns,
--       _common_audit_columns,
--       bu_resource_columns
--   );
create table if not exists _common_business_unit_resource_columns
(
    id               bigint       not null,
    uuid uuid not null,
    type             VARCHAR(255) not null,
    tenant_id        bigint       not null,
    business_unit_id bigint       not null
) without OIDS;
