package net.blugrid.security.core.service

import jakarta.inject.Singleton
import net.blugrid.platform.logging.logger
import net.blugrid.security.core.context.BusinessUnitIdOverride
import net.blugrid.security.core.context.RequestContext
import net.blugrid.security.core.context.IsUnscoped
import net.blugrid.security.core.context.TenantIdOverride
import net.blugrid.security.core.model.BaseAuthenticatedOrganisation
import net.blugrid.security.core.model.BaseAuthenticatedSession
import net.blugrid.security.core.model.BaseAuthenticatedUser
import java.util.concurrent.Callable

/**
 * Pure security context implementation - only manages thread-local overrides
 * Database scope setting is handled separately by persistence layer
 */
@Singleton
open class SecurityContextServiceImpl : SecurityContextService {

    private val log = logger()

    // ===== READ-ONLY ACCESS TO CURRENT CONTEXT =====

    override val currentSession: BaseAuthenticatedSession?
        get() = RequestContext.currentSession

    override val currentTenant: BaseAuthenticatedOrganisation?
        get() = RequestContext.currentOrganisation

    override val currentUser: BaseAuthenticatedUser?
        get() = RequestContext.currentUser

    override val currentBusinessUnitId: Long?
        get() = RequestContext.currentBusinessUnitId

    override val currentTenantId: Long?
        get() = RequestContext.currentTenantId

    override val currentIsUnscoped: Boolean
        get() = RequestContext.currentIsUnscoped

    // ===== SECURITY CONTEXT OVERRIDE METHODS =====

    override fun <T> runWithTenantId(tenantId: Long, original: Callable<T>): T {
        log.debug("Running with tenantId override: {}", tenantId)

        val previousTenantId = if (TenantIdOverride.hasOverride()) {
            TenantIdOverride.value
        } else {
            null
        }

        try {
            TenantIdOverride.use {
                TenantIdOverride.value = tenantId.toString()
                // Note: Database scope will be automatically updated by persistence layer
                // when it detects the context change
                return original.call()
            }
        } finally {
            if (previousTenantId != null) {
                TenantIdOverride.value = previousTenantId
            }
        }
    }

    override fun <T> runWithBusinessUnitId(tenantId: Long, businessUnitId: Long, original: Callable<T>): T {
        log.debug("Running with tenantId: {}, businessUnitId: {} override", tenantId, businessUnitId)

        val previousBusinessId = if (BusinessUnitIdOverride.hasOverride()) {
            BusinessUnitIdOverride.value
        } else {
            null
        }
        val previousTenantId = if (TenantIdOverride.hasOverride()) {
            TenantIdOverride.value
        } else {
            null
        }

        try {
            TenantIdOverride.use {
                TenantIdOverride.value = tenantId.toString()
                BusinessUnitIdOverride.use {
                    BusinessUnitIdOverride.value = businessUnitId.toString()
                    // Note: Database scope will be automatically updated by persistence layer
                    return original.call()
                }
            }
        } finally {
            if (previousBusinessId != null) {
                BusinessUnitIdOverride.value = previousBusinessId
            }
            if (previousTenantId != null) {
                TenantIdOverride.value = previousTenantId
            }
        }
    }

    override fun <T> runUnscoped(original: Callable<T>): T {
        log.debug("Running with unscoped override")

        val previousToken = if (IsUnscoped.isSet()) {
            IsUnscoped.value
        } else {
            null
        }

        try {
            IsUnscoped.use {
                IsUnscoped.value = true
                // Note: Database scope will be automatically updated by persistence layer
                return original.call()
            }
        } finally {
            if (previousToken != null) {
                IsUnscoped.value = previousToken
            }
        }
    }
}
