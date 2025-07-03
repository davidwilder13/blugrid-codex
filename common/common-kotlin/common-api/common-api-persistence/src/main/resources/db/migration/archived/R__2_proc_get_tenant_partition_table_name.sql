-- -----------------------------------------------------------------------------
-- Function: get_tenant_partition_table_name
-- Description: Returns the partition table name for a tenant, creating the partition if it does not exist.
-- -----------------------------------------------------------------------------
-- Examples:
--   SELECT get_tenant_partition_table_name('orders', 1);
CREATE OR REPLACE FUNCTION get_tenant_partition_table_name(
    IN in_base_table TEXT,
    IN in_tenant_id  BIGINT,
    OUT table_name   TEXT
) RETURNS TEXT AS
$body$
DECLARE
BEGIN
    --Create table if needed
    SELECT p.table_name FROM create_tenant_partition_table(in_base_table, in_tenant_id) p INTO table_name;
END;
$body$ LANGUAGE 'plpgsql';
