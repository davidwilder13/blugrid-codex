package net.blugrid.api.common.persistence.model.resource

import net.blugrid.api.common.persistence.audit.AuditEmbeddable
import net.blugrid.api.common.persistence.model.AuditablePersistable
import net.blugrid.api.common.persistence.model.PersistableResource

interface UnscopedPersistable<T> : PersistableResource<T>, AuditablePersistable {

    override var audit: AuditEmbeddable
}
