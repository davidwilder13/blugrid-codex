DROP FUNCTION IF EXISTS get_new_table_short_code;
CREATE OR REPLACE FUNCTION get_new_table_short_code(
    IN source_table_name      TEXT,
    IN destination_table_name TEXT,
    OUT new_table_short_code TEXT
) RETURNS TEXT AS
$body$
DECLARE
    short_code TEXT;
BEGIN
    SELECT get_short_code(source_table_name) INTO short_code;

    SELECT replace(destination_table_name, source_table_name, short_code)
    INTO new_table_short_code;
END;
$body$ LANGUAGE 'plpgsql';
