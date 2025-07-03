CREATE TABLE IF NOT EXISTS audit_event_log
(
    uuid              uuid      NOT NULL,
    resource_id       BIGINT    NOT NULL,
    resource_type     TEXT      NOT NULL,
    version    INTEGER   NOT NULL DEFAULT 0,
    audit_event_type  TEXT      NOT NULL,
    resource          jsonb,
    tenant_id         BIGINT    NOT NULL,
    session_id        BIGINT    NOT NULL,
    timestamp         TIMESTAMP NOT NULL,
    created_timestamp TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT pk_audit_event_log PRIMARY KEY (uuid),
    CHECK (FALSE) NO INHERIT
)
WITHOUT OIDS;

CREATE INDEX IF NOT EXISTS idx_audit_event_log_tenant_id ON audit_event_log USING btree (tenant_id);

CREATE UNIQUE INDEX IF NOT EXISTS ak_audit_event_log_resource_id_type_version ON audit_event_log USING btree (resource_id, resource_type, version);

CREATE INDEX IF NOT EXISTS idx_audit_event_log_resource_id_resource_type ON audit_event_log USING btree (resource_id, resource_type);

CREATE INDEX IF NOT EXISTS idx_audit_event_log_tenant_id_resource_type ON audit_event_log USING btree (tenant_id, resource_type);

CREATE INDEX IF NOT EXISTS idx_audit_event_log_tenant_id_resource_id ON audit_event_log USING btree (tenant_id, resource_id);

CREATE INDEX IF NOT EXISTS idx_audit_event_log_timestamp ON audit_event_log USING btree (timestamp);
