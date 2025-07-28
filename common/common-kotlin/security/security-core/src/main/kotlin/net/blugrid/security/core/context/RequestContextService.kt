package net.blugrid.security.core.context

import net.blugrid.security.core.model.BaseAuthenticatedSession
import net.blugrid.security.core.model.DecoratedAuthentication
import java.util.Optional

interface RequestContextService {
    /**
     * Get current authentication from the active request context
     */
    fun getCurrentAuthentication(): Optional<DecoratedAuthentication<out BaseAuthenticatedSession>>

    /**
     * Set authentication in the current request context
     */
    fun setCurrentAuthentication(authentication: DecoratedAuthentication<out BaseAuthenticatedSession>?)

    /**
     * Clear authentication from the current request context
     */
    fun clearCurrentAuthentication()
}
