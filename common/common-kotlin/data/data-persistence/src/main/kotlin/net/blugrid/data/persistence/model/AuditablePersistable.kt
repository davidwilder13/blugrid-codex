package net.blugrid.data.persistence.model

import net.blugrid.data.persistence.audit.AuditEmbeddable

interface AuditablePersistable {
    var audit: AuditEmbeddable
}
