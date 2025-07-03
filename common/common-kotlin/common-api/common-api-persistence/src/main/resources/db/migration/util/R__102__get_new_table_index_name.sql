-- -----------------------------------------------------------------------------
-- Function: get_new_table_index_name
-- Description: Generates a new index name by replacing the original table name in an existing index name with a new table short code.
-- -----------------------------------------------------------------------------
-- Examples:
--   SELECT get_new_table_index_name(
--     'audit_event_log_resource_id_tenant_id_idx',
--     'audit_event_log',
--     'bustisauevlg_000001_21'
--   );  -- returns 'bustisauevlg_000001_21_resource_id_tenant_id_idx'
CREATE OR REPLACE FUNCTION get_new_table_index_name(
    IN index_name TEXT,
    IN table_name TEXT,
    IN new_table_short_code TEXT,
    OUT new_index_name TEXT
) RETURNS TEXT AS
$body$
BEGIN
    -- "With index name audit_event_log_resource_id_tenant_id_idx" for "audit_event_log"
    -- should generate "bustisauevlg_000001_21_log_resource_id_tenant_id_idx" from new table short code "bustisauevlg_000001_21"
    SELECT regexp_replace(index_name, table_name, new_table_short_code)
    INTO new_index_name;
END;
$body$ LANGUAGE 'plpgsql';

