CREATE OR REPLACE FUNCTION get_tenant_table_name(
    IN in_base_table TEXT,
    IN in_tenant_id  T_IDENTITY
) RETURNS TEXT AS
$body$
DECLARE
    the_table_name TEXT;
BEGIN
    --Create table if needed
    SELECT p.table_name INTO the_table_name FROM create_tenant_table(in_base_table, in_tenant_id) p;

    RETURN the_table_name;
END;
$body$ LANGUAGE 'plpgsql';
