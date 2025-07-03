package net.blugrid.api.common.persistence.model.resource

import net.blugrid.api.common.persistence.audit.AuditEmbeddable
import net.blugrid.api.common.persistence.model.AuditablePersistable
import net.blugrid.api.common.persistence.model.PersistableResource
import net.blugrid.api.common.persistence.scope.BusinessUnitScopedEntity

interface BusinessUnitScopedResource<T> : PersistableResource<T>, BusinessUnitScopedEntity, AuditablePersistable {

    override var audit: AuditEmbeddable
}
