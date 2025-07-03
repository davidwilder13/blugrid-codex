CREATE OR REPLACE FUNCTION partition_table_exists(
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
      WHERE (c.relkind = 'p')
        AND (c.relname = quote_ident(table_name))
        AND (n.nspname = '$$$schema$$$');

    IF found THEN table_exists = TRUE; END IF;

END;
$body$ LANGUAGE 'plpgsql';
