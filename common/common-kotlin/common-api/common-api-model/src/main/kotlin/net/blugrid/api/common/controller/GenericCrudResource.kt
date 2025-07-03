package net.blugrid.api.common.controller

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import net.blugrid.api.common.model.resource.GenericCreateResource
import net.blugrid.api.common.model.resource.GenericResource
import net.blugrid.api.common.model.resource.GenericUpdateResource
import net.blugrid.api.common.repository.model.GenericEntity
import net.blugrid.api.common.repository.model.GenericEntityMapper

interface GenericCrudResource<T : GenericResource<T>, U : GenericCreateResource<U>, V : GenericUpdateResource<V>> {

    @Put(consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun create(@Body created: U): T

    @Post(uri = "/{id}", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun update(@PathVariable id: Long, @Body updated: V): T

    @Get(value = "/{id}", produces = [MediaType.APPLICATION_JSON])
    fun getById(@PathVariable id: Long): T

    @Post(consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun getPage(@Body pageable: Pageable): Page<T>

    @Get(produces = [MediaType.APPLICATION_JSON])
    fun getAll(): List<T>

    @Delete(value = "/{id}", produces = [MediaType.APPLICATION_JSON])
    fun delete(@PathVariable id: Long)
}

interface GenericReadOnlyResource<T : GenericResource<T>, U : GenericCreateResource<U>, V : GenericUpdateResource<V>> {

    @Get(value = "/{id}", produces = [MediaType.APPLICATION_JSON])
    fun getById(@PathVariable id: Long): T

    @Post(consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun getPage(@Body pageable: Pageable): Page<T>

    @Get(produces = [MediaType.APPLICATION_JSON])
    fun getAll(): List<T>
}

interface GenericCrudController<T : GenericResource<T>, U : GenericCreateResource<U>, V : GenericUpdateResource<V>, X : GenericEntity<X>, Y : GenericEntityMapper<T, U, V, X>> {

    @Put(consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun create(@Body created: U): T

    @Post(uri = "/{id}", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun update(@PathVariable id: Long, @Body updated: V): T

    @Get(value = "/{id}", produces = [MediaType.APPLICATION_JSON])
    fun getById(@PathVariable id: Long): T

    @Post(consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun getPage(@Body pageable: Pageable): Page<T>

    @Get(produces = [MediaType.APPLICATION_JSON])
    fun getAll(): List<T>

    @Delete(value = "/{id}", produces = [MediaType.APPLICATION_JSON])
    fun delete(@PathVariable id: Long)
}

