DROP VIEW IF EXISTS vw_audit_event_log_read;

CREATE OR REPLACE VIEW vw_audit_event_log_read
AS
SELECT *
  FROM audit_event_log s;

