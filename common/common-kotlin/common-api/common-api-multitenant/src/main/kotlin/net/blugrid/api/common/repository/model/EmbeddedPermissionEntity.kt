package net.blugrid.api.common.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import net.blugrid.api.security.authentication.model.AuthenticatedOrganisation
import net.blugrid.api.security.context.CurrentRequestContext

@Embeddable
class EmbeddedTenantPermissionEntity(
    @Column(name = "tenant_id", updatable = false)
    override var tenantId: Long? = null

) : ITenantPermissionEntity {

    fun prePersist() {
        tenantId = CurrentRequestContext.currentTenantId
    }
}

@Embeddable
class EmbeddedBusinessUnitPermissionEntity(
    @Column(name = "business_unit_id", updatable = false)
    override var businessUnitId: Long? = CurrentRequestContext.currentBusinessUnitId,

    @Column(name = "tenant_id", updatable = false)
    override var tenantId: Long? = CurrentRequestContext.currentTenantId
) : IBusinessUnitPermissionEntity {

    fun prePersist() {
        businessUnitId = CurrentRequestContext.currentBusinessUnitId
        tenantId = CurrentRequestContext.currentTenantId
    }
}
