package net.blugrid.api.common.controller

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import net.blugrid.api.common.model.query.PageableQuery
import java.util.Optional
import java.util.UUID

interface GenericQueryResource<F, T> {

    @Post(uri = "/query", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun query(@Body query: PageableQuery<F>): Page<T>

    @Get(uri = "/", produces = [MediaType.APPLICATION_JSON])
    fun getAll(): List<T>

    @Get(uri = "/page", produces = [MediaType.APPLICATION_JSON])
    fun getPage(pageable: Pageable): Page<T>

    @Get(uri = "/{id}", produces = [MediaType.APPLICATION_JSON])
    fun getById(@PathVariable id: Long): T

    @Get(uri = "/{id}/optional", produces = [MediaType.APPLICATION_JSON])
    fun getByIdOptional(@PathVariable id: Long): Optional<T>

    @Get(uri = "/uuid/{uuid}", produces = [MediaType.APPLICATION_JSON])
    fun getByUuid(@PathVariable uuid: UUID): T

    @Get(uri = "/uuid/{uuid}/optional", produces = [MediaType.APPLICATION_JSON])
    fun getByUuidOptional(@PathVariable uuid: UUID): Optional<T>
}
