package net.blugrid.data.persistence.scope

interface BusinessUnitScoped : TenantScoped {
    var businessUnitId: Long?
}
