package net.blugrid.api.common.model.audit

import net.blugrid.api.common.model.resource.ResourceType
import net.blugrid.common.domain.IdentityID
import java.time.LocalDateTime

class AuditEventLog(
    val auditEventType: AuditEventType,
    val auditEventTimestamp: LocalDateTime,
    val resourceType: ResourceType,
    val resourceId: IdentityID,
    val resource: Any,
    val tenantId: IdentityID,
    val sessionId: IdentityID,
    val version: Int? = 0
)
