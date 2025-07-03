package net.blugrid.api.common.persistence.repository.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context

abstract class DbMigration : BaseJavaMigration() {

    open fun runMigration(context: Context, migrationScript: String) =
        context
            .connection
            .prepareStatement(migrationScript)
            .use { statement -> statement.execute() }
}
