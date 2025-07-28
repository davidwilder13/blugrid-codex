package net.blugrid.security.core.context

import io.micronaut.http.HttpRequest
import io.micronaut.http.context.ServerRequestContext
import io.micronaut.security.filters.SecurityFilter
import net.blugrid.security.core.model.BaseAuthenticatedOrganisation
import net.blugrid.security.core.model.BaseAuthenticatedSession
import net.blugrid.security.core.model.BaseAuthenticatedUser
import net.blugrid.security.core.model.DecoratedAuthentication
import net.blugrid.security.core.session.BusinessUnitSession
import net.blugrid.security.core.session.TenantSession
import java.util.Optional

/**
 * Self-contained static facade that works without DI
 * This is the primary interface for legacy code and test environments
 */
object CurrentRequestContext : RequestContextProvider {

    override val currentSessionId: Long?
        get() = currentAuthentication?.session?.sessionId?.toLongOrNull()

    override val currentBusinessUnitId: Long?
        get() = when {
            BusinessUnitIdOverride.hasOverride() -> BusinessUnitIdOverride.value.toLong()
            else -> {
                val session = currentSession
                when (session) {
                    is BusinessUnitSession -> session.businessUnitId.toLongOrNull()
                    else -> null
                }
            }
        }

    override val currentTenantId: Long?
        get() = when {
            TenantIdOverride.hasOverride() -> TenantIdOverride.value.toLong()
            else -> {
                val session = currentSession
                when (session) {
                    is TenantSession -> session.tenantId.toLongOrNull()
                    is BusinessUnitSession -> session.tenantId.toLongOrNull()
                    else -> null
                }
            }
        }

    override val currentIsUnscoped: Boolean
        get() = IsUnscoped.isSet() && IsUnscoped.value

    override val currentOrganisation: BaseAuthenticatedOrganisation?
        get() = currentAuthentication?.let { auth ->
            auth.getAttributes()["organisation"] as? BaseAuthenticatedOrganisation
        }

    override val currentSession: BaseAuthenticatedSession?
        get() = currentAuthentication?.session

    override val currentUser: BaseAuthenticatedUser?
        get() = currentAuthentication?.user

    val currentAuthentication: DecoratedAuthentication<out BaseAuthenticatedSession>?
        get() = currentAuthenticationOpt.orElse(null)

    val currentAuthenticationOpt: Optional<DecoratedAuthentication<out BaseAuthenticatedSession>>
        get() = ServerRequestContext.currentRequest<Any>()
            .flatMap { request: HttpRequest<Any> ->
                request.toMultitenantAuthentication()
            }
}

@Suppress("UNCHECKED_CAST")
fun HttpRequest<*>.toMultitenantAuthentication(): Optional<DecoratedAuthentication<out BaseAuthenticatedSession>> {
    val attributeValue = attributes.getValue(SecurityFilter.AUTHENTICATION)
    return if (attributeValue is DecoratedAuthentication<*>) {
        Optional.of(attributeValue)
    } else {
        Optional.empty()
    }
}
