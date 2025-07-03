package net.blugrid.api.common.controller

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import net.blugrid.api.common.model.resource.BaseCreateResource
import net.blugrid.api.common.model.resource.BaseResource
import net.blugrid.api.common.model.resource.BaseUpdateResource

interface GenericCommandResource<T : BaseResource<T>, U : BaseCreateResource<U>, V : BaseUpdateResource<V>> {

    @Put(consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun create(@Body created: U): T

    @Post(uri = "/{id}", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun update(@PathVariable id: Long, @Body updated: V): T

    @Delete(value = "/{id}", produces = [MediaType.APPLICATION_JSON])
    fun delete(@PathVariable id: Long)
}
