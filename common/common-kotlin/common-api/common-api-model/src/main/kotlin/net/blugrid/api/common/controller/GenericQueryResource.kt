package net.blugrid.api.common.controller

import io.micronaut.data.model.Page
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import net.blugrid.api.common.query.PageableQuery

interface GenericQueryResource<F, T> {

    @Post(uri = "/query", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun query(@Body query: PageableQuery<F>): Page<T>
}
