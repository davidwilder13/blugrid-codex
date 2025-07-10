package net.blugrid.api.test.factory.session

import net.blugrid.api.session.model.GuestSession
import net.blugrid.api.test.factory.base.BaseFactory
import net.blugrid.api.test.factory.base.RandomizableFactory
import net.blugrid.api.test.factory.base.ScenarioFactory
import net.blugrid.api.test.generator.randomId
import java.util.UUID

object GuestSessionFactory : BaseFactory<GuestSession>,
    RandomizableFactory<GuestSession>,
    ScenarioFactory<GuestSession> {

    override fun createDefault(): GuestSession = create()

    fun create(
        sessionId: String = "session${Long.randomId()}",
        userId: String = "user${Long.randomId()}",
        webApplicationId: String = "100001"
    ): GuestSession = GuestSession(
        sessionId = sessionId,
        userId = userId,
        webApplicationId = webApplicationId
    )

    fun createWithSessionId(sessionId: String) = create(
        sessionId = sessionId
    )

    fun createWithUserId(userId: String) = create(
        userId = userId
    )

    fun createWithWebApplicationId(webApplicationId: String) = create(
        webApplicationId = webApplicationId
    )

    override fun createRandom(): GuestSession = create(
        sessionId = "session${Long.randomId()}",
        userId = "user${Long.randomId()}",
        webApplicationId = Long.randomId().toString()
    )

    override fun createForScenario(scenario: String): GuestSession = when (scenario) {
        "default-app" -> create(webApplicationId = "100001")
        "mobile-app" -> create(webApplicationId = "200001")
        "admin-app" -> create(webApplicationId = "300001")
        "anonymous" -> create(userId = "anonymous")
        "long-session" -> create(sessionId = "session${Long.randomId()}")
        "short-session" -> create(sessionId = "s${Long.randomId()}")
        else -> createDefault()
    }

    /**
     * Builder DSL for GuestSession
     */
    class Builder : BaseFactory.Builder<GuestSession> {
        private var sessionId: String? = null
        private var userId: String? = null
        private var webApplicationId: String? = null

        fun sessionId(sessionId: String) = apply { this.sessionId = sessionId }
        fun userId(userId: String) = apply { this.userId = userId }
        fun webApplicationId(webApplicationId: String) = apply { this.webApplicationId = webApplicationId }

        // Convenience methods
        fun randomSessionId() = apply { this.sessionId = "session${Long.randomId()}" }
        fun randomUserId() = apply { this.userId = "user${Long.randomId()}" }
        fun randomWebApplicationId() = apply { this.webApplicationId = Long.randomId().toString() }

        // Session ID patterns
        fun uuidSessionId() = apply { this.sessionId = UUID.randomUUID().toString() }
        fun timestampSessionId() = apply { this.sessionId = "session_${System.currentTimeMillis()}" }
        fun prefixedSessionId(prefix: String) = apply { this.sessionId = "${prefix}_${Long.randomId()}" }

        // User ID patterns
        fun numericUserId(id: Long) = apply { this.userId = id.toString() }
        fun prefixedUserId(prefix: String) = apply { this.userId = "${prefix}${Long.randomId()}" }
        fun anonymousUser() = apply { this.userId = "anonymous" }
        fun guestUser() = apply { this.userId = "guest${Long.randomId()}" }

        // Web Application ID patterns
        fun defaultWebApp() = apply { this.webApplicationId = "100001" }
        fun mobileApp() = apply { this.webApplicationId = "200001" }
        fun adminApp() = apply { this.webApplicationId = "300001" }
        fun apiClient() = apply { this.webApplicationId = "400001" }
        fun testApp() = apply { this.webApplicationId = "999999" }

        // Scenario builders
        fun shortLivedSession() = apply {
            this.sessionId = "temp_${Long.randomId()}"
            anonymousUser()
        }

        fun longLivedSession() = apply {
            uuidSessionId()
            randomUserId()
        }

        fun mobileSession() = apply {
            prefixedSessionId("mobile")
            mobileApp()
        }

        fun webSession() = apply {
            prefixedSessionId("web")
            defaultWebApp()
        }

        fun testSession() = apply {
            this.sessionId = "test_session_${Long.randomId()}"
            this.userId = "test_user_${Long.randomId()}"
            testApp()
        }

        override fun build(): GuestSession = GuestSession(
            sessionId = sessionId ?: "session${Long.randomId()}",
            userId = userId ?: "user${Long.randomId()}",
            webApplicationId = webApplicationId ?: "100001"
        )
    }

    override fun build(block: BaseFactory.Builder<GuestSession>.() -> Unit): GuestSession =
        Builder().apply(block).build()
}
