package net.blugrid.api.security.service

import jakarta.inject.Singleton
import net.blugrid.api.logging.logger
import net.blugrid.api.security.context.BusinessUnitIdOverride
import net.blugrid.api.security.context.CurrentRequestContext
import net.blugrid.api.security.context.IsUnscoped
import net.blugrid.api.security.context.TenantIdOverride
import net.blugrid.api.security.model.BaseAuthenticatedOrganisation
import net.blugrid.api.security.model.BaseAuthenticatedSession
import net.blugrid.api.security.model.BaseAuthenticatedUser
import net.blugrid.api.security.repository.RequestScopeRepository
import java.util.concurrent.Callable

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

@Singleton
open class SecurityContextServiceImpl(
    private val requestScopeRepository: RequestScopeRepository,
) : SecurityContextService {

    val log = logger()

    override val currentSession: BaseAuthenticatedSession?
        get() = CurrentRequestContext.currentSession

    override val currentTenant: BaseAuthenticatedOrganisation?
        get() = CurrentRequestContext.currentOrganisation

    override val currentUser: BaseAuthenticatedUser?
        get() = CurrentRequestContext.currentUser

    override val currentBusinessUnitId: Long?
        get() = CurrentRequestContext.currentBusinessUnitId

    override val currentTenantId: Long?
        get() = CurrentRequestContext.currentTenantId

    override val currentIsUnscoped: Boolean
        get() = CurrentRequestContext.currentIsUnscoped

    override fun <T> runWithTenantId(tenantId: Long, original: Callable<T>): T {
        log.debug("Running with tenantId: $tenantId")
        val previousTenantId = if (TenantIdOverride.hasOverride()) {
            TenantIdOverride.value
        } else {
            null
        }
        try {
            TenantIdOverride.use {
                TenantIdOverride.value = tenantId.toString()
                requestScopeRepository.setTenantId(tenantId.toString())
                return original.call()
            }
        } finally {
            if (previousTenantId != null) {
                TenantIdOverride.value = previousTenantId
                requestScopeRepository.setTenantId(previousTenantId)
            }
        }
    }

    override fun <T> runWithBusinessUnitId(tenantId: Long, businessUnitId: Long, original: Callable<T>): T {
        log.debug("Running with tenantId: $tenantId businessUnitId: $businessUnitId")
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
                requestScopeRepository.setTenantId(tenantId.toString())
                BusinessUnitIdOverride.use {
                    BusinessUnitIdOverride.value = businessUnitId.toString()
                    requestScopeRepository.setBusinessUnitId(businessUnitId.toString())
                    return original.call()
                }
            }
        } finally {
            if (previousBusinessId != null) {
                BusinessUnitIdOverride.value = previousBusinessId
                requestScopeRepository.setBusinessUnitId(previousBusinessId)
            }
            if (previousTenantId != null) {
                TenantIdOverride.value = previousTenantId
                requestScopeRepository.setTenantId(previousTenantId)
            }
        }
    }

    override fun <T> runUnscoped(original: Callable<T>): T {
        log.debug("Running unscoped")
        val previousToken = if (IsUnscoped.isSet()) {
            IsUnscoped.value
        } else {
            null
        }
        try {
            IsUnscoped.use {
                IsUnscoped.value = true
                return original.call()
            }
        } finally {
            if (previousToken != null) {
                IsUnscoped.value = previousToken
            }
        }
    }
}
