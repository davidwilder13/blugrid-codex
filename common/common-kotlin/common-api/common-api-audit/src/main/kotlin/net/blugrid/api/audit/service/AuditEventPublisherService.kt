package net.blugrid.api.audit.service

import io.micronaut.context.event.ApplicationEventPublisher
import jakarta.inject.Singleton
import net.blugrid.api.logging.logger
import net.blugrid.api.common.model.audit.AuditEvent
import net.blugrid.api.common.model.audit.AuditEventType
import net.blugrid.api.common.model.resource.BaseAuditedResource
import net.blugrid.api.common.model.resource.ResourceType
import net.blugrid.common.domain.IdentityID
import java.time.LocalDateTime

interface AuditEventEmitterService {
    fun publishEvent(
        eventType: AuditEventType,
        resourceType: ResourceType,
        resource: BaseAuditedResource<*>,
        resourceId: IdentityID,
        tenantId: IdentityID,
        sessionId: IdentityID,
        version: Int,
        localDateTime: LocalDateTime
    )
}

@Singleton
class AuditEventEmitterServiceImpl(
    private val publisher: ApplicationEventPublisher<AuditEvent>
) : AuditEventEmitterService {

    private val logger = logger()

    override fun publishEvent(
        eventType: AuditEventType,
        resourceType: ResourceType,
        resource: BaseAuditedResource<*>,
        resourceId: IdentityID,
        tenantId: IdentityID,
        sessionId: IdentityID,
        version: Int,
        localDateTime: LocalDateTime
    ) {
        logger.debug("publishing $eventType event for $resourceType id: $resourceId version:$version")
        publisher.publishEvent(
            AuditEvent(
                auditEventType = eventType,
                auditEventTimestamp = localDateTime,
                resourceId = resourceId,
                resourceType = resourceType,
                resource = resource,
                version = version,
                tenantId = tenantId,
                sessionId = sessionId,
            )
        )
    }
}

