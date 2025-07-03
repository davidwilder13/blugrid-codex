package net.blugrid.api.core.organisation.service

import jakarta.inject.Singleton
import net.blugrid.api.common.persistence.service.GenericQueryServiceImpl
import net.blugrid.api.core.organisation.mapping.OrganisationMappingService
import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.core.organisation.model.OrganisationFilter
import net.blugrid.api.core.organisation.repository.OrganisationRepository
import net.blugrid.api.core.organisation.repository.OrganisationSpecifications
import net.blugrid.api.core.organisation.repository.model.OrganisationEntity

@Singleton
open class OrganisationQueryServiceDbImpl(
    repository: OrganisationRepository,
    private val mapper: OrganisationMappingService
) : GenericQueryServiceImpl<OrganisationFilter, Organisation, OrganisationEntity>(
    repository = repository,
    mapper = mapper::entityToResource,
    specBuilder = OrganisationSpecifications::fromFilter
), OrganisationQueryService
