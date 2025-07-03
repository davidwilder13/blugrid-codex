package net.blugrid.api.common.persistence.scope

import io.micronaut.data.annotation.Embeddable

@Embeddable
data class BusinessUnitScopeEmbeddable(
    override var tenantId: Long? = null,

    override var businessUnitId: Long? = null
) : BusinessUnitScoped {


    fun applyPermissionContext(businessUnitId: Long?, tenantId: Long?) {
        this.businessUnitId = businessUnitId
        this.tenantId = tenantId
    }
}
