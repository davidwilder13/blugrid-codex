-- -----------------------------------------------------------------------------
-- Function: tenant_nextval
-- Description: Provides the next value for a tenant-specific or global sequence, creating and configuring the sequence if necessary.
-- -----------------------------------------------------------------------------
-- Examples:
--   SELECT tenant_nextval('orders', 1);    -- returns a value between tenant-specific min/max range
--   SELECT tenant_nextval('users', NULL);  -- returns a global sequence value
CREATE OR REPLACE FUNCTION tenant_nextval(
    IN input_name VARCHAR,
    IN tenant_id BIGINT,
    OUT id BIGINT
) RETURNS BIGINT AS
$body$
DECLARE
    default_schema_name TEXT = '$$$schema$$$';  -- This will be replaced by Flyway
    table_name          TEXT;
    seq_name            TEXT;
    the_min_id          BIGINT;
    the_max_id          BIGINT;
    sequence_exists     BOOLEAN;
    is_tenant_table     BOOLEAN;
BEGIN
    -- 1. Since the input_name is just the table name, we use it directly
    table_name := input_name;

    -- 2. Determine if the table is tenant-specific by checking for a tenant_id column
    SELECT table_column_exists(table_name, 'tenant_id') AND table_name != 'organisation' INTO is_tenant_table;

    -- 3. Generate the sequence name based on whether the table is tenant-specific
    IF (is_tenant_table) THEN
        seq_name := 'seq_' || table_name || '_' || tenant_id;
    ELSE
        seq_name := 'seq_' || table_name;
    END IF;

    -- 4. Safely quote the sequence name
    seq_name := quote_ident(seq_name);

    -- 5. Check if the sequence already exists in the default schema
    SELECT EXISTS(SELECT 1 FROM pg_sequences WHERE schemaname = default_schema_name AND sequencename = seq_name) INTO sequence_exists;

    -- 6. If the sequence does not exist, create it with the appropriate settings
    IF NOT sequence_exists THEN
        IF (is_tenant_table) THEN
            -- 7. For tenant-specific tables, create a sequence with custom min, max, and start values
            SELECT min_id + 1 as min_id, max_id
            INTO the_min_id, the_max_id
            FROM tenant_sequence_details(tenant_id);

            EXECUTE 'CREATE SEQUENCE ' || default_schema_name || '.' || seq_name ||
                    ' START WITH ' || the_min_id ||
                    ' MINVALUE ' || the_min_id ||
                    ' MAXVALUE ' || the_max_id;
        ELSE
            -- 8. Create a global sequence with default values if it doesn't exist
            EXECUTE 'CREATE SEQUENCE ' || default_schema_name || '.' || seq_name || ' START WITH 1';
        END IF;
    END IF;

    -- 9. Use the sequence to get the next ID value
    EXECUTE 'SELECT nextval(''' || default_schema_name || '.' || seq_name || ''')' INTO id;

END;
$body$ LANGUAGE 'plpgsql';

