DROP FUNCTION IF EXISTS setvar(text,anyelement);

CREATE OR REPLACE FUNCTION setvar(
    IN _key    text,
    IN _val    ANYELEMENT
) RETURNS VOID AS
$body$
BEGIN
    --     DROP TABLE IF EXISTS tmp_variable;
    CREATE TEMP TABLE IF NOT EXISTS tmp_variable (
        key        text NOT NULL PRIMARY KEY,
        text_val   text,
        int_val    int,
        bigint_val bigint,
        uuid_val   uuid
    );

    CASE pg_typeof(_val) WHEN 'text'::regtype THEN INSERT INTO tmp_variable(key, text_val)
                                                   VALUES (_key, _val)
                                                       ON CONFLICT (key) DO UPDATE SET text_val = _val;

                         WHEN 'integer'::regtype THEN INSERT INTO tmp_variable(key, int_val)
                                                      VALUES (_key, _val)
                                                          ON CONFLICT (key) DO UPDATE SET int_val = _val;

                         WHEN 'bigint'::regtype THEN INSERT INTO tmp_variable(key, bigint_val)
                                                     VALUES (_key, _val)
                                                         ON CONFLICT (key) DO UPDATE SET bigint_val = _val;

                         WHEN 'uuid'::regtype THEN INSERT INTO tmp_variable(key, uuid_val)
                                                   VALUES (_key, _val)
                                                       ON CONFLICT (key) DO UPDATE SET uuid_val = _val;

                         ELSE RAISE EXCEPTION 'Unexpected data type: %', pg_typeof(_val)::text; END CASE;
END;
$body$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION getvar(
    IN _key    TEXT,
    OUT result TEXT
) AS
$body$
BEGIN
    SELECT coalesce(text_val, int_val::TEXT, bigint_val::TEXT, uuid_val::TEXT) FROM tmp_variable WHERE key = _key::TEXT INTO result;

    RETURN;

END;
$body$ LANGUAGE 'plpgsql';
