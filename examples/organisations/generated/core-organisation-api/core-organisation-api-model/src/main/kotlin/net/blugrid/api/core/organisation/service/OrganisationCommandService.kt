package net.blugrid.api.core.organisation.service

import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.core.organisation.model.OrganisationCreate
import net.blugrid.api.core.organisation.model.OrganisationUpdate

interface OrganisationCommandService {
    fun update(id: Long, update: OrganisationUpdate): Organisation
    fun create(newResource: OrganisationCreate): Organisation
    fun delete(id: Long)
}
