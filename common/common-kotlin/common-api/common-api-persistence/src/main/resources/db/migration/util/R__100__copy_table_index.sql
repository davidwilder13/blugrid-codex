DROP FUNCTION IF EXISTS copy_table_index;
CREATE OR REPLACE FUNCTION copy_table_index(
    source_table_name      TEXT,
    destination_table_name TEXT,
    destination_table_short_code TEXT
) RETURNS VOID AS
$body$
DECLARE
    the_source_index_statement   TEXT;
    the_source_index_name        TEXT;
    the_new_index_name           TEXT;
    the_new_index_statement      TEXT;
BEGIN
    -- null check
    IF ((source_table_name IS NOT NULL) AND (destination_table_name IS NOT NULL)) THEN
        source_table_name = strip_schema_name(source_table_name);
        destination_table_name = strip_schema_name(destination_table_name);

        -- loop over any index that is not primary replacing table names
        FOR the_source_index_statement, the_source_index_name IN SELECT pg_get_indexdef(idx.indexrelid),
                                                                        i.relname
                                                                   FROM pg_index AS idx
                                                                        JOIN pg_class AS i
                                                                        ON (i.oid = idx.indexrelid)
                                                                  WHERE (idx.indrelid = ('$$$schema$$$.' || source_table_name) :: regclass)
                                                                    AND (idx.indisprimary IS FALSE)
                                                                    AND (i.relname !~ '[0-9]') LOOP -- exclude inherited tables

            -- gen new index name
            SELECT get_new_table_index_name(the_source_index_name, source_table_name, destination_table_short_code)
              INTO the_new_index_name;

            -- gen new index statement
            SELECT get_new_table_index_statement(the_source_index_statement, source_table_name, '$$$schema$$$', destination_table_name, destination_table_short_code)
              INTO the_new_index_statement;

            -- check not exists first
            -- IF NOT EXISTS(SELECT 1 FROM pg_indexes i WHERE (i.indexname = the_new_index_name)) THEN
                -- create the index
                EXECUTE the_new_index_statement || ';' ;
            -- END IF; -- end exists
        END LOOP; -- end loop
    END IF; -- null check

END;
$body$ LANGUAGE 'plpgsql';
