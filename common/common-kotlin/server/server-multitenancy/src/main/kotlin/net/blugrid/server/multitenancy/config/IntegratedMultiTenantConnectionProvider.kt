package net.blugrid.server.multitenancy.config

import jakarta.inject.Singleton
import net.blugrid.platform.logging.logger
import net.blugrid.security.core.context.RequestContext
import net.blugrid.security.core.session.BusinessUnitSession
import net.blugrid.security.core.session.TenantSession
import net.blugrid.server.api.config.ServerMode
import net.blugrid.server.api.persistence.ConnectionProvider
import net.blugrid.server.api.tenant.TenantContext
import org.hibernate.HibernateException
import org.hibernate.cfg.AvailableSettings
import org.hibernate.engine.config.spi.ConfigurationService
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl
import org.hibernate.service.spi.ServiceRegistryAwareService
import org.hibernate.service.spi.ServiceRegistryImplementor
import org.hibernate.service.spi.Stoppable
import java.sql.Connection
import java.sql.SQLException
import java.sql.Types
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource

/**
 * Your existing production-ready MultiTenantConnectionProvider, now integrated
 * with the server-api abstraction layer for framework compatibility.
 *
 * This implementation provides:
 * - Database-level row-level security via PostgreSQL functions
 * - Multi-level scoping (tenant, business unit, session)
 * - Production connection management
 * - Integration with both old and new APIs
 */
@Suppress("UNCHECKED_CAST")
@Singleton
open class IntegratedMultiTenantConnectionProvider(
    private val securityContextService: RequestContext
) : AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String>(),
    ConnectionProvider,
    ServiceRegistryAwareService,
    Stoppable {

    private val log = logger()
    private var dataSourceMap: Map<String, DataSource>? = null
    private var tenantIdentifierForAny: String? = null

    companion object {
        const val DEFAULT_TENANT_ID = "default"
        const val TENANT_IDENTIFIER_TO_USE_FOR_ANY_KEY = "hibernate.multi_tenant.datasource.identifier_for_any"
    }

    override fun getDataSource(tenantContext: TenantContext): DataSource {
        return when {
            tenantContext.isStandalone -> getDefaultDataSource()
            else -> selectDataSource(tenantContext.tenantId)
        }
    }

    override fun getDefaultDataSource(): DataSource {
        return selectDataSource(DEFAULT_TENANT_ID)
    }

    override fun supports(mode: ServerMode): Boolean {
        return mode == ServerMode.MULTI_TENANT
    }

    override fun selectAnyDataSource(): DataSource {
        return selectDataSource(tenantIdentifierForAny ?: DEFAULT_TENANT_ID)
    }

    override fun selectDataSource(tenantIdentifier: String): DataSource {
        return dataSourceMap()[tenantIdentifier]
            ?: throw HibernateException("Could not locate datasource for tenantIdentifier: $tenantIdentifier")
    }

    override fun getAnyConnection(): Connection {
        return getConnection("1")
    }

    /**
     * Core connection configuration method - handles all the sophisticated
     * database-level security that your system depends on.
     */
    override fun getConnection(regionId: String): Connection {
        val connection: Connection
        try {
            connection = selectAnyDataSource().connection
            val schema = connection.schema
            val unscoped = securityContextService.currentIsUnscoped
            val currentTenantId = securityContextService.currentTenantId
            val currentBusinessUnitId = securityContextService.currentBusinessUnitId
            val currentSession = securityContextService.currentSession

            when {
                unscoped -> {
                    log.debug("Configuring connection - unscoped override found - no scoping needed")
                    connection.configureUnscoped(schema)
                }

                currentSession is TenantSession && currentTenantId != null -> {
                    log.debug("Configuring connection - WebApplication session found in SecurityContext - scoping to TenantId: $currentTenantId")
                    connection.configureTenantSessionScope(schema, currentTenantId.toString(), currentSession.sessionId)
                }

                currentSession is BusinessUnitSession && currentTenantId != null && currentBusinessUnitId != null -> {
                    log.debug("Configuring connection - Business unit session found in SecurityContext - scoping to TenantId: $currentTenantId, BusinessUnitId: $currentBusinessUnitId")
                    connection.configureBusinessUnitSessionScope(schema, currentTenantId.toString(), currentBusinessUnitId.toString(), currentSession.sessionId)
                }

                currentTenantId != null && currentBusinessUnitId != null -> {
                    log.debug("Configuring connection - Business unit session found in SecurityContext - scoping to TenantId: $currentTenantId, BusinessUnitId: $currentBusinessUnitId")
                    connection.configureBusinessUnitIdScope(schema, currentTenantId.toString(), currentBusinessUnitId.toString())
                }

                currentTenantId != null -> {
                    log.debug("Configuring connection - WebApplication session found in SecurityContext - scoping to TenantId: $currentTenantId")
                    connection.configureTenantIdScope(schema, currentTenantId.toString())
                }

                else -> {
                    log.debug("Configuring connection - no scoping needed")
                    connection.configureUnscoped(schema)
                }
            }
        } catch (e: SQLException) {
            throw SQLException("Could not alter JDBC connection: ", e)
        }
        return connection
    }

    override fun releaseAnyConnection(connection: Connection) {
        this.releaseConnection("1", connection)
    }

    override fun releaseConnection(regionId: String, connection: Connection) {
        log.debug("Releasing connection")
        connection.close()
    }

    override fun supportsAggressiveRelease(): Boolean {
        return true
    }

    private fun dataSourceMap(): MutableMap<String, DataSource?> {
        if (dataSourceMap == null) {
            dataSourceMap = ConcurrentHashMap()
        }
        return dataSourceMap as MutableMap<String, DataSource?>
    }

    override fun injectServices(serviceRegistry: ServiceRegistryImplementor) {
        @Suppress("DEPRECATION")
        val dataSourceConfigValue = serviceRegistry.getService(ConfigurationService::class.java)
            ?.settings?.get(AvailableSettings.DATASOURCE)
        if (dataSourceConfigValue == null) {
            throw HibernateException("Improper set up of DataSourceBasedMultiTenantConnectionProviderImpl")
        }
        log.info("Default data source: $dataSourceConfigValue")
        dataSourceMap = mapOf(DEFAULT_TENANT_ID to dataSourceConfigValue as DataSource)
    }

    override fun stop() {
        if (dataSourceMap != null) {
            dataSourceMap = null
        }
    }
}

// ===== KEEP ALL YOUR EXISTING DATABASE CONFIGURATION METHODS =====

private fun Connection.configureUnscoped(schema: String) {
    prepareCall("select pg_catalog.set_config('search_path', ?, false)")
        .use { statement ->
            statement.setString(1, schema)
            statement.execute()
        }
}

private fun Connection.configureTenantIdScope(schema: String, tenantId: String) {
    prepareCall("{? = CALL set_tenant_session(CAST(? AS TEXT), CAST(? AS TEXT)) }")
        .use { statement ->
            statement.registerOutParameter(1, Types.INTEGER)
            statement.setString(2, schema)
            statement.setString(3, tenantId)
            statement.execute()
        }
}

private fun Connection.configureTenantSessionScope(schema: String, tenantId: String, sessionId: String) {
    prepareCall("{? = CALL set_tenant_session(CAST(? AS TEXT), CAST(? AS TEXT), CAST(? AS TEXT)) }")
        .use { statement ->
            statement.registerOutParameter(1, Types.INTEGER)
            statement.setString(2, schema)
            statement.setString(3, tenantId)
            statement.setString(4, sessionId)
            statement.execute()
        }
}

private fun Connection.configureBusinessUnitIdScope(schema: String, tenantId: String, businessUnitId: String) {
    prepareCall("{? = CALL set_business_unit_session(CAST(? AS TEXT), CAST(? AS TEXT), CAST(? AS TEXT)) }")
        .use { statement ->
            statement.registerOutParameter(1, Types.INTEGER)
            statement.setString(2, schema)
            statement.setString(3, tenantId)
            statement.setString(4, businessUnitId)
            statement.execute()
        }
}

private fun Connection.configureBusinessUnitSessionScope(schema: String, tenantId: String, businessUnitId: String, sessionId: String) {
    prepareCall("{? = CALL set_business_unit_session(CAST(? AS TEXT), CAST(? AS TEXT), CAST(? AS TEXT), CAST(? AS TEXT)) }")
        .use { statement ->
            statement.registerOutParameter(1, Types.INTEGER)
            statement.setString(2, schema)
            statement.setString(3, tenantId)
            statement.setString(4, businessUnitId)
            statement.setString(5, sessionId)
            statement.execute()
        }
}
