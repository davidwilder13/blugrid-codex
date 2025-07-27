DROP FUNCTION IF EXISTS create_audit_event_log_tenant_partition_table;
CREATE OR REPLACE FUNCTION create_audit_event_log_tenant_partition_table(
    in_table_name    TEXT,
    in_resource_type TEXT,
    in_tenant_id     BIGINT,
    in_timestamp     TIMESTAMP
)
    RETURNS TABLE (
        table_name TEXT,
        min_id     BIGINT,
        max_id     BIGINT
    )
AS
$body$
DECLARE
    the_table_primary_key TEXT;
    the_shard_length      INT DEFAULT 6;
    in_tenant_id_length   INT DEFAULT 8;
    the_padded_tenant_id  TEXT;
    the_year              INT;
    the_padded_year       TEXT;
    the_min_timestamp     TEXT;
    the_max_timestamp     TEXT;
    resource_type_table_name TEXT;
    table_short_code      TEXT;
    sql_text                 TEXT;
BEGIN
    -- set statement variables
    the_padded_tenant_id = lpad(in_tenant_id :: TEXT, the_shard_length, '0');
    min_id = in_tenant_id * (10 ^ (in_tenant_id_length));
    max_id = ((in_tenant_id + 1) * (10 ^ (in_tenant_id_length))) - 1;

    the_year = EXTRACT(YEAR FROM in_timestamp);
    the_padded_year = lpad((the_year - 2000)::TEXT, 2, '0');
    the_min_timestamp = the_year::text || '-01-01 00:00:00';
    the_max_timestamp = (the_year + 1)::text || '-01-01 00:00:00';

    resource_type_table_name = lower(in_resource_type || '_' || in_table_name);
    table_name = resource_type_table_name || '_' || the_padded_tenant_id || '_' || the_padded_year;
    table_short_code = get_new_table_short_code(resource_type_table_name, table_name);

    -- Create partition table if not exists
    IF (table_exists(table_name :: TEXT) IS FALSE) THEN
        -- lookup primary key
        the_table_primary_key = get_table_primary_key(in_table_name);

        -- create table
        sql_text = 'CREATE TABLE ' || table_name || '( ' || 'CONSTRAINT ' || 'pk_' || table_short_code || ' PRIMARY KEY (' || the_table_primary_key || ') ' ||
                ') INHERITS (' || in_table_name || ')';
        EXECUTE sql_text;

        --copy indexes
        PERFORM copy_table_index(in_table_name, table_name, table_short_code);

        --create scoped id constraint
        sql_text = 'ALTER TABLE ' || table_name || ' ADD CONSTRAINT CK_' || table_short_code || '_RESOURCE_ID CHECK ' || '( RESOURCE_ID >= ' || min_id ||
                ' AND RESOURCE_ID < ' || max_id || ' )';
        EXECUTE sql_text;

        --create tenant_id constraint
        sql_text = 'ALTER TABLE ' || table_name || ' ADD CONSTRAINT CK_' || table_short_code || '_TENANT_ID CHECK ' || '( TENANT_ID = ' || in_tenant_id ||
                ' )';
        EXECUTE sql_text;

        --create resource_type constraint
        sql_text = 'ALTER TABLE ' || table_name || ' ADD CONSTRAINT CK_' || table_short_code || '_RESOURCE_TYPE CHECK ' || '( RESOURCE_TYPE = ' ||
                quote_literal(in_resource_type) || ' )';
        EXECUTE sql_text;

        --create year constraint
        sql_text = 'ALTER TABLE ' || table_name || ' ADD CONSTRAINT CK_' || table_short_code || '_YEAR CHECK ' || '( TIMESTAMP >= ' ||
                quote_literal(the_min_timestamp) || ' AND TIMESTAMP < ' || quote_literal(the_max_timestamp) || ' )';
        EXECUTE sql_text;
    END IF;

    RETURN QUERY (
        SELECT table_name, min_id, max_id
    );
END;
$body$ LANGUAGE 'plpgsql';
