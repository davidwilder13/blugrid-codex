package net.blugrid.api.core.organisation.service

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.core.organisation.model.OrganisationFilter
import java.util.Optional
import java.util.UUID

interface OrganisationQueryService {
    fun getPage(pageable: Pageable): Page<Organisation>
    fun getById(id: Long): Organisation
    fun getByIdOptional(id: Long): Optional<Organisation>
    fun getAll(): List<Organisation>
    fun getByUuid(uuid: UUID): Organisation
    fun getByUuidOptional(uuid: UUID): Optional<Organisation>
    fun findByFilter(filter: OrganisationFilter, pageable: Pageable): Page<Organisation>
}
