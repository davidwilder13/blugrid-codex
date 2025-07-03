package net.blugrid.api.common.persistence.mapping

import net.blugrid.api.common.model.audit.AuditStamp
import net.blugrid.api.common.model.audit.ResourceAudit
import net.blugrid.api.common.persistence.audit.AuditEmbeddable
import net.blugrid.common.domain.IdentityID

fun AuditEmbeddable.toResourceAudit(): ResourceAudit =
    ResourceAudit(
        version = this.version,
        created = if (createdBySessionId != null && createdTimestamp != null) {
            AuditStamp(sessionId = IdentityID(createdBySessionId!!), session = null, timestamp = createdTimestamp!!)
        } else null,
        lastChanged = if (lastChangedBySessionId != null && lastChangedTimestamp != null) {
            AuditStamp(sessionId = IdentityID(lastChangedBySessionId!!), session = null, timestamp = lastChangedTimestamp!!)
        } else null
    )
