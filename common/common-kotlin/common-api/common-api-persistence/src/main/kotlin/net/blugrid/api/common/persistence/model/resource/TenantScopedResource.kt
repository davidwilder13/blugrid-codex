package net.blugrid.api.common.persistence.model.resource

import net.blugrid.api.common.persistence.audit.AuditEmbeddable
import net.blugrid.api.common.persistence.model.AuditablePersistable
import net.blugrid.api.common.persistence.model.PersistableResource
import net.blugrid.api.common.persistence.scope.TenantScopedEntity

interface TenantScopedResource<T> : PersistableResource<T>, TenantScopedEntity, AuditablePersistable {

    override var audit: AuditEmbeddable
}
