package net.blugrid.api.audit.model

import net.blugrid.api.common.model.audit.AuditEventType
import net.blugrid.api.common.model.resource.ResourceType
import java.time.LocalDateTime

class AuditEventLogQuery(
    val resourceTypes: List<ResourceType>,
    val resourceIds: List<Long>? = null,
    val auditEventTypes: List<AuditEventType>? = null,
    val tenantIds: List<Long>? = null,
    val partyIds: List<Long>? = null,

    val minDateTime: LocalDateTime? = null,

    val maxDateTime: LocalDateTime? = null,
)
