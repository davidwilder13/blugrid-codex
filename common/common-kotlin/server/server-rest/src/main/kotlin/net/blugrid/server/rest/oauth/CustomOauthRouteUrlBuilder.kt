package net.blugrid.api.security.service

import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.http.uri.UriBuilder
import io.micronaut.security.oauth2.configuration.OauthConfiguration
import io.micronaut.security.oauth2.url.OauthRouteUrlBuilder
import jakarta.inject.Singleton
import net.blugrid.platform.config.ServerProps
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL

@Singleton
@Replaces(bean = OauthRouteUrlBuilder::class)
class CustomOauthRouteUrlBuilder(
    private val serverProps: ServerProps,
    private val oauthConfiguration: OauthConfiguration,
    @Value("\${micronaut.server.context-path:/}") private val contextPath: String
) : OauthRouteUrlBuilder<HttpRequest<*>> {

    private val loginUriTemplate: String = oauthConfiguration.loginUri
    private val callbackUriTemplate: String = oauthConfiguration.callbackUri

    override fun buildLoginUrl(originating: HttpRequest<*>, providerName: String): URL {
        return buildUrl(originating, getPath(loginUriTemplate, providerName))
    }

    override fun buildCallbackUrl(originating: HttpRequest<*>, providerName: String): URL {
        return buildUrl(originating, getPath(callbackUriTemplate, providerName))
    }

    override fun buildLoginUri(providerName: String?): URI {
        return try {
            URI(getPath(loginUriTemplate, providerName ?: DEFAULT_PROVIDER_NAME))
        } catch (e: URISyntaxException) {
            throw IllegalArgumentException("Error building URI for path [$loginUriTemplate]", e)
        }
    }

    override fun buildCallbackUri(providerName: String?): URI {
        return try {
            URI(getPath(callbackUriTemplate, providerName ?: DEFAULT_PROVIDER_NAME))
        } catch (e: URISyntaxException) {
            throw IllegalArgumentException("Error building URI for path [$callbackUriTemplate]", e)
        }
    }

    private fun getPath(uriTemplate: String, providerName: String): String {
        val uriParams = mutableMapOf<String, Any>("provider" to providerName)
        return UriBuilder.of(contextPath)
            .path(uriTemplate)
            .expand(uriParams)
            .toString()
    }

    override fun buildUrl(originating: HttpRequest<*>, path: String): URL {
        return try {
            UriBuilder.of(serverProps.baseUri)
                .path(path)
                .build()
                .toURL()
        } catch (e: MalformedURLException) {
            throw IllegalArgumentException("Error building URL for path [$path]", e)
        }
    }

    companion object {
        private val DEFAULT_PROVIDER_NAME = "" // This will build a route like Registering default login route [GET: /oauth/login/]
    }
}

