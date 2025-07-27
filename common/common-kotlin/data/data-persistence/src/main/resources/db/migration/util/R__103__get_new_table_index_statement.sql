-- -----------------------------------------------------------------------------
-- Function: get_new_table_index_statement
-- Description: Produces a modified index creation statement by substituting the table name and schema with a new table name and short code.
-- -----------------------------------------------------------------------------
-- Examples:
--   SELECT get_new_table_index_statement(
--     'CREATE INDEX audit_event_log_resource_id_tenant_id_idx ON public.audit_event_log(resource_id, tenant_id)',
--     'audit_event_log', 'public',
--     'audit_event_log_000001_21', 'bustisauevlg_000001_21'
--   );  -- returns 'CREATE INDEX bustisauevlg_000001_21_resource_id_tenant_id_idx ON public.audit_event_log_000001_21(resource_id, tenant_id)'
DROP FUNCTION IF EXISTS get_new_table_index_statement;
CREATE OR REPLACE FUNCTION get_new_table_index_statement(
    IN index_statement      TEXT,
    IN table_name           TEXT,
    IN schema               TEXT,
    IN new_table_name       TEXT,
    IN new_table_short_code TEXT,
    OUT new_index_statement TEXT
) RETURNS TEXT AS
$body$
DECLARE
    statement_with_new_table_short_code TEXT;
BEGIN
    -- "With index name audit_event_log_resource_id_tenant_id_idx" for "audit_event_log"
    -- should generate "bustisauevlg_000001_21_log_resource_id_tenant_id_idx" from new table short code "bustisauevlg_000001_21"
    SELECT regexp_replace(index_statement, table_name, new_table_short_code)
    INTO statement_with_new_table_short_code;

    SELECT replace(statement_with_new_table_short_code, ' ON ' || schema || '.' || table_name, ' ON ' || schema || '.' || new_table_name)
      INTO new_index_statement;
END ;
$body$ LANGUAGE 'plpgsql';
