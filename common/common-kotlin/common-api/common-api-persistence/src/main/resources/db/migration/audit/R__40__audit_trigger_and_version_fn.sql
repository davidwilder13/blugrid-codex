DROP TABLE IF EXISTS _common_audit_columns CASCADE;
CREATE TABLE IF NOT EXISTS _common_audit_columns
(
    created_timestamp          t_timestamp   NOT NULL DEFAULT NOW(),
    created_by_session_id      t_identity    NOT NULL,
    last_changed_timestamp     t_timestamp   NOT NULL DEFAULT NOW(),
    last_changed_by_session_id t_identity    NOT NULL,
    version                    t_line_number NOT NULL DEFAULT 0,
    expiry_timestamp           t_timestamp   NOT NULL DEFAULT 'infinity'
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

