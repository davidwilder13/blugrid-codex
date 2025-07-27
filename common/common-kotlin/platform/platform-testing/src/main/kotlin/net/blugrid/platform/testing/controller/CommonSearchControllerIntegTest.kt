package net.blugrid.platform.testing.controller

import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import net.blugrid.common.model.resource.BaseAuditedResource
import net.blugrid.common.model.search.SearchIndex
import net.blugrid.platform.testing.factory.randomInstance
import net.blugrid.platform.testing.generator.randomId
import java.time.LocalDateTime

open class CommonSearchControllerIntegTest(
    override val baseUri: String,
    @Client("/") override val client: HttpClient
) : CommonControllerIntegTest(baseUri, client) {

    companion object {
        var tenantId: Long = 1L
    }

    fun searchIndex(resource: BaseAuditedResource<*>, searchTerms: String = randomInstance()) = SearchIndex(
        resourceId = Long.randomId(),
        resourceType = resource.resourceType,
        resource = resource as Any,
        tenantId = 1L,
        searchTerms = searchTerms,
        createdTimestamp = LocalDateTime.now(),
        lastChangedTimestamp = LocalDateTime.now()
    )
}
