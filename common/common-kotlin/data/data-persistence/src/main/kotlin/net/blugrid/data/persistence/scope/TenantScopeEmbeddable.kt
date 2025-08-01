package net.blugrid.data.persistence.scope

import io.micronaut.data.annotation.Embeddable
import net.blugrid.security.core.context.RequestContext

@Embeddable
data class TenantScopeEmbeddable(
    override var tenantId: Long? = null
) : TenantScoped {

    fun prePersist() {
        tenantId = RequestContext.currentTenantId
    }
}
