package net.blugrid.api.core.organisation.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.SEQUENCE
import jakarta.persistence.Id
import jakarta.persistence.Table
import net.blugrid.api.common.persistence.audit.AuditEmbeddable
import net.blugrid.api.common.persistence.model.resource.UnscopedPersistable
import net.blugrid.api.util.kotlinEquals
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.Objects
import java.util.UUID

@Entity
@Table(name = "vw_organisation")
class OrganisationEntity(

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "organisation-sequence")
    @GenericGenerator(name = "organisation-sequence", strategy = "net.blugrid.api.db.GlobalTenantSequenceGenerator")
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    override var id: Long? = null,

    @Column(name = "uuid", updatable = false)
    override var uuid: UUID,

    @Column(name = "parent_organisation_id", nullable = false, updatable = false)
    var parentOrganisationId: Long,

    @Column(name = "effective_timestamp", nullable = false, updatable = false)
    var effectiveTimestamp: LocalDateTime,

) : UnscopedPersistable<OrganisationEntity> {

    @Embedded
    override var audit: AuditEmbeddable = AuditEmbeddable()

    companion object {
        private val equalsProperties = arrayOf(
            OrganisationEntity::parentOrganisationId,
            OrganisationEntity::effectiveTimestamp
        )
    }

    override fun update(update: OrganisationEntity): OrganisationEntity {
        this.parentOrganisationId = update.parentOrganisationId
        this.effectiveTimestamp = update.effectiveTimestamp
        return this
    }

    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsProperties)

    override fun hashCode() = Objects.hash(
        uuid, parentOrganisationId, effectiveTimestamp
    )
}
