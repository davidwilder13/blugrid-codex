package net.blugrid.api.common.persistence.model.resource

import net.blugrid.api.common.persistence.audit.AuditEmbeddable
import net.blugrid.api.common.persistence.model.PersistableResource

interface UnscopedPersistable<T> : PersistableResource<T> {

    var audit: AuditEmbeddable
}
