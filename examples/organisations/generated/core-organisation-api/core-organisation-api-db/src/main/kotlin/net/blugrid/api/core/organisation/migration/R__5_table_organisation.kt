package net.blugrid.api.core.organisation.repository.migration

import net.blugrid.api.common.persistence.repository.migration.RepeatableDbMigration
import org.flywaydb.core.api.migration.Context

class R__5_table_organisation : RepeatableDbMigration() {
    override fun migrate(context: Context) {
        runMigration(
            context, """
CREATE TABLE IF NOT EXISTS organisation_columns (
    parent_organisation_id bigint,
    effective_timestamp t_datetime
);


CREATE TABLE IF NOT EXISTS ORGANISATION (
type VARCHAR(255) NOT NULL DEFAULT 'ORGANISATION',
CONSTRAINT pk_organisation PRIMARY KEY (id)
)
INHERITS (
_common_unscoped_resource_columns,
  _common_audit_columns,
organisation_columns
)
WITHOUT OIDS;


CREATE UNIQUE INDEX IF NOT EXISTS ak_organisation_uuid ON organisation USING btree (uuid);


DROP TRIGGER IF EXISTS trig_organisation_insert_audit ON organisation;

CREATE TRIGGER trig_organisation_insert_audit
BEFORE INSERT ON organisation
FOR EACH ROW
EXECUTE PROCEDURE proc_trig_insert_audit_columns();


DROP TRIGGER IF EXISTS trig_organisation_update_audit ON organisation;

CREATE TRIGGER trig_organisation_update_audit
BEFORE UPDATE ON organisation
FOR EACH ROW
EXECUTE PROCEDURE proc_trig_update_audit_columns();
        """.trimIndent()
        )
    }
}
