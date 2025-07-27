package net.blugrid.security.core.service

import net.blugrid.security.core.model.BaseAuthenticatedOrganisation
import net.blugrid.security.core.model.BaseAuthenticatedSession
import net.blugrid.security.core.model.BaseAuthenticatedUser
import java.util.concurrent.Callable

/**
 * Pure security context management - NO persistence dependencies
 * Handles thread-local security context overrides only
 */
interface SecurityContextService {
    fun <T> runWithTenantId(tenantId: Long, original: Callable<T>): T
    fun <T> runWithBusinessUnitId(tenantId: Long, businessUnitId: Long, original: Callable<T>): T
    fun <T> runUnscoped(original: Callable<T>): T

    val currentTenant: BaseAuthenticatedOrganisation?
    val currentUser: BaseAuthenticatedUser?
    val currentSession: BaseAuthenticatedSession?
    val currentBusinessUnitId: Long?
    val currentTenantId: Long?
    val currentIsUnscoped: Boolean
}
