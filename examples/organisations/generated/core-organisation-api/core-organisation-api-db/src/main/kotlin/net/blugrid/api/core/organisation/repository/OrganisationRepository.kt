package net.blugrid.api.core.organisation.repository

import io.micronaut.context.annotation.Executable
import io.micronaut.data.annotation.Repository
import net.blugrid.api.core.organisation.repository.model.OrganisationEntity
import net.blugrid.data.persistence.repository.GenericEntityRepository

@Repository
interface OrganisationRepository : GenericEntityRepository<OrganisationEntity> {
    @Executable
    override fun update(update: OrganisationEntity): OrganisationEntity

    @Executable
    fun findByIdIn(ids: Collection<Long>): List<OrganisationEntity>
}
