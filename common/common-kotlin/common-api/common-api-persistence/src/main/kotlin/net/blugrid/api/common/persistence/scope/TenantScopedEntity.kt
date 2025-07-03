package net.blugrid.api.common.persistence.scope

interface TenantScopedEntity {
    var permission: TenantScopeEmbeddable
}
