package net.blugrid.api.core.organisation.grpc.client

import io.micronaut.context.annotation.Requires
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.core.organisation.model.OrganisationCreate
import net.blugrid.api.core.organisation.model.OrganisationUpdate
import net.blugrid.api.core.organisation.service.OrganisationCommandService
import net.blugrid.api.core.organisation.grpc.toOrganisation
import net.blugrid.api.core.organisation.grpc.toOrganisationCreateRequest
import net.blugrid.api.core.organisation.grpc.toOrganisationPageRequest
import net.blugrid.api.core.organisation.grpc.toOrganisationUpdateRequest
import net.blugrid.api.core.organisation.model.OrganisationFilter
import net.blugrid.api.core.organisation.service.OrganisationQueryService
import java.util.Optional
import java.util.UUID

@Singleton
@Requires(classes = [OrganisationGrpcClient::class])
class OrganisationCommandServiceGrpcClientImpl(
    private val grpcClient: OrganisationGrpcClient
) : OrganisationCommandService, OrganisationQueryService {

    override fun getPage(pageable: Pageable): Page<Organisation> = runBlocking {
        val response = grpcClient.getPage(pageable.toOrganisationPageRequest())
        Page.of(
            response.organisationsList.map { it.toOrganisation() },
            pageable,
            response.totalElements.toLong()
        )
    }

    override fun getById(id: Long): Organisation = runBlocking {
        grpcClient.getById(id).toOrganisation()
    }

    override fun getByIdOptional(id: Long): Optional<Organisation> = runBlocking {
        val response = grpcClient.getByIdOptional(id)
        if (response.exists) Optional.of(response.organisation.toOrganisation())
        else Optional.empty()
    }

    override fun getByUuid(uuid: UUID): Organisation = runBlocking {
        grpcClient.getByUuid(uuid).toOrganisation()
    }

    override fun getByUuidOptional(uuid: UUID): Optional<Organisation> = runBlocking {
        val response = grpcClient.getByUuidOptional(uuid)
        if (response.exists) Optional.of(response.organisation.toOrganisation())
        else Optional.empty()
    }

    override fun findByFilter(filter: OrganisationFilter, pageable: Pageable): Page<Organisation> {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<Organisation> = runBlocking {
        grpcClient.getAll().organisationsList.map { it.toOrganisation() }
    }

    override fun create(newResource: OrganisationCreate): Organisation = runBlocking {
        grpcClient.create(newResource.toOrganisationCreateRequest()).toOrganisation()
    }

    override fun update(id: Long, update: OrganisationUpdate): Organisation = runBlocking {
        grpcClient.update(update.toOrganisationUpdateRequest()).toOrganisation()
    }

    override fun delete(id: Long) = runBlocking {
        grpcClient.delete(id)
    }
}
