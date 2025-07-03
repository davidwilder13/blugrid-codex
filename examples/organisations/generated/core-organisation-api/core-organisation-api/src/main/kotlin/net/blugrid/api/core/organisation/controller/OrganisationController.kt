package net.blugrid.api.core.organisation.controller

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.Controller
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import net.blugrid.api.common.controller.GenericCommandResource
import net.blugrid.api.common.controller.GenericQueryResource
import net.blugrid.api.common.query.PageableQuery
import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.core.organisation.model.OrganisationCreate
import net.blugrid.api.core.organisation.model.OrganisationFilter
import net.blugrid.api.core.organisation.model.OrganisationUpdate
import net.blugrid.api.core.organisation.service.OrganisationQueryService
import net.blugrid.api.core.organisation.service.OrganisationCommandService

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/organisations")
open class OrganisationController(
    private val service: OrganisationCommandService,
    private val queryService: OrganisationQueryService
) : GenericCommandResource<Organisation, OrganisationCreate, OrganisationUpdate>,
    GenericQueryResource<OrganisationFilter, Organisation> {

    override fun create(created: OrganisationCreate): Organisation =
        service.create(created)

    override fun update(id: Long, updated: OrganisationUpdate): Organisation =
        service.update(id, updated)

    override fun getById(id: Long): Organisation =
        service.getById(id)

    override fun getPage(pageable: Pageable): Page<Organisation> =
        service.getPage(pageable)

    override fun getAll(): List<Organisation> =
        service.getAll()

    override fun delete(id: Long) =
        service.delete(id)

    override fun query(query: PageableQuery<OrganisationFilter>): Page<Organisation> =
        queryService.findByFilter(query.query, Pageable.from(query.number, query.size))
}
