package net.blugrid.api.common.mapper

import jakarta.inject.Singleton
import net.blugrid.api.common.model.resource.Audit
import net.blugrid.api.common.model.resource.BusinessUnitPermission
import net.blugrid.api.common.model.resource.TenantPermission
import net.blugrid.api.common.repository.model.EmbeddedAuditEntity
import net.blugrid.api.common.repository.model.EmbeddedBusinessUnitPermissionEntity
import net.blugrid.api.common.repository.model.EmbeddedTenantPermissionEntity
import org.mapstruct.Mapper
import org.mapstruct.MapperConfig
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants

interface HasId {
    var id: Long?
}

@MapperConfig
interface CommonMappings {

    @Mapping(target = "id", source = "id", resultType = Long::class)
    fun mapId(source: HasId): HasId
}

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)
abstract class CommonMapper {

    fun toAudit(source: EmbeddedAuditEntity): Audit = source.toAudit()
    fun toTenantPermission(source: EmbeddedTenantPermissionEntity): TenantPermission = source.toTenantPermission()
    fun toEmbeddedTenantPermissionEntity(): EmbeddedTenantPermissionEntity = newEmbeddedTenantPermission()
    fun toBusinessUnitPermission(source: EmbeddedBusinessUnitPermissionEntity): BusinessUnitPermission = source.toBusinessUnitPermission()
    fun toEmbeddedBusinessUnitPermissionEntity(): EmbeddedBusinessUnitPermissionEntity = newEmbeddedBusinessUnitPermission()
}

@Singleton
class EmbeddedAuditMapper {
    fun mapToEmbeddedAudit(): EmbeddedAuditEntity {
        return EmbeddedAuditEntity()
    }

    fun mapFromEmbeddedAudit(source: EmbeddedAuditEntity): Audit {
        return Audit(
            createdTimestamp = source.createdTimestamp,
            createdBySessionId = source.createdBySessionId,
            lastChangedTimestamp = source.lastChangedTimestamp,
            lastChangedBySessionId = source.lastChangedBySessionId,
        )
    }
}

@Singleton
class EmbeddedPermissionMapper {

    fun toEmbeddedTenantPermissionEntity(): EmbeddedTenantPermissionEntity {
        return EmbeddedTenantPermissionEntity()
    }

    fun toTenantPermission(source: EmbeddedTenantPermissionEntity): TenantPermission {
        return TenantPermission(tenantId = source.tenantId)
    }

    fun toEmbeddedBusinessUnitPermissionEntity(): EmbeddedBusinessUnitPermissionEntity {
        return EmbeddedBusinessUnitPermissionEntity()
    }

    fun toBusinessUnitPermission(source: EmbeddedBusinessUnitPermissionEntity): BusinessUnitPermission {
        return BusinessUnitPermission(
            tenantId = source.tenantId,
            businessUnitId = source.businessUnitId,
        )
    }
}
