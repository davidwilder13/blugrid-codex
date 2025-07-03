package net.blugrid.api.common.persistence.scope

import io.micronaut.data.annotation.Embeddable
import net.blugrid.api.security.context.CurrentRequestContext

@Embeddable
data class TenantScopeEmbeddable(
    override var tenantId: Long? = null
) : TenantScoped {

    fun prePersist() {
        tenantId = CurrentRequestContext.currentTenantId
    }
}
