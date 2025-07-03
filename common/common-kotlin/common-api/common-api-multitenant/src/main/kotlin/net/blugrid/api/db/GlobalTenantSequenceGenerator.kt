package net.blugrid.api.db

import net.blugrid.api.logging.logger
import net.blugrid.api.security.context.CurrentRequestContext
import org.hibernate.HibernateException
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.hibernate.id.enhanced.SequenceStyleGenerator
import org.hibernate.service.ServiceRegistry
import org.hibernate.type.Type
import java.io.Serializable
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Types
import java.util.Properties

class GlobalTenantSequenceGenerator : SequenceStyleGenerator(), IdentifierGenerator {

    private val log = logger()

    private lateinit var sequenceName: String

    override fun configure(type: Type, params: Properties, serviceRegistry: ServiceRegistry) {
        super<SequenceStyleGenerator>.configure(type, params, serviceRegistry)
        this.sequenceName = params.getProperty("target_table")
            .removePrefix("vw_")
            .removeSuffix("_##scope_option##")
    }

    override fun generate(session: SharedSessionContractImplementor, entity: Any?): Serializable {
        val tenantId = CurrentRequestContext.currentTenantId
        log.debug("Generating Id sequenceName: $sequenceName for tenantId: $tenantId")

        val jdbcCoordinator = session.jdbcCoordinator
        var nextValue: Long = 1

        val sql = if (tenantId != null) {
            "SELECT tenant_nextval(?,?)"
        } else {
            "SELECT unscoped_nextval(?)"
        }

        try {
            jdbcCoordinator.statementPreparer.prepareStatement(sql).use { preparedStatement ->
                prepareStatement(preparedStatement, tenantId)
                jdbcCoordinator.resultSetReturn.extract(preparedStatement, sql).use { resultSet ->
                    if (resultSet.next()) {
                        nextValue = resultSet.getLong(1)
                        log.trace("Sequence value obtained: $nextValue")
                    } else {
                        throw HibernateException("No value obtained for sequence.")
                    }
                }
            }
            jdbcCoordinator.afterStatementExecution()
        } catch (sqlException: SQLException) {
            throw session.jdbcServices.sqlExceptionHelper.convert(sqlException, "Could not get next sequence value")
        }

        return nextValue
    }

    private fun prepareStatement(preparedStatement: PreparedStatement, tenantId: Long?) {
        if (tenantId != null) {
            preparedStatement.setString(1, sequenceName)
            preparedStatement.setObject(2, tenantId, Types.BIGINT)
        } else {
            preparedStatement.setString(1, sequenceName)
        }
    }
}
