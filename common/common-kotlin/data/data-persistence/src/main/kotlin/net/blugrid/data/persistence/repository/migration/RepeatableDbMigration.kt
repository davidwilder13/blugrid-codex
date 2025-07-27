package net.blugrid.data.persistence.repository.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import java.time.LocalDateTime
import java.util.zip.CRC32

private fun makeChecksum(): Int {
    val crc32 = CRC32()
    crc32.update(LocalDateTime.now().toString().toByteArray())
    return crc32.value.toInt()
}

abstract class RepeatableDbMigration : BaseJavaMigration() {
    override fun getChecksum(): Int? = makeChecksum()

    open fun runMigration(context: Context, migrationScript: String) =
        context
            .connection
            .prepareStatement(migrationScript)
            .use { statement -> statement.execute() }
}
