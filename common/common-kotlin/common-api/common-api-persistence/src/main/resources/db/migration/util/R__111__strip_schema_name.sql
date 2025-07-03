CREATE OR REPLACE FUNCTION strip_schema_name(
    IN table_name      TEXT,
    OUT out_table_name TEXT
) RETURNS TEXT AS
$body$
DECLARE

BEGIN
    -- remove schema name from table name if exists
    IF (table_name LIKE '%.%') THEN out_table_name = split_part(table_name, '.', 2); ELSE out_table_name = table_name; END IF;
END;
$body$ LANGUAGE 'plpgsql';
