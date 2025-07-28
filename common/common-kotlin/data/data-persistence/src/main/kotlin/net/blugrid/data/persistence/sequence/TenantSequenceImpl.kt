package net.blugrid.data.persistence.sequence

import jakarta.inject.Singleton
import net.blugrid.data.persistence.service.DatabaseService
import net.blugrid.platform.config.DbProps
import net.blugrid.platform.logging.logger
import org.hibernate.engine.jdbc.spi.JdbcCoordinator
import org.hibernate.engine.spi.SharedSessionContractImplementor
import java.sql.SQLException

/**
 * Implementation of tenant-aware sequence generation
 * Uses PostgreSQL functions for tenant isolation and sequence management
 */
@Singleton
class TenantSequenceImpl(
    private val dbProps: DbProps,
    private val databaseService: DatabaseService,
) : TenantSequence {

    private val log = logger()

    override fun tenantNextVal(session: SharedSessionContractImplementor, inputName: String, tenantId: Long): Long {
        val jdbcCoordinator = session.jdbcCoordinator
        val defaultSchemaName = dbProps.schema
        val (schemaName, tableName) = parseInputName(inputName, defaultSchemaName)

        log.debug("Generating tenant sequence value for table: {}, tenant: {}", tableName, tenantId)

        if (sequenceExists(jdbcCoordinator, schemaName, tableName)) {
            return executeNextVal(jdbcCoordinator, inputName)
                .also {
                    jdbcCoordinator.afterStatementExecution()
                    log.debug("Used existing sequence: {} -> {}", inputName, it)
                }
        } else {
            val isTenantTable = isTenantTable(jdbcCoordinator, tableName)
            val seqName = generateSequenceName(tableName, tenantId, isTenantTable)

            if (!sequenceExists(jdbcCoordinator, schemaName, seqName)) {
                createSequence(jdbcCoordinator, seqName, tenantId, isTenantTable)
                log.debug("Created new sequence: {}", seqName)
            }

            return executeNextVal(jdbcCoordinator, "$schemaName.$seqName")
                .also {
                    jdbcCoordinator.afterStatementExecution()
                    log.debug("Used tenant sequence: {} -> {}", seqName, it)
                }
        }
    }

    private fun sequenceExists(jdbcCoordinator: JdbcCoordinator, schemaName: String, sequenceName: String): Boolean {
        val sql = "SELECT EXISTS(SELECT 1 FROM pg_sequences WHERE schemaname = ? AND sequencename = ?)"
        val params = listOf(schemaName, sequenceName)
        return databaseService.executeQuery(jdbcCoordinator, sql, params).use { rs ->
            rs?.next() == true && rs.getBoolean(1)
        }
    }

    private fun isTenantTable(jdbcCoordinator: JdbcCoordinator, tableName: String): Boolean {
        val sql = "SELECT table_column_exists(?, 'tenant_id') AND ? != 'organisation'"
        val params = listOf(tableName, tableName)
        return databaseService.executeQuery(jdbcCoordinator, sql, params).use { rs ->
            rs?.next() == true && rs.getBoolean(1)
        }
    }

    private fun generateSequenceName(tableName: String, tenantId: Long?, isTenantTable: Boolean): String {
        return if (isTenantTable && tenantId != null) {
            "seq_${tableName}_$tenantId"
        } else {
            "seq_$tableName"
        }
    }

    private fun createSequence(jdbcCoordinator: JdbcCoordinator, seqName: String, tenantId: Long?, isTenantTable: Boolean) {
        val sql: String
        val params: List<Any>

        if (isTenantTable && tenantId != null) {
            val tenantDetails = getTenantSequenceDetails(jdbcCoordinator, tenantId)
            sql = "CREATE SEQUENCE $seqName START WITH ? MINVALUE ? MAXVALUE ?"
            params = listOf(tenantDetails.minId + 1, tenantDetails.minId, tenantDetails.maxId)
        } else {
            sql = "CREATE SEQUENCE $seqName START WITH 1"
            params = emptyList()
        }

        return databaseService.executeUpdate(jdbcCoordinator, sql, params)
    }

    private fun getTenantSequenceDetails(jdbcCoordinator: JdbcCoordinator, tenantId: Long): TenantSequenceDetails {
        val sql = "SELECT min_id + 1 AS min_id, max_id FROM tenant_sequence_details(?)"
        val params = listOf(tenantId)
        return databaseService.executeQuery(jdbcCoordinator, sql, params).use { rs ->
            if (rs?.next() == true) {
                TenantSequenceDetails(rs.getLong("min_id"), rs.getLong("max_id"))
            } else {
                throw SQLException("Failed to fetch tenant sequence details for tenantId: $tenantId")
            }
        }
    }

    private fun executeNextVal(jdbcCoordinator: JdbcCoordinator, sequenceName: String): Long {
        // PostgreSQL nextval() requires literal sequence name, not parameterized
        // Validate sequence name to prevent SQL injection
        require(sequenceName.matches(Regex("^[a-zA-Z_][a-zA-Z0-9_]*\\.[a-zA-Z_][a-zA-Z0-9_]*$|^[a-zA-Z_][a-zA-Z0-9_]*$"))) {
            "Invalid sequence name format: $sequenceName"
        }

        val sql = "SELECT nextval('$sequenceName')"
        val params = emptyList<Any>()

        return databaseService.executeQuery(jdbcCoordinator, sql, params).use { rs ->
            if (rs?.next() == true) {
                rs.getLong(1)
            } else {
                throw SQLException("Failed to get next value from sequence: $sequenceName")
            }
        }
    }

    private fun parseInputName(inputName: String, defaultSchemaName: String): Pair<String, String> {
        return if (inputName.contains('.')) {
            // Schema.table format provided
            val schemaName = inputName.substringBefore('.')
            val tableName = inputName.substringAfter('.')
            schemaName to tableName
        } else {
            // Only table name provided, use default schema
            defaultSchemaName to inputName
        }
    }
}
