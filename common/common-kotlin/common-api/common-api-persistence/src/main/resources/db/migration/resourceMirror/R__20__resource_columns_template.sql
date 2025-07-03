DROP TABLE IF EXISTS _common_resource_columns CASCADE;
CREATE TABLE IF NOT EXISTS _common_resource_columns
(
    id   T_IDENTITY   NOT NULL,
    uuid T_UUID       NOT NULL,
    type T_TABLE_NAME NOT NULL
)
WITHOUT OIDS;
