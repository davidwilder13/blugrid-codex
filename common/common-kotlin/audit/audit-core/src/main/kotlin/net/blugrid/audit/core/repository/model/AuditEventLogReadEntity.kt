package net.blugrid.audit.core.repository.model

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import net.blugrid.common.model.audit.AuditEventType
import net.blugrid.common.model.resource.ResourceType
import net.blugrid.common.util.kotlinEquals
import org.hibernate.annotations.Immutable
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.util.Objects
import java.util.UUID

@Entity
@Immutable
@Table(name = "vw_audit_event_log_read")
class AuditEventLogReadEntity(

    @Id
    @Column(name = "uuid", updatable = false, nullable = false)
    val id: UUID,

    @Column(name = "resource_id")
    val resourceId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type")
    val resourceType: ResourceType,

    @Enumerated(EnumType.STRING)
    @Column(name = "audit_event_type")
    val auditEventType: AuditEventType,

    @Type(JsonBinaryType::class)
    @Column(name = "resource")
    val resource: Any,

    @Column(name = "timestamp")
    val auditEventTimestamp: LocalDateTime,

    @Column(name = "session_id")
    val sessionId: Long,

    @Column(name = "tenant_id")
    val tenantId: Long,
) {
    companion object {
        private val equalsProperties = arrayOf(
            AuditEventLogReadEntity::id,
            AuditEventLogReadEntity::resourceId,
            AuditEventLogReadEntity::resourceType,
            AuditEventLogReadEntity::auditEventType,
            AuditEventLogReadEntity::resource,
            AuditEventLogReadEntity::auditEventTimestamp,
            AuditEventLogReadEntity::sessionId,
            AuditEventLogReadEntity::tenantId
        )
    }

    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsProperties)

    override fun hashCode() = Objects.hash(
        id,
        resourceId,
        resourceType,
        auditEventType,
        resource,
        auditEventTimestamp,
        sessionId,
        tenantId
    )
}
