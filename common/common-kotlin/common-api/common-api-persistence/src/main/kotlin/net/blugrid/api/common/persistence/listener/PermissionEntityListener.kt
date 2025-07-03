package net.blugrid.api.common.persistence.listener

import io.micronaut.context.annotation.Context
import io.micronaut.data.annotation.event.PrePersist
import jakarta.inject.Inject
import net.blugrid.api.common.persistence.model.resource.BusinessUnitScopedResource
import net.blugrid.api.common.persistence.model.resource.TenantScopedResource
import net.blugrid.api.common.security.context.RequestContextProvider

@Context
class PermissionEntityListener @Inject constructor(
    private val context: RequestContextProvider
) {

    @PrePersist
    fun onPersist(entity: Any) {
        when (entity) {
            is TenantScopedResource<*> -> {
                entity.permission.applyPermissionContext(context.currentTenantId)
            }

            is BusinessUnitScopedResource<*> -> {
                entity.permission.applyPermissionContext(
                    context.currentBusinessUnitId,
                    context.currentTenantId
                )
            }
        }
    }
}
