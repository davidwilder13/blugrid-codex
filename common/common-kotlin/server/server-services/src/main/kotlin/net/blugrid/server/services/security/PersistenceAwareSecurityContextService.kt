package net.blugrid.server.services.security

import jakarta.inject.Singleton
import net.blugrid.platform.logging.logger
import net.blugrid.security.core.model.BaseAuthenticatedOrganisation
import net.blugrid.security.core.model.BaseAuthenticatedSession
import net.blugrid.security.core.model.BaseAuthenticatedUser
import net.blugrid.security.core.service.SecurityContextService
import net.blugrid.server.persistence.scope.RequestScopeService
import java.util.concurrent.Callable

/**
 * Persistence-aware security context service that also updates database scope
 * This is a decorator around the pure SecurityContextService
 */
interface PersistenceAwareSecurityContextService : SecurityContextService {
}

@Singleton
class PersistenceAwareSecurityContextServiceImpl(
    private val securityContextService: SecurityContextService,
    private val requestScopeService: RequestScopeService
) : PersistenceAwareSecurityContextService {

    private val log = logger()

    // ===== DELEGATE READ-ONLY OPERATIONS =====

    override val currentSession: BaseAuthenticatedSession?
        get() = securityContextService.currentSession

    override val currentTenant: BaseAuthenticatedOrganisation?
        get() = securityContextService.currentTenant

    override val currentUser: BaseAuthenticatedUser?
        get() = securityContextService.currentUser

    override val currentBusinessUnitId: Long?
        get() = securityContextService.currentBusinessUnitId

    override val currentTenantId: Long?
        get() = securityContextService.currentTenantId

    override val currentIsUnscoped: Boolean
        get() = securityContextService.currentIsUnscoped

    // ===== DATABASE SCOPE MANAGEMENT WITH YOUR POSTGRESQL FUNCTIONS =====

    override fun <T> runWithTenantId(tenantId: Long, original: Callable<T>): T {
        log.debug("Running with tenantId (with database scope): {}", tenantId)

        return securityContextService.runWithTenantId(tenantId) {
            // Call your PostgreSQL function: set_tenant_scope()
            val result = requestScopeService.setTenantScope(tenantId)
            log.debug("set_tenant_scope({}) returned: {}", tenantId, result)

            try {
                original.call()
            } finally {
                // Reset scope when done
                requestScopeService.resetRequestScope()
            }
        }
    }

    override fun <T> runWithBusinessUnitId(tenantId: Long, businessUnitId: Long, original: Callable<T>): T {
        log.debug("Running with tenantId: {}, businessUnitId: {} (with database scope)", tenantId, businessUnitId)

        return securityContextService.runWithBusinessUnitId(tenantId, businessUnitId) {
            // Set tenant scope first, then business unit scope
            requestScopeService.setTenantScope(tenantId)
            val result = requestScopeService.setBusinessUnitScope(businessUnitId)
            log.debug("set_business_unit_scope({}) returned: {}", businessUnitId, result)

            try {
                original.call()
            } finally {
                // Reset scope when done
                requestScopeService.resetRequestScope()
            }
        }
    }

    override fun <T> runUnscoped(original: Callable<T>): T {
        log.debug("Running unscoped (with database scope reset)")

        return securityContextService.runUnscoped {
            // Reset scope for unscoped operations
            val result = requestScopeService.resetRequestScope()
            log.debug("reset_request_scope() returned: {}", result)

            original.call()
        }
    }
}
