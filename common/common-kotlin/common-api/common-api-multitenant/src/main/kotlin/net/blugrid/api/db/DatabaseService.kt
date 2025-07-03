package net.blugrid.api.db

import jakarta.inject.Singleton
import org.hibernate.engine.jdbc.spi.JdbcCoordinator
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.collections.forEachIndexed
import kotlin.collections.indices
import kotlin.let

interface DatabaseService {
    fun executeQuery(jdbcCoordinator: JdbcCoordinator, sql: String, params: List<Any>? = null): ResultSet?
    fun releaseResources(jdbcCoordinator: JdbcCoordinator, resultSet: ResultSet?, preparedStatement: PreparedStatement?)
    fun executeUpdate(jdbcCoordinator: JdbcCoordinator, sql: String, params: List<Any>)
}

@Singleton
class DatabaseServiceImpl : DatabaseService {

    override fun executeQuery(jdbcCoordinator: JdbcCoordinator, sql: String, params: List<Any>?): ResultSet? {
        val preparedStatement: PreparedStatement = jdbcCoordinator.statementPreparer.prepareStatement(sql)
        if (params != null) {
            for (i in params.indices) {
                preparedStatement.setObject(i + 1, params[i])
            }
        }

        return try {
            jdbcCoordinator.resultSetReturn.extract(preparedStatement, sql)
        } catch (e: SQLException) {
            jdbcCoordinator.logicalConnection.resourceRegistry.release(preparedStatement)
            jdbcCoordinator.afterStatementExecution()
            throw e
        }
    }

    override fun executeUpdate(jdbcCoordinator: JdbcCoordinator, sql: String, params: List<Any>) {
        val preparedStatement: PreparedStatement = jdbcCoordinator.statementPreparer.prepareStatement(sql)
        try {
            // Set parameters
            params.forEachIndexed { index, param ->
                preparedStatement.setObject(index + 1, param)
            }

            // Execute the update
            preparedStatement.executeUpdate()
        } finally {
            // Release the prepared statement
            jdbcCoordinator.logicalConnection.resourceRegistry.release(preparedStatement)
            jdbcCoordinator.afterStatementExecution()
        }
    }


    override fun releaseResources(jdbcCoordinator: JdbcCoordinator, resultSet: ResultSet?, preparedStatement: PreparedStatement?) {
        try {
            resultSet?.let {
                jdbcCoordinator.logicalConnection.resourceRegistry.release(it, preparedStatement)
            }
        } finally {
            preparedStatement?.let {
                jdbcCoordinator.logicalConnection.resourceRegistry.release(it)
            }
            jdbcCoordinator.afterStatementExecution()
        }
    }
}
