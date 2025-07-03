-- -----------------------------------------------------------------------------
-- Table: _common_tenant_resource_columns
-- Description: Template for tenant-scoped resource columns (id, uuid, type, tenant_id).
-- -----------------------------------------------------------------------------
-- Example usage:
--   CREATE TABLE tenant_resource (
--       ...
--   ) INHERITS (
--       _common_tenant_resource_columns,
--       _common_audit_columns,
--       tenant_resource_columns
--   );
CREATE TABLE IF NOT EXISTS _common_tenant_resource_columns
(
    id        bigint   NOT NULL,
    uuid      UUID       NOT NULL,
    type      VARCHAR(255) NOT NULL,
    tenant_id bigint   NOT NULL
)
WITHOUT OIDS;
