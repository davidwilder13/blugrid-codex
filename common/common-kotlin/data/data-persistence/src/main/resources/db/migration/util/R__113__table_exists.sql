-- -----------------------------------------------------------------------------
-- Function: table_exists
-- Description: Checks if a table exists in the current schema.
-- -----------------------------------------------------------------------------
-- Examples:
--   SELECT table_exists('orders');  -- returns true or false
CREATE OR REPLACE FUNCTION table_exists(
    IN table_name    TEXT,
    OUT table_exists BOOLEAN
) RETURNS BOOLEAN AS
$body$
DECLARE
BEGIN
    table_exists = FALSE;

    PERFORM 1
       FROM pg_catalog.pg_class c
            JOIN pg_catalog.pg_namespace n
            ON n.oid = c.relnamespace
      WHERE (c.relkind = 'r')
        AND (c.relname = quote_ident(table_name))
        AND (n.nspname = '$$$schema$$$');

    IF found THEN table_exists = TRUE; END IF;

END;
$body$ LANGUAGE 'plpgsql';
