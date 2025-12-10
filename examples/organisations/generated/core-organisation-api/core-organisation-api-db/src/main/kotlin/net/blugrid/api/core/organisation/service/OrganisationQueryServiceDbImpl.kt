package net.blugrid.api.core.organisation.service

import io.micronaut.transaction.annotation.ReadOnly
import jakarta.inject.Singleton
import net.blugrid.api.core.organisation.mapping.OrganisationMappingService
import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.core.organisation.model.OrganisationFilter
import net.blugrid.api.core.organisation.repository.OrganisationRepository
import net.blugrid.api.core.organisation.repository.OrganisationSpecifications
import net.blugrid.api.core.organisation.repository.model.OrganisationEntity
import net.blugrid.data.persistence.service.GenericQueryServiceImpl

@Singleton
open class OrganisationQueryServiceDbImpl(
    private val organisationRepository: OrganisationRepository,
    private val mapper: OrganisationMappingService
) : GenericQueryServiceImpl<OrganisationFilter, Organisation, OrganisationEntity>(
    repository = organisationRepository,
    mapper = mapper::entityToResource,
    specBuilder = OrganisationSpecifications::fromFilter
), OrganisationQueryService {

    @ReadOnly
    override fun getByIds(ids: List<Long>): List<Organisation> {
        if (ids.isEmpty()) return emptyList()
        return organisationRepository.findByIdIn(ids)
            .map { mapper.entityToResource(it) }
    }
}
