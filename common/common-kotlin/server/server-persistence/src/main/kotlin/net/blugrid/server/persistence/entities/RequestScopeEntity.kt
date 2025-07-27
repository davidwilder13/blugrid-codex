package net.blugrid.api.security.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "vw_request_scope")
class RequestScopeEntity(

    @Id
    @Column(name = "tenant_id")
    val tenantId: Long? = null,
)
