package net.blugrid.api.common.persistence.model

import net.blugrid.api.common.persistence.audit.AuditEmbeddable

interface AuditablePersistable {
    var audit: AuditEmbeddable
}
