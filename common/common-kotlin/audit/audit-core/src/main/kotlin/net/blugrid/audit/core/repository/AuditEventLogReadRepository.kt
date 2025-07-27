package net.blugrid.audit.core.repository

import io.micronaut.context.annotation.Executable
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import io.micronaut.data.jpa.repository.JpaSpecificationExecutor
import io.micronaut.data.jpa.repository.criteria.Specification
import io.micronaut.data.jpa.repository.criteria.Specification.where
import net.blugrid.audit.core.model.AuditEventLogQuery
import net.blugrid.audit.core.repository.model.AuditEventLogReadEntity
import net.blugrid.common.model.audit.AuditEventType
import net.blugrid.common.model.resource.ResourceType
import net.blugrid.data.persistence.repository.equal
import net.blugrid.data.persistence.repository.`in`
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface AuditEventLogReadRepository : JpaRepository<AuditEventLogReadEntity, UUID>, JpaSpecificationExecutor<AuditEventLogReadEntity?> {
    @Executable
    fun findBySessionId(sessionId: Long): List<AuditEventLogReadEntity>?

    @Executable
    fun findByResourceType(resourceType: ResourceType): List<AuditEventLogReadEntity>?

    @Executable
    @Query(value = "SELECT count(*) from AuditEventLogReadEntity a WHERE a.resourceType = :resourceType")
    fun countByResourceType(resourceType: ResourceType): Long
}

object AuditEventLogReadSpecifications {
    fun auditLogEventQueryToSpecification(auditEventLogQuery: AuditEventLogQuery): Specification<AuditEventLogReadEntity?> {
        return where(hasResourceTypes(auditEventLogQuery.resourceTypes))
            .and(hasResourceIds(auditEventLogQuery.resourceIds))
            .and(hasClientIds(auditEventLogQuery.tenantIds))
            .and(hasAuditEventType(auditEventLogQuery.auditEventTypes))
            .and(hasTimestampBetween(auditEventLogQuery.minDateTime, auditEventLogQuery.maxDateTime))
    }

    fun hasResourceTypeAndResourceId(resourceType: ResourceType?, resourceId: Long?): Specification<AuditEventLogReadEntity?> {
        return where(hasResourceType(resourceType = resourceType))
            .and(hasResourceId(resourceId = resourceId))
    }

    fun hasResourceType(resourceType: ResourceType?): Specification<AuditEventLogReadEntity>? =
        resourceType?.let { AuditEventLogReadEntity::resourceType.equal(it) }

    fun hasResourceId(resourceId: Long?): Specification<AuditEventLogReadEntity>? =
        resourceId?.let { AuditEventLogReadEntity::resourceId.equal(it) }

    fun hasResourceTypes(resourceTypes: List<ResourceType>?): Specification<AuditEventLogReadEntity>? = resourceTypes?.let { AuditEventLogReadEntity::resourceType.`in`(resourceTypes) }
    fun hasResourceIds(resourceIds: List<Long>?): Specification<AuditEventLogReadEntity>? = resourceIds?.let { AuditEventLogReadEntity::resourceId.`in`(it) }
    fun hasClientIds(tenantIds: List<Long>?): Specification<AuditEventLogReadEntity>? = tenantIds?.let { AuditEventLogReadEntity::tenantId.`in`(tenantIds) }
    fun hasAuditEventType(auditEventTypes: List<AuditEventType>?): Specification<AuditEventLogReadEntity>? = auditEventTypes?.let { AuditEventLogReadEntity::auditEventType.`in`(auditEventTypes) }

    fun hasTimestampBetween(minDate: LocalDateTime?, maxDate: LocalDateTime?): Specification<AuditEventLogReadEntity> {
        return Specification<AuditEventLogReadEntity> { root, _, cb ->
            cb.and(
                if (minDate == null) cb.conjunction() else cb.greaterThanOrEqualTo(root.get("timestamp"), minDate),
                if (maxDate == null) cb.conjunction() else cb.lessThanOrEqualTo(root.get("timestamp"), maxDate)
            )
        }
    }

    fun hasTimestampGreaterThan(startDate: LocalDateTime): Specification<AuditEventLogReadEntity> {
        return Specification<AuditEventLogReadEntity> { root, _, cb ->
            cb.and(
                cb.greaterThanOrEqualTo(root.get("timestamp"), startDate)
            )
        }
    }

    fun hasTimestampLessThan(endDate: LocalDateTime): Specification<AuditEventLogReadEntity> {
        return Specification<AuditEventLogReadEntity> { root, _, cb ->
            cb.and(
                cb.lessThanOrEqualTo(root.get("timestamp"), endDate)
            )
        }
    }
}

