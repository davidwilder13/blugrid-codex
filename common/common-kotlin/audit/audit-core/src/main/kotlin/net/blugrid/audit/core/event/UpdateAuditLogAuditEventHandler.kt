package net.blugrid.audit.core.event

import io.micronaut.runtime.event.annotation.EventListener
import jakarta.inject.Singleton
import net.blugrid.audit.core.service.AuditEventLogService
import net.blugrid.platform.logging.logger
import net.blugrid.common.model.audit.AuditEvent
import net.blugrid.platform.serialization.objectToJson

@Singleton
open class UpdateAuditLogAuditEventHandler(
    private val auditEventLogService: AuditEventLogService
) {

    private val log = logger()

    @EventListener
    open fun handle(event: AuditEvent) {
        log.debug("received audit ${event.auditEventType} event for ${event.resourceType}")
        log.trace("received audit ${event.auditEventType} event for ${event.resourceType}:  ${objectToJson(event)}")
        auditEventLogService.createAuditEventLog(event)
    }
}
