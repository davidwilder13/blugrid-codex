package net.blugrid.api.audit.event

import io.micronaut.runtime.event.annotation.EventListener
import jakarta.inject.Singleton
import net.blugrid.api.audit.service.AuditEventLogService
import net.blugrid.api.logging.logger
import net.blugrid.api.common.model.audit.AuditEvent
import net.blugrid.api.json.objectToJson

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
