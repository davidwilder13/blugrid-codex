package net.blugrid.api

import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import net.blugrid.api.test.controller.CommonControllerIntegTest

@MicronautTest(environments = ["logging", "json", "security", "db", "multitenant-test"])
open class BaseMultitenantIntegTest(
    override val baseUri: String,
    @Client("/") override val client: HttpClient
) : CommonControllerIntegTest(
    baseUri = baseUri,
    client = client
)
