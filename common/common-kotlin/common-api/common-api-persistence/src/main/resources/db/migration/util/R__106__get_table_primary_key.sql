CREATE OR REPLACE FUNCTION get_table_primary_key(
    IN table_name   TEXT,
    OUT primary_key TEXT
) RETURNS TEXT AS
$body$
DECLARE
BEGIN

    SELECT
        STRING_AGG(pg_attribute.attname, ',')
      FROM pg_index,
           pg_class,
           pg_attribute,
           pg_namespace
     WHERE pg_class.oid = quote_ident(table_name) :: REGCLASS
       AND indrelid = pg_class.oid
       AND nspname = '$$$schema$$$'
       AND pg_class.relnamespace = pg_namespace.oid
       AND pg_attribute.attrelid = pg_class.oid
       AND pg_attribute.attnum = ANY (pg_index.indkey)
       AND indisprimary
      INTO primary_key;

END;
$body$ LANGUAGE 'plpgsql';
