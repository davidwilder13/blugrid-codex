CREATE OR REPLACE FUNCTION create_tenant_partition_table(
    IN in_table_name TEXT,
    IN in_tenant_id  BIGINT,
    OUT table_name   TEXT,
    OUT min_id       BIGINT,
    OUT max_id       BIGINT
) RETURNS RECORD AS
$body$
DECLARE
    in_tenant_id          BIGINT;
    the_table_primary_key TEXT;
    the_shard_length      INT DEFAULT 6;
    in_tenant_id_length   INT DEFAULT 8;
    the_padded_tenant_id  TEXT;
    table_short_code      TEXT;

BEGIN
    in_tenant_id = get_tenant_scope();

    -- set statement variables
    the_padded_tenant_id = lpad(in_tenant_id :: TEXT, the_shard_length, '0');
    min_id = in_tenant_id * (10 ^ (in_tenant_id_length));
    max_id = ((in_tenant_id + 1) * (10 ^ (in_tenant_id_length))) - 1;

    table_name = in_table_name || '_' || the_padded_tenant_id;

    -- Create partition table if not exists
    IF (table_exists(table_name :: TEXT) IS FALSE) THEN
        -- lookup primary key
        the_table_primary_key = get_table_primary_key(in_table_name);
        table_short_code = get_new_table_short_code(in_table_name, table_name);

        -- create table
        EXECUTE 'CREATE TABLE ' || table_name || ' PARTITION OF ' || in_table_name || ' FOR VALUES FROM (' || min_id || ') TO (' || max_id || ')';

        --create tenant_id constraint
        EXECUTE 'ALTER TABLE ' || table_name || ' ADD CONSTRAINT CK_' || table_short_code || '_TENANT_ID CHECK ' || '( TENANT_ID = ' || in_tenant_id ||
                ' )';
    END IF;

END;
$body$ LANGUAGE 'plpgsql';
