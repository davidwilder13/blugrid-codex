package net.blugrid.api.core.organisation.service

import jakarta.inject.Singleton
import net.blugrid.api.common.service.GenericCommandServiceImpl
import net.blugrid.api.core.organisation.mapping.OrganisationMappingService
import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.core.organisation.model.OrganisationCreate
import net.blugrid.api.core.organisation.model.OrganisationUpdate
import net.blugrid.api.core.organisation.repository.OrganisationRepository
import net.blugrid.api.core.organisation.repository.model.OrganisationEntity

@Singleton
open class OrganisationCommandServiceDbImpl(
    repository: OrganisationRepository,
    mapper: OrganisationMappingService
) : GenericCommandServiceImpl<Organisation, OrganisationCreate, OrganisationUpdate, OrganisationEntity, OrganisationMappingService>(repository, mapper),
    OrganisationCommandService
