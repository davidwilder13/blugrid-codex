CREATE OR REPLACE FUNCTION create_tenant_table(
    in_table_name   TEXT,
    IN in_tenant_id BIGINT
)
    RETURNS TABLE (
        table_name TEXT,
        min_id     BIGINT,
        max_id     BIGINT
    )
AS
$body$
DECLARE
    the_table_primary_key  TEXT;
    sql_text               TEXT;
    table_short_code       TEXT;

    v_returned_sqlstate    TEXT;
    v_message_text         TEXT;
    v_pg_exception_detail  TEXT;
    v_pg_exception_hint    TEXT;
    v_pg_exception_context TEXT;
BEGIN
    -- lookup statement variables
    SELECT p.table_name, p.min_id, p.max_id FROM tenant_table_details(in_table_name, in_tenant_id) p
    INTO table_name, min_id, max_id;

    -- Create partition table if not exists
    IF (table_exists(table_name :: TEXT) IS FALSE) THEN
        -- gen table name short code
        table_short_code = get_new_table_short_code(in_table_name, table_name);

        -- lookup primary key
        the_table_primary_key = get_table_primary_key(in_table_name);

        -- create table
        sql_text = 'CREATE TABLE ' || table_name || '( ' || 'CONSTRAINT ' || 'pk_' || table_short_code || ' PRIMARY KEY (' || the_table_primary_key || ') ' ||
                   ') INHERITS (' || in_table_name || ')';
        EXECUTE sql_text;

        --copy indexes
        PERFORM copy_table_index(in_table_name, table_name, table_short_code);

        --create scoped id constraint
        sql_text = 'ALTER TABLE ' || table_name || ' ADD CONSTRAINT CK_' || table_short_code || '_' || the_table_primary_key || ' CHECK ' || '( ' ||
                   the_table_primary_key || ' >= ' || min_id || ' AND ' || the_table_primary_key || ' < ' || max_id || ' )';
        EXECUTE sql_text;

        --create tenant_id constraint
        sql_text = 'ALTER TABLE ' || table_name || ' ADD CONSTRAINT CK_' || table_short_code || '_TENANT_ID CHECK ' || '( TENANT_ID = ' || in_tenant_id ||
                   ' )';
        EXECUTE sql_text;

        sql_text = 'CREATE TRIGGER trig_' || table_short_code || '_update_audit ' || 'BEFORE UPDATE ON ' || table_name || ' FOR EACH ROW ' ||
                   'EXECUTE PROCEDURE proc_trig_update_audit_columns() ';
        EXECUTE sql_text;

    END IF;

    RETURN QUERY (
        SELECT table_name, min_id, max_id
    );
EXCEPTION
    WHEN OTHERS THEN GET STACKED DIAGNOSTICS v_returned_sqlstate = RETURNED_SQLSTATE, v_message_text = MESSAGE_TEXT, v_pg_exception_detail = PG_EXCEPTION_DETAIL, v_pg_exception_hint = PG_EXCEPTION_HINT, v_pg_exception_context = PG_EXCEPTION_CONTEXT;

    RAISE NOTICE E'Got exception:
        state  : %
        message: %
        detail : %
        hint   : %
        context: %', v_returned_sqlstate, v_message_text, v_pg_exception_detail, v_pg_exception_hint, v_pg_exception_context;

    RAISE NOTICE E'Got exception:
        SQLSTATE: %
        SQLERRM: %', sqlstate, sqlerrm;

    RAISE NOTICE '%', sql_text;
END;
$body$ LANGUAGE 'plpgsql';
