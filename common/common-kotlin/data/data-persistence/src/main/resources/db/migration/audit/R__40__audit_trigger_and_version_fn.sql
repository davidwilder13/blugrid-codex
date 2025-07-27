CREATE TABLE IF NOT EXISTS _common_audit_columns
(
    created_timestamp          TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_by_session_id      bigint    NOT NULL,
    last_changed_timestamp     TIMESTAMP   NOT NULL DEFAULT NOW(),
    last_changed_by_session_id bigint    NOT NULL,
    version                    t_line_number NOT NULL DEFAULT 0,
    expiry_timestamp           TIMESTAMP   NOT NULL DEFAULT 'infinity'
)
    WITHOUT OIDS;

CREATE OR REPLACE FUNCTION proc_trig_insert_audit_columns() RETURNS TRIGGER AS
$body$
DECLARE
BEGIN
    IF (TO_JSONB(new) ? 'version') THEN new.version := 1; END IF;

    RETURN new;
END;
$body$ LANGUAGE 'plpgsql';



CREATE OR REPLACE FUNCTION proc_trig_update_audit_columns() RETURNS TRIGGER AS
$body$
DECLARE
BEGIN
    IF (TO_JSONB(new) ? 'version') THEN new.version := new.version + 1; END IF;

    RETURN new;
END;
$body$ LANGUAGE 'plpgsql';

