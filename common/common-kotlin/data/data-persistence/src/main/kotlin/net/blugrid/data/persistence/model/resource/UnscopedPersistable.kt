package net.blugrid.data.persistence.model.resource

import net.blugrid.data.persistence.audit.AuditEmbeddable
import net.blugrid.data.persistence.model.AuditablePersistable
import net.blugrid.data.persistence.model.PersistableResource

interface UnscopedPersistable<T> : PersistableResource<T>, AuditablePersistable {

    override var audit: AuditEmbeddable
}
