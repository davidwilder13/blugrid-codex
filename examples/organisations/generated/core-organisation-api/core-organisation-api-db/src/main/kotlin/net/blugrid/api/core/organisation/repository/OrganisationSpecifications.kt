package net.blugrid.api.core.organisation.repository

import io.micronaut.data.jpa.repository.criteria.Specification
import net.blugrid.api.common.persistence.repository.and
import net.blugrid.api.common.persistence.repository.greaterThanOrEqualTo
import net.blugrid.api.common.persistence.repository.`in`
import net.blugrid.api.common.persistence.repository.lessThanOrEqualTo
import net.blugrid.api.core.organisation.model.OrganisationFilter
import net.blugrid.api.core.organisation.repository.model.OrganisationEntity
import java.util.UUID

object OrganisationSpecifications {

    fun idsIn(ids: List<Long>?): Specification<OrganisationEntity>? =
        ids?.let { OrganisationEntity::id.`in`(it) }

    fun uuidsIn(uuids: List<UUID>?): Specification<OrganisationEntity>? =
        uuids?.let { OrganisationEntity::uuid.`in`(it) }

    fun parentOrganisationIdIn(ids: List<Long>?): Specification<OrganisationEntity>? =
        ids?.let { OrganisationEntity::parentOrganisationId.`in`(it) }

    fun effectiveFrom(from: java.time.LocalDateTime?): Specification<OrganisationEntity>? =
        from?.let { OrganisationEntity::effectiveTimestamp.greaterThanOrEqualTo(it) }

    fun effectiveTo(to: java.time.LocalDateTime?): Specification<OrganisationEntity>? =
        to?.let { OrganisationEntity::effectiveTimestamp.lessThanOrEqualTo(it) }

    fun fromFilter(filter: OrganisationFilter): Specification<OrganisationEntity> = and(
        idsIn(filter.ids),
        uuidsIn(filter.uuids),
        parentOrganisationIdIn(filter.parentOrganisationIds),
        effectiveFrom(filter.effectiveFrom),
        effectiveTo(filter.effectiveTo),
    )
}
