package net.blugrid.api.test.controller

import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import net.blugrid.api.common.model.resource.GenericAuditedResource
import net.blugrid.api.common.search.SearchIndex
import net.blugrid.api.test.factory.randomInstance
import java.time.LocalDateTime

open class CommonSearchControllerIntegTest(
    override val baseUri: String,
    @Client("/") override val client: HttpClient
) : CommonControllerIntegTest(baseUri, client) {

    companion object {
        var tenantId: Long = 1L
    }

    fun searchIndex(resource: GenericAuditedResource<*>, searchTerms: String = randomInstance()) = SearchIndex(
        resourceId = faker.random.nextLong(),
        resourceType = resource.resourceType,
        resource = resource as Any,
        tenantId = 1L,
        searchTerms = searchTerms,
        createdTimestamp = LocalDateTime.now(),
        lastChangedTimestamp = LocalDateTime.now()
    )
}
