
package net.blugrid.core.organisation.repository

import io.micronaut.context.annotation.Executable
import io.micronaut.data.annotation.Repository
import net.blugrid.core.organisation.repository.model.OrganisationEntity
import net.blugrid.api.common.repository.GenericCrudRepository

@Repository
interface OrganisationRepository : GenericCrudRepository
<OrganisationEntity> {
    @Executable
    override fun update(update: OrganisationEntity): OrganisationEntity
    }
