package net.blugrid.server.persistence.scope

import jakarta.inject.Singleton
import net.blugrid.platform.logging.logger
import net.blugrid.server.persistence.repositories.RequestScopeRepository

/**
 * Implementation that delegates to your existing PostgreSQL function calls
 * Keeps JPA repository concerns isolated in persistence layer
 */
@Singleton
class RequestScopeServiceImpl(
    private val requestScopeRepository: RequestScopeRepository
) : RequestScopeService {

    private val log = logger()

    override fun setTenantScope(tenantId: String): Int {
        log.debug("Setting database tenant scope via PostgreSQL function: {}", tenantId)
        return requestScopeRepository.setTenantId(tenantId)
    }

    override fun setBusinessUnitScope(businessUnitId: String): Int {
        log.debug("Setting database business unit scope via PostgreSQL function: {}", businessUnitId)
        return requestScopeRepository.setBusinessUnitId(businessUnitId)
    }

    override fun resetRequestScope(): Int {
        log.debug("Resetting database request scope via PostgreSQL function")
        return requestScopeRepository.resetRequestScope()
    }
}
