package net.blugrid.api.audit.service

import net.blugrid.api.common.audit.service.AuditEventEmitterServiceImpl
import net.blugrid.api.common.model.audit.AuditEventType
import net.blugrid.api.common.model.resource.GenericCreateResource
import net.blugrid.api.common.model.resource.GenericUpdateResource
import net.blugrid.api.common.model.resource.ResourceType
import net.blugrid.api.common.model.resource.TenantResource
import net.blugrid.api.common.repository.model.GenericEntity
import net.blugrid.api.common.repository.model.GenericEntityMapper
import net.blugrid.api.common.service.GenericCrudService
import java.time.LocalDateTime

abstract class GenericAuditLoader<
    T : TenantResource<T>,
    U : GenericCreateResource<U>,
    V : GenericUpdateResource<V>,
    X : GenericEntity<X>,
    Y : GenericEntityMapper<T, U, V, X>
    > {
    abstract fun reload(resourceType: ResourceType)
    abstract fun isEmpty(resourceType: ResourceType): Boolean
}

open class GenericAuditLoaderImpl<
    T : TenantResource<T>,
    U : GenericCreateResource<U>,
    V : GenericUpdateResource<V>,
    X : GenericEntity<X>,
    Y : GenericEntityMapper<T, U, V, X>
    >(
    private val auditEventLogService: AuditEventLogService,
    private val auditEventEmitterService: AuditEventEmitterServiceImpl,
    private val stateService: GenericCrudService<T, U, V, X, Y>
) : GenericAuditLoader<T, U, V, X, Y>() {

    override fun reload(resourceType: ResourceType) {
        stateService.getAll().forEach {
            auditEventEmitterService.publishEvent(
                eventType = AuditEventType.CREATE,
                resourceType = it.resourceType,
                resource = it,
                resourceId = it.id,
                tenantId = it.permission?.tenantId!!,
                sessionId = it.audit?.createdBySessionId!!,
                version = 1,
                localDateTime = LocalDateTime.now()
            )
        }
    }

    override fun isEmpty(resourceType: ResourceType): Boolean {
        return this.auditEventLogService.isEmpty(resourceType)
    }
}


