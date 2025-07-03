package net.blugrid.api.core.organisation.controller

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import net.blugrid.api.common.controller.GenericCommandResource
import net.blugrid.api.common.controller.GenericQueryResource
import net.blugrid.api.common.model.query.PageableQuery
import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.core.organisation.model.OrganisationCreate
import net.blugrid.api.core.organisation.model.OrganisationFilter
import net.blugrid.api.core.organisation.model.OrganisationUpdate
import net.blugrid.api.core.organisation.service.OrganisationCommandService
import net.blugrid.api.core.organisation.service.OrganisationQueryService
import java.util.Optional
import java.util.UUID

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/organisations")
@Tag(name = "Organisations", description = "Organisation management operations")
open class OrganisationController(
    private val commandService: OrganisationCommandService,
    private val queryService: OrganisationQueryService
) : GenericCommandResource<Organisation, OrganisationCreate, OrganisationUpdate>,
    GenericQueryResource<OrganisationFilter, Organisation> {

    @Operation(summary = "Create a new organisation")
    @Post("/")
    override fun create(@Body created: OrganisationCreate): Organisation =
        commandService.create(created)

    @Operation(summary = "Update an existing organisation by ID")
    @Put("/{id}")
    override fun update(@PathVariable id: Long, @Body updated: OrganisationUpdate): Organisation =
        commandService.update(id, updated)

    @Operation(summary = "Delete an organisation by ID")
    @Delete("/{id}")
    override fun delete(@PathVariable id: Long) =
        commandService.delete(id)

    @Operation(summary = "Get all organisations")
    @Get("/")
    override fun getAll(): List<Organisation> =
        queryService.getAll()

    @Operation(summary = "Get a paginated list of organisations")
    @Get("/page")
    override fun getPage(pageable: Pageable): Page<Organisation> =
        queryService.getPage(pageable)

    @Operation(summary = "Get organisation by ID")
    @Get("/{id}")
    override fun getById(@PathVariable id: Long): Organisation =
        queryService.getById(id)

    @Operation(summary = "Get organisation by ID (optional)")
    @Get("/{id}/optional")
    override fun getByIdOptional(@PathVariable id: Long): Optional<Organisation> =
        queryService.getByIdOptional(id)

    @Operation(summary = "Get organisation by UUID")
    @Get("/uuid/{uuid}")
    override fun getByUuid(@PathVariable uuid: UUID): Organisation =
        queryService.getByUuid(uuid)

    @Operation(summary = "Get organisation by UUID (optional)")
    @Get("/uuid/{uuid}/optional")
    override fun getByUuidOptional(@PathVariable uuid: UUID): Optional<Organisation> =
        queryService.getByUuidOptional(uuid)

    @Operation(summary = "Query organisations with filters and pagination")
    @Post("/query")
    override fun query(
        @RequestBody(description = "Filter and pagination parameters")
        @Body query: PageableQuery<OrganisationFilter>
    ): Page<Organisation> =
        queryService.findByFilter(query.query, Pageable.from(query.number, query.size))
}
