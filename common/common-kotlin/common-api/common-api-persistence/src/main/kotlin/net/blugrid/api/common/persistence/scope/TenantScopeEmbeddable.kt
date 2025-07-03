package net.blugrid.api.common.persistence.scope

import io.micronaut.data.annotation.Embeddable

@Embeddable
data class TenantScopeEmbeddable(
    override var tenantId: Long? = null
) : TenantScoped {

    fun applyPermissionContext(tenantId: Long?) {
        this.tenantId = tenantId
    }
}
