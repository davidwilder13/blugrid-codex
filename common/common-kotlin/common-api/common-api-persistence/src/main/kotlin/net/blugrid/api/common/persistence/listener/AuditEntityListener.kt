package net.blugrid.api.common.persistence.listener

import io.micronaut.context.annotation.Context
import jakarta.inject.Inject
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import net.blugrid.api.common.persistence.model.AuditableEntity
import net.blugrid.api.common.security.context.RequestContextProvider

@Context
class AuditEntityListener @Inject constructor(
    private val context: RequestContextProvider
) {
    @PrePersist
    fun setCreateAudit(entity: Any) {
        if (entity is AuditableEntity) {
            entity.audit.applyCreateAudit(context.currentSessionId ?: 0L)
        }
    }

    @PreUpdate
    fun setUpdateAudit(entity: Any) {
        if (entity is AuditableEntity) {
            entity.audit.applyUpdateAudit(context.currentSessionId ?: 0L)
        }
    }
}
