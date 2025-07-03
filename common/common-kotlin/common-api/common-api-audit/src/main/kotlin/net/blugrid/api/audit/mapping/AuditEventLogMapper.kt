package net.blugrid.api.audit.mapping

import net.blugrid.api.audit.repository.model.AuditEventLogInsertEntity
import net.blugrid.api.audit.repository.model.AuditEventLogReadEntity
import net.blugrid.api.common.model.audit.AuditEvent
import net.blugrid.api.common.model.audit.AuditEventLog
import net.blugrid.api.common.model.resource.BaseAuditedResource
import net.blugrid.common.domain.IdentityID
import java.util.UUID

fun AuditEventLogInsertEntity.toAuditEventLog() =
    AuditEventLog(
        auditEventType = this.auditEventType,
        auditEventTimestamp = this.auditEventTimestamp,
        resourceType = this.resourceType,
        resourceId = IdentityID(this.resourceId),
        resource = this.resource,
        tenantId = IdentityID(this.tenantId),
        sessionId = IdentityID(this.sessionId),
        version = 0 // ToDo add read entity
    )

fun AuditEventLogInsertEntity.toAuditEvent() =
    AuditEvent(
        auditEventType = this.auditEventType,
        auditEventTimestamp = this.auditEventTimestamp,
        resourceType = this.resourceType,
        resourceId = IdentityID(this.resourceId),
        resource = this.resource,
        tenantId = IdentityID(this.tenantId),
        sessionId = IdentityID(this.sessionId),
    )

fun AuditEventLogReadEntity.toAuditEventLog() =
    AuditEventLog(
        auditEventType = this.auditEventType,
        auditEventTimestamp = this.auditEventTimestamp,
        resourceType = this.resourceType,
        resourceId = IdentityID(this.resourceId),
        resource = this.resource,
        tenantId = IdentityID(this.tenantId),
        sessionId = IdentityID(this.sessionId),
        version = 0 // ToDo add read entity
    )

fun AuditEventLogReadEntity.toAuditEvent() =
    AuditEvent(
        auditEventType = this.auditEventType,
        auditEventTimestamp = this.auditEventTimestamp,
        resourceType = this.resourceType,
        resourceId = IdentityID(this.resourceId),
        resource = this.resource as BaseAuditedResource<*>,
        tenantId = IdentityID(this.tenantId),
        sessionId = IdentityID(this.sessionId),
    )

fun AuditEvent.toAuditEventLogUpdateEntity() =
    AuditEventLogInsertEntity(
        id = UUID.randomUUID(),
        auditEventType = this.auditEventType,
        auditEventTimestamp = this.auditEventTimestamp,
        resourceType = this.resourceType,
        resourceId = this.resourceId.value,
        resource = this.resource,
        tenantId = this.tenantId.value,
        sessionId = this.sessionId.value,
    )
