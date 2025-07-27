package net.blugrid.server.api.tenant

import net.blugrid.server.api.config.ServerMode

data class TenantContext(
    val tenantId: String,
    val tenantName: String? = null,
    val mode: ServerMode = ServerMode.MULTI_TENANT
) {
    companion object {
        val STANDALONE = TenantContext(
            tenantId = "default",
            tenantName = "Default",
            mode = ServerMode.STANDALONE
        )

        fun multiTenant(tenantId: String, tenantName: String? = null) =
            TenantContext(tenantId, tenantName, ServerMode.MULTI_TENANT)
    }

    val isStandalone: Boolean
        get() = mode == ServerMode.STANDALONE
}
