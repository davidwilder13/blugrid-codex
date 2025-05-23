
package net.blugrid.core.organisation.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.SEQUENCE
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table

import net.blugrid.api.common.repository.model.
import net.blugrid.api.util.kotlinEquals
import org.hibernate.annotations.Formula
import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenericGenerator
import org.hibernate.generator.EventType
import java.time.LocalDateTime
import java.util.Objects
import java.util.UUID

@Entity
@Table(name = "vw_organisation")
class organisationEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organisation-sequence")
    @GenericGenerator(name = "organisation-sequence", strategy = "net.blugrid.api.db.GlobalTenantSequenceGenerator")
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    override var id: Long? = null,

    @Column(name = "uuid", updatable = false)
    override var uuid: UUID,

    @Column(name = "parentOrganisationId", nullable = true, updatable = false)
    var parentOrganisationId: t_identity? = null,
    
    @Column(name = "effectiveTimestamp", nullable = true, updatable = false)
    var effectiveTimestamp: t_datetime? = null
    
) : <organisationEntity> {
    
    companion object {
        private val equalsProperties = arrayOf(
            organisationEntity::parentOrganisationId,
            organisationEntity::effectiveTimestamp
        )
    }

    override fun update(update: organisationEntity): organisationEntity {
        this.parentOrganisationId = update.parentOrganisationId
        this.effectiveTimestamp = update.effectiveTimestamp
        return this
    }

    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsProperties)

    override fun hashCode() = Objects.hash(
        uuid, parentOrganisationId, effectiveTimestamp
    )
}
