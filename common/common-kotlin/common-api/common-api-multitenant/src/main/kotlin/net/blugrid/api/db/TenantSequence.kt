package net.blugrid.api.db

import jakarta.inject.Singleton
import net.blugrid.api.config.DbProps
import org.hibernate.engine.jdbc.spi.JdbcCoordinator
import org.hibernate.engine.spi.SharedSessionContractImplementor
import java.sql.SQLException

interface TenantSequence {
    fun tenantNextVal(session: SharedSessionContractImplementor, inputName: String, tenantId: Long): Long
}

@Singleton
class TenantSequenceImpl(
    private val DbProps: DbProps,
    private val databaseService: DatabaseService,
) : TenantSequence {

    override fun tenantNextVal(session: SharedSessionContractImplementor, inputName: String, tenantId: Long): Long {
        val jdbcCoordinator = session.jdbcCoordinator
        val defaultSchemaName = DbProps.schema
        val (schemaName, tableName) = parseInputName(inputName, defaultSchemaName)

        // 4. Check if the input_name (including schema) refers to an existing sequence
        if (sequenceExists(jdbcCoordinator, schemaName, tableName)) {
            // If table_name already refers to a sequence, call nextval directly
            return executeNextVal(jdbcCoordinator, inputName)
                .also { jdbcCoordinator.afterStatementExecution() }
        } else {
            // 5. Generate the sequence name based on table and tenant_id
            val isTenantTable = isTenantTable(jdbcCoordinator, tableName)
            val seqName = generateSequenceName(tableName, tenantId, isTenantTable)

            // 7. Check if the sequence already exists
            if (!sequenceExists(jdbcCoordinator, schemaName, seqName)) {
                // 8. If the sequence does not exist, create it with the appropriate settings
                createSequence(jdbcCoordinator, seqName, tenantId, isTenantTable)
            }

            // 11. Use the sequence to get the next ID value
            return executeNextVal(jdbcCoordinator, "$schemaName.$seqName")
                .also { jdbcCoordinator.afterStatementExecution() }
        }
    }

    private fun parseInputName(inputName: String, defaultSchemaName: String): Pair<String, String> {
        val schemaName = inputName.substringBefore('.', defaultSchemaName)
        val tableName = inputName.substringAfter('.', inputName)
        return schemaName to tableName
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
        val sql = "SELECT nextval(?)"
        val params = listOf(sequenceName)
        return databaseService.executeQuery(jdbcCoordinator, sql, params).use { rs ->
            if (rs?.next() == true) {
                rs.getLong(1)
            } else {
                throw SQLException("Failed to get next value from sequence: $sequenceName")
            }
        }
    }
}

data class TenantSequenceDetails(val minId: Long, val maxId: Long)
