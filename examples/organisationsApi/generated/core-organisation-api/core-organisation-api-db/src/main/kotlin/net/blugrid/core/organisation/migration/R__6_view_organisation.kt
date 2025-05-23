
package net.blugrid.core.organisation.repository.migration

import net.blugrid.api.db.migration.RepeatableDbMigration
import org.flywaydb.core.api.migration.Context

class R__vw_organisation : RepeatableDbMigration() {
    override fun migrate(context: Context) {
        runMigration(context, """

DROP VIEW IF EXISTS vw_organisation CASCADE;
CREATE OR REPLACE VIEW vw_organisation WITH (security_barrier)
AS
SELECT *
FROM organisation
WHERE expiry_timestamp > now()
WITH CHECK OPTION;



CREATE OR REPLACE FUNCTION proc_trig_organisation_insert()
RETURNS TRIGGER AS $body$
DECLARE
BEGIN
    new.expiry_timestamp = 'infinity';
    new.version = 0;
    new.type = 'ORGANISATION';

    EXECUTE 'INSERT INTO ORGANISATION VALUES ($1.*)' USING new;
    RETURN new;
END;
$body$ LANGUAGE 'plpgsql';



DROP TRIGGER IF EXISTS trig_organisation_insert ON vw_organisation;



CREATE TRIGGER trig_organisation_insert
    INSTEAD OF INSERT
    ON vw_organisation
    FOR EACH ROW
EXECUTE PROCEDURE proc_trig_organisation_insert();

        """.trimIndent())
    }
}
