CREATE TABLE IF NOT EXISTS _common_unscoped_resource_columns
(
    id        t_identity   NOT NULL,
    uuid      t_uuid       NOT NULL,
    type      t_table_name NOT NULL
)
WITHOUT OIDS;
