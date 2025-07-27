package net.blugrid.data.persistence.mapping

import net.blugrid.common.model.audit.AuditStamp
import net.blugrid.common.model.audit.ResourceAudit
import net.blugrid.data.persistence.audit.AuditEmbeddable
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
