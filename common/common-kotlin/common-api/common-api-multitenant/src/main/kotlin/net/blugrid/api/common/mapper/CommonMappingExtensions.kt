package net.blugrid.api.common.mapper

import net.blugrid.api.common.model.resource.Audit
import net.blugrid.api.common.model.resource.BusinessUnitPermission
import net.blugrid.api.common.model.resource.TenantPermission
import net.blugrid.api.common.repository.model.EmbeddedAuditEntity
import net.blugrid.api.common.repository.model.EmbeddedBusinessUnitPermissionEntity
import net.blugrid.api.common.repository.model.EmbeddedTenantPermissionEntity

fun EmbeddedAuditEntity.toAudit(): Audit =
    Audit(
        createdTimestamp = createdTimestamp,
        createdBySessionId = createdBySessionId,
        lastChangedTimestamp = lastChangedTimestamp,
        lastChangedBySessionId = lastChangedBySessionId
    )

fun EmbeddedBusinessUnitPermissionEntity.toBusinessUnitPermission(): BusinessUnitPermission =
    BusinessUnitPermission(
        tenantId = tenantId,
        businessUnitId = businessUnitId
    )

fun EmbeddedTenantPermissionEntity.toTenantPermission() =
    TenantPermission(
        tenantId = this.tenantId,
    )

fun EmbeddedAuditEntity.toCommonAudit() =
    Audit(
        createdTimestamp = this.createdTimestamp,
        createdBySessionId = this.createdBySessionId,
        lastChangedTimestamp = this.lastChangedTimestamp,
        lastChangedBySessionId = this.lastChangedBySessionId,
    )

fun TenantPermission.toTenantPermissionEntity() =
    EmbeddedTenantPermissionEntity(
        tenantId = this.tenantId,
    )

fun Audit.toCommonAuditEntity() =
    EmbeddedAuditEntity(
        createdTimestamp = this.createdTimestamp,
        createdBySessionId = this.createdBySessionId,
        lastChangedTimestamp = this.lastChangedTimestamp,
        lastChangedBySessionId = this.lastChangedBySessionId,
    )


fun newEmbeddedTenantPermission(): EmbeddedTenantPermissionEntity =
    EmbeddedTenantPermissionEntity()

fun newEmbeddedBusinessUnitPermission(): EmbeddedBusinessUnitPermissionEntity =
    EmbeddedBusinessUnitPermissionEntity()

fun newEmbeddedAudit(): EmbeddedAuditEntity =
    EmbeddedAuditEntity()
