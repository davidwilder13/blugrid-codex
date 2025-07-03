package net.blugrid.api.core.organisation.mapping

import jakarta.inject.Singleton
import net.blugrid.api.common.repository.model.GenericEntityMapper
import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.core.organisation.model.OrganisationCreate
import net.blugrid.api.core.organisation.model.OrganisationUpdate
import net.blugrid.api.core.organisation.repository.model.OrganisationEntity

@Singleton
class OrganisationMappingService : GenericEntityMapper<Organisation, OrganisationCreate, OrganisationUpdate, OrganisationEntity>() {
    override fun createToEntity(source: OrganisationCreate): OrganisationEntity = source.toEntity()
    override fun updateToEntity(source: OrganisationUpdate): OrganisationEntity = source.toEntity()
    override fun entityToResource(source: OrganisationEntity): Organisation = source.toResource()
    override fun resourceToCreate(source: Organisation): OrganisationCreate = source.toCreate()
    override fun resourceToUpdate(source: Organisation): OrganisationUpdate = source.toUpdate()
}
