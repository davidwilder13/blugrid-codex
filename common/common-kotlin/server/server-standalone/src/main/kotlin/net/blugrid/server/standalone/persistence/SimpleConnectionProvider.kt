package net.blugrid.server.standalone.persistence

import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import net.blugrid.server.api.config.ServerMode
import net.blugrid.server.api.persistence.ConnectionProvider
import net.blugrid.server.api.tenant.TenantContext
import javax.sql.DataSource

/**
 * Simple connection provider for standalone deployments (POS, kiosks, etc.)
 * No multi-tenancy, no PostgreSQL functions, just basic database access.
 */
@Singleton
@Requires(property = "app.server.mode", value = "standalone", defaultValue = "standalone")
class SimpleConnectionProvider(
    private val dataSource: DataSource
) : ConnectionProvider {

    override fun getDataSource(tenantContext: TenantContext): DataSource {
        // In standalone mode, always return the same datasource regardless of context
        return dataSource
    }

    override fun getDefaultDataSource(): DataSource {
        return dataSource
    }

    override fun supports(mode: ServerMode): Boolean {
        return mode == ServerMode.STANDALONE
    }
}
