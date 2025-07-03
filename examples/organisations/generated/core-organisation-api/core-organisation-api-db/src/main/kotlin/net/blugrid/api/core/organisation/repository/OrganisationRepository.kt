package net.blugrid.api.core.organisation.repository

import io.micronaut.context.annotation.Executable
import io.micronaut.data.annotation.Repository
import net.blugrid.api.common.persistence.repository.GenericEntityRepository
import net.blugrid.api.core.organisation.repository.model.OrganisationEntity

@Repository
interface OrganisationRepository : GenericEntityRepository<OrganisationEntity> {
    @Executable
    override fun update(update: OrganisationEntity): OrganisationEntity
}
