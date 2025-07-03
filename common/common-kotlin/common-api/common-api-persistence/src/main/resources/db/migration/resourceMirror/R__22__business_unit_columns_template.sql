CREATE TABLE IF NOT EXISTS _common_business_unit_resource_columns
(
    id               t_identity   NOT NULL,
    uuid             t_uuid       NOT NULL,
    type             t_table_name NOT NULL,
    tenant_id        t_identity   NOT NULL,
    business_unit_id t_identity   NOT NULL
)
WITHOUT OIDS;
