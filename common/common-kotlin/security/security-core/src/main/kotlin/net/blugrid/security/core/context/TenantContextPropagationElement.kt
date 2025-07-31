package net.blugrid.security.core.context

import io.micronaut.core.propagation.ThreadPropagatedContextElement
import jakarta.inject.Singleton
import net.blugrid.platform.logging.logger
import net.blugrid.security.core.model.BaseAuthenticatedSession
import net.blugrid.security.core.model.DecoratedAuthentication
import net.blugrid.security.core.session.BusinessUnitSession
import net.blugrid.security.core.session.TenantSession

/**
 * Tenant Context Propagation Element
 *
 * Based on Spring Security ThreadContextElement pattern and Micronaut's MdcPropagationContext.
 * This solves the exact problem you're facing: ThreadLocal context lost in coroutines.
 *
 * Pattern: Capture → Propagate → Restore
 * 1. Capture current authentication when created
 * 2. Update ThreadLocal on every thread switch
 * 3. Restore previous ThreadLocal state when done
 */
@Singleton
data class TenantContextPropagationElement(
    val authentication: DecoratedAuthentication<out BaseAuthenticatedSession>
) : ThreadPropagatedContextElement<TenantThreadState> {

    private val log = logger()

    /**
     * Called by Micronaut when coroutine resumes on ANY thread
     */
    override fun updateThreadContext(): TenantThreadState? {
        val currentThread = Thread.currentThread().name
        log.debug("🔄 Updating ThreadLocal context on thread: {}", currentThread)

        // Capture existing ThreadLocal state (for restoration)
        val previousState = TenantThreadState(
            tenantId = TenantContextThreadLocal.get(),
            businessUnitId = BusinessUnitContextThreadLocal.get(),
            userId = UserContextThreadLocal.get(),
            sessionId = SessionContextThreadLocal.get()
        )

        // Set new ThreadLocal values from authentication
        val session = authentication.session
        when (session) {
            is TenantSession -> {
                TenantContextThreadLocal.set(session.tenantId)
                UserContextThreadLocal.set(session.userId)
                SessionContextThreadLocal.set(session.sessionId)
                log.debug("✅ Set TENANT ThreadLocal: tenantId={} on {}", session.tenantId, currentThread)
            }

            is BusinessUnitSession -> {
                TenantContextThreadLocal.set(session.tenantId)
                BusinessUnitContextThreadLocal.set(session.businessUnitId)
                UserContextThreadLocal.set(session.userId)
                SessionContextThreadLocal.set(session.sessionId)
                log.debug(
                    "✅ Set BUSINESS_UNIT ThreadLocal: tenantId={}, businessUnitId={} on {}",
                    session.tenantId, session.businessUnitId, currentThread
                )
            }

            else -> {
                UserContextThreadLocal.set(session.userId)
                SessionContextThreadLocal.set(session.sessionId)
                log.debug("✅ Set USER ThreadLocal: userId={} on {}", session.userId, currentThread)
            }
        }

        return previousState
    }

    /**
     * Called by Micronaut when coroutine leaves this thread
     * Restores the previous ThreadLocal state
     */
    override fun restoreThreadContext(oldState: TenantThreadState?) {
        val currentThread = Thread.currentThread().name
        log.debug("🔙 Restoring ThreadLocal context on thread: {}", currentThread)

        if (oldState != null) {
            // Restore previous ThreadLocal state
            TenantContextThreadLocal.set(oldState.tenantId)
            BusinessUnitContextThreadLocal.set(oldState.businessUnitId)
            UserContextThreadLocal.set(oldState.userId)
            SessionContextThreadLocal.set(oldState.sessionId)
            log.debug("📦 Restored ThreadLocal: tenantId={} on {}", oldState.tenantId, currentThread)
        } else {
            // Clear all ThreadLocal values (no previous state)
            TenantContextThreadLocal.remove()
            BusinessUnitContextThreadLocal.remove()
            UserContextThreadLocal.remove()
            SessionContextThreadLocal.remove()
            log.debug("🧹 Cleared ThreadLocal context on {}", currentThread)
        }
    }
}

/**
 * Thread state holder for restoration - inspired by Spring Security pattern
 */
data class TenantThreadState(
    val tenantId: String?,
    val businessUnitId: String?,
    val userId: String?,
    val sessionId: String?
)

// ===== 2. ThreadLocal Context Holders (What your sequence generator needs) =====
/**
 * ThreadLocal holders - based on Spring Security pattern
 * These provide the ThreadLocal access your GlobalTenantSequenceGenerator needs
 */
object TenantContextThreadLocal {
    private val threadLocal = ThreadLocal<String?>()

    fun get(): String? = threadLocal.get()
    fun set(tenantId: String?) {
        if (tenantId != null) {
            threadLocal.set(tenantId)
        } else {
            threadLocal.remove()
        }
    }

    fun remove() = threadLocal.remove()
}

object BusinessUnitContextThreadLocal {
    private val threadLocal = ThreadLocal<String?>()

    fun get(): String? = threadLocal.get()
    fun set(businessUnitId: String?) {
        if (businessUnitId != null) {
            threadLocal.set(businessUnitId)
        } else {
            threadLocal.remove()
        }
    }

    fun remove() = threadLocal.remove()
}

object UserContextThreadLocal {
    private val threadLocal = ThreadLocal<String?>()

    fun get(): String? = threadLocal.get()
    fun set(userId: String?) {
        if (userId != null) {
            threadLocal.set(userId)
        } else {
            threadLocal.remove()
        }
    }

    fun remove() = threadLocal.remove()
}

object SessionContextThreadLocal {
    private val threadLocal = ThreadLocal<String?>()

    fun get(): String? = threadLocal.get()
    fun set(sessionId: String?) {
        if (sessionId != null) {
            threadLocal.set(sessionId)
        } else {
            threadLocal.remove()
        }
    }

    fun remove() = threadLocal.remove()
}
