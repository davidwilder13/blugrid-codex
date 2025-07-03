package net.blugrid.api.common.persistence.scope

interface BusinessUnitScoped : TenantScoped {
    var businessUnitId: Long?
}
