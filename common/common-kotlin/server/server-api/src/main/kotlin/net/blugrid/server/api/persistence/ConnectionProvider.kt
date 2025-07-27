package net.blugrid.server.api.persistence

import net.blugrid.server.api.config.ServerMode
import net.blugrid.server.api.tenant.TenantContext
import javax.sql.DataSource

interface ConnectionProvider {
    fun getDataSource(tenantContext: TenantContext): DataSource
    fun getDefaultDataSource(): DataSource
    fun supports(mode: ServerMode): Boolean
}
