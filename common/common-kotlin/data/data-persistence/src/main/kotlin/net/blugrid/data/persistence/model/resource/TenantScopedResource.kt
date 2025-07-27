package net.blugrid.data.persistence.model.resource

import net.blugrid.data.persistence.audit.AuditEmbeddable
import net.blugrid.data.persistence.model.AuditablePersistable
import net.blugrid.data.persistence.model.PersistableResource
import net.blugrid.data.persistence.scope.TenantScopedEntity

interface TenantScopedResource<T> : PersistableResource<T>, TenantScopedEntity, AuditablePersistable {

    override var audit: AuditEmbeddable
}
