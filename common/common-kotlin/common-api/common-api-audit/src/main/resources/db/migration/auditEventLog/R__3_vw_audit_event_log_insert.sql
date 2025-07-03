DROP VIEW IF EXISTS vw_audit_event_log_insert;

CREATE OR REPLACE VIEW vw_audit_event_log_insert
AS
SELECT *
  FROM audit_event_log s;

CREATE OR REPLACE FUNCTION proc_trig_audit_event_log_insert(
) RETURNS TRIGGER AS
$body$
DECLARE
    the_table_name TEXT;
BEGIN
    new.created_timestamp = now();

    SELECT coalesce(MAX(version), 0) + 1
    FROM audit_event_log
    WHERE resource_id = new.resource_id AND resource_type = new.resource_type
    INTO new.version;

    the_table_name = create_audit_event_log_partition_table('audit_event_log', new.resource_type::TEXT, new.timestamp::TIMESTAMP);

    EXECUTE 'INSERT INTO ' || QUOTE_IDENT(the_table_name) || ' VALUES ($1.*) '
        USING new;

    RETURN new;
END
$body$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS trig_audit_event_log ON vw_audit_event_log_insert;

CREATE TRIGGER trig_audit_event_log
    INSTEAD OF INSERT
    ON vw_audit_event_log_insert
    FOR EACH ROW
EXECUTE PROCEDURE proc_trig_audit_event_log_insert();


