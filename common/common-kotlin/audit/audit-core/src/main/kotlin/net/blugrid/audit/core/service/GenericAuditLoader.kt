package net.blugrid.api.common.persistence.audit

import net.blugrid.audit.core.service.AuditEventEmitterServiceImpl
import net.blugrid.audit.core.service.AuditEventLogService
import net.blugrid.common.model.audit.AuditEventType
import net.blugrid.common.model.resource.BaseResource
import net.blugrid.common.model.resource.BaseTenantResource
import net.blugrid.common.model.resource.ResourceType
import net.blugrid.data.persistence.service.GenericQueryService
import java.time.LocalDateTime

abstract class GenericAuditLoader<T : BaseResource<T>> {
    abstract fun reload(resourceType: ResourceType)
    abstract fun isEmpty(resourceType: ResourceType): Boolean
}

open class GenericAuditLoaderImpl<T : BaseTenantResource<T>>(
    private val auditEventLogService: AuditEventLogService,
    private val auditEventEmitterService: AuditEventEmitterServiceImpl,
    private val stateService: GenericQueryService<*, T>
) : GenericAuditLoader<T>() {

    override fun reload(resourceType: ResourceType) {
        stateService.getAll().forEach {
            auditEventEmitterService.publishEvent(
                eventType = AuditEventType.CREATE,
                resourceType = it.resourceType,
                resource = it,
                resourceId = it.id,
                tenantId = it.scope?.tenantId ?: error("Missing tenantId"),
                sessionId = it.audit?.created?.sessionId ?: error("Missing createdBySessionId"),
                version = it.audit?.version ?: 0,
                localDateTime = it.audit?.lastChanged?.timestamp ?: LocalDateTime.now()
            )
        }
    }

    override fun isEmpty(resourceType: ResourceType): Boolean =
        auditEventLogService.isEmpty(resourceType)
}
