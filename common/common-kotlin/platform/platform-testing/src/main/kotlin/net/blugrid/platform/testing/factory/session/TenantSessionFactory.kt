package net.blugrid.platform.testing.factory.session

import net.blugrid.platform.testing.factory.base.BaseFactory
import net.blugrid.platform.testing.factory.base.RandomizableFactory
import net.blugrid.platform.testing.factory.base.ScenarioFactory
import net.blugrid.platform.testing.generator.randomId
import net.blugrid.security.core.session.TenantSession
import java.util.UUID

object TenantSessionFactory : BaseFactory<TenantSession>,
    RandomizableFactory<TenantSession>,
    ScenarioFactory<TenantSession> {

    override fun createDefault(): TenantSession = create()

    fun create(
        sessionId: String = "session${Long.randomId()}",
        userId: String = "user${Long.randomId()}",
        tenantId: String = "tenant${Long.randomId()}",
        webApplicationId: String = "100001",
        operatorId: String = "operator${Long.randomId()}"
    ): TenantSession = TenantSession(
        sessionId = sessionId,
        userId = userId,
        tenantId = tenantId,
        webApplicationId = webApplicationId,
        operatorId = operatorId
    )

    fun createForTenant(tenantId: String) = create(
        tenantId = tenantId
    )

    fun createWithOperator(operatorId: String) = create(
        operatorId = operatorId
    )

    fun createWithUser(userId: String) = create(
        userId = userId
    )

    fun createWithSession(sessionId: String) = create(
        sessionId = sessionId
    )

    override fun createRandom(): TenantSession = create(
        sessionId = "session${Long.randomId()}",
        userId = "user${Long.randomId()}",
        tenantId = "tenant${Long.randomId()}",
        webApplicationId = Long.randomId().toString(),
        operatorId = "operator${Long.randomId()}"
    )

    override fun createForScenario(scenario: String): TenantSession = when (scenario) {
        "default-tenant" -> createForTenant("tenant1001")
        "multi-tenant-1" -> createForTenant("tenant1001")
        "multi-tenant-2" -> createForTenant("tenant2001")
        "admin-operator" -> create(operatorId = "admin")
        "support-operator" -> create(operatorId = "support${Long.randomId()}")
        "mobile-app" -> create(webApplicationId = "200001")
        "admin-app" -> create(webApplicationId = "300001")
        "self-service" -> create(operatorId = "user${Long.randomId()}")
        else -> createDefault()
    }

    /**
     * Builder DSL for TenantSession
     */
    class Builder : BaseFactory.Builder<TenantSession> {
        private var sessionId: String? = null
        private var userId: String? = null
        private var tenantId: String? = null
        private var webApplicationId: String? = null
        private var operatorId: String? = null

        fun sessionId(sessionId: String) = apply { this.sessionId = sessionId }
        fun userId(userId: String) = apply { this.userId = userId }
        fun tenantId(tenantId: String) = apply { this.tenantId = tenantId }
        fun tenantId(tenantId: Long) = apply { this.tenantId = tenantId.toString() }
        fun webApplicationId(webApplicationId: String) = apply { this.webApplicationId = webApplicationId }
        fun operatorId(operatorId: String) = apply { this.operatorId = operatorId }

        // Convenience methods
        fun randomSessionId() = apply { this.sessionId = "session${Long.randomId()}" }
        fun randomUserId() = apply { this.userId = "user${Long.randomId()}" }
        fun randomTenantId() = apply { this.tenantId = "tenant${Long.randomId()}" }
        fun randomOperatorId() = apply { this.operatorId = "operator${Long.randomId()}" }
        fun randomWebApplicationId() = apply { this.webApplicationId = Long.randomId().toString() }

        // Session patterns
        fun uuidSessionId() = apply { this.sessionId = UUID.randomUUID().toString() }
        fun timestampSessionId() = apply { this.sessionId = "session_${System.currentTimeMillis()}" }
        fun prefixedSessionId(prefix: String) = apply { this.sessionId = "${prefix}_${Long.randomId()}" }

        // User patterns
        fun numericUserId(id: Long) = apply { this.userId = id.toString() }
        fun prefixedUserId(prefix: String) = apply { this.userId = "${prefix}${Long.randomId()}" }

        // Tenant patterns
        fun tenant1() = apply { this.tenantId = "tenant1001" }
        fun tenant2() = apply { this.tenantId = "tenant2001" }
        fun prefixedTenantId(prefix: String) = apply { this.tenantId = "${prefix}${Long.randomId()}" }
        fun numericTenantId(id: Long) = apply { this.tenantId = "tenant$id" }

        // Operator patterns
        fun adminOperator() = apply { this.operatorId = "admin" }
        fun supportOperator() = apply { this.operatorId = "support${Long.randomId()}" }
        fun systemOperator() = apply { this.operatorId = "system" }
        fun selfServiceOperator() = apply { this.operatorId = this.userId ?: "user${Long.randomId()}" }
        fun prefixedOperatorId(prefix: String) = apply { this.operatorId = "${prefix}${Long.randomId()}" }

        // Web Application patterns
        fun defaultWebApp() = apply { this.webApplicationId = "100001" }
        fun mobileApp() = apply { this.webApplicationId = "200001" }
        fun adminApp() = apply { this.webApplicationId = "300001" }
        fun apiClient() = apply { this.webApplicationId = "400001" }
        fun testApp() = apply { this.webApplicationId = "999999" }

        // Scenario builders
        fun adminSession() = apply {
            adminOperator()
            adminApp()
        }

        fun supportSession() = apply {
            supportOperator()
            adminApp()
        }

        fun selfServiceSession() = apply {
            selfServiceOperator()
            defaultWebApp()
        }

        fun mobileSession() = apply {
            prefixedSessionId("mobile")
            mobileApp()
            selfServiceOperator()
        }

        fun apiSession() = apply {
            prefixedSessionId("api")
            apiClient()
            systemOperator()
        }

        fun multiTenantSession(tenantNumber: Int) = apply {
            numericTenantId(1000L + tenantNumber)
            this.sessionId = "mt_session_${tenantNumber}_${Long.randomId()}"
        }

        fun impersonationSession(operatorId: String, userId: String) = apply {
            this.operatorId = operatorId
            this.userId = userId
            this.sessionId = "impersonate_${operatorId}_as_${userId}"
        }

        fun testSession() = apply {
            this.sessionId = "test_session_${Long.randomId()}"
            this.userId = "test_user_${Long.randomId()}"
            this.tenantId = "test_tenant_${Long.randomId()}"
            this.operatorId = "test_operator"
            testApp()
        }

        override fun build(): TenantSession = TenantSession(
            sessionId = sessionId ?: "session${Long.randomId()}",
            userId = userId ?: "user${Long.randomId()}",
            tenantId = tenantId ?: "tenant${Long.randomId()}",
            webApplicationId = webApplicationId ?: "100001",
            operatorId = operatorId ?: "operator${Long.randomId()}"
        )
    }

    override fun build(block: BaseFactory.Builder<TenantSession>.() -> Unit): TenantSession =
        Builder().apply(block).build()
}
