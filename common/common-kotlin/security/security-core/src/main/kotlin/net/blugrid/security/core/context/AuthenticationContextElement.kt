package net.blugrid.security.core.context

import io.micronaut.core.propagation.PropagatedContextElement
import net.blugrid.security.core.model.BaseAuthenticatedSession
import net.blugrid.security.core.model.DecoratedAuthentication

/**
 * Authentication Context Element for Micronaut 4 Propagation
 *
 * Mental Model: Simple data holder that gets propagated across threads
 * - No thread-local management needed (TenantContextPropagationElement handles that)
 * - Just carries authentication data across coroutine boundaries
 */
data class AuthenticationContextElement(
    val authentication: DecoratedAuthentication<out BaseAuthenticatedSession>
) : PropagatedContextElement {

    companion object {
        const val KEY = "grpc-authentication"
    }
}
