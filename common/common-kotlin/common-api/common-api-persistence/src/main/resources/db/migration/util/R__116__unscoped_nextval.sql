CREATE OR REPLACE FUNCTION unscoped_nextval(
    IN input_name VARCHAR,
    OUT id BIGINT
) RETURNS BIGINT AS
$body$
DECLARE
    seq_name            TEXT;
    sequence_exists     BOOLEAN;
BEGIN
    -- 1. Construct the sequence name from the table name
    seq_name := 'seq_' || input_name;
    seq_name := quote_ident(seq_name); -- Safely quote the sequence name

    -- 3. Check if the sequence already exists in the current schema
    SELECT EXISTS(SELECT 1 FROM pg_sequences WHERE schemaname = current_schema() AND sequencename = seq_name) INTO sequence_exists;

    -- 4. If the sequence does not exist, create it with the appropriate settings
    IF NOT sequence_exists THEN
        EXECUTE 'CREATE SEQUENCE ' || seq_name || ' START WITH 1';
    END IF;

    -- 5. Use the sequence to get the next ID value
    EXECUTE 'SELECT nextval(''' || seq_name || ''')' INTO id;

    RETURN;
END;
$body$ LANGUAGE 'plpgsql';
