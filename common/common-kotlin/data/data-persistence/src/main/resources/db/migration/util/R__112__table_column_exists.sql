-- -----------------------------------------------------------------------------
-- Function: table_column_exists
-- Description: Determines whether a specified column exists in a given table within the current schema.
-- -----------------------------------------------------------------------------
-- Examples:
--   SELECT table_column_exists('orders', 'customer_id');  -- returns true or false
CREATE OR REPLACE FUNCTION table_column_exists(
    IN table_name    TEXT,
    IN column_name   TEXT,
    OUT table_exists BOOLEAN
) RETURNS BOOLEAN AS
$body$
DECLARE
BEGIN
    table_exists = FALSE;

    PERFORM 1
       FROM pg_catalog.pg_class c
            JOIN pg_catalog.pg_namespace n
            ON (n.oid = c.relnamespace) AND (c.relkind = 'r') AND (c.relname = quote_ident(table_name)) AND (n.nspname = '$$$schema$$$')
            JOIN pg_catalog.pg_attribute a
            ON (a.attrelid = c.oid) AND (a.attname = quote_ident(column_name));

    IF found THEN table_exists = TRUE; END IF;

END;
$body$ LANGUAGE 'plpgsql';
