package net.blugrid.api.test.factory.audit

import net.blugrid.api.common.model.audit.AuditStamp
import net.blugrid.api.test.factory.base.BaseFactory
import net.blugrid.api.test.factory.base.RandomizableFactory
import net.blugrid.api.test.factory.base.ScenarioFactory
import net.blugrid.api.test.generator.IdentityIDRandom
import net.blugrid.common.domain.IdentityID
import net.blugrid.api.test.generator.random
import net.blugrid.api.test.generator.randomAlphanumeric
import java.time.LocalDateTime

object AuditStampFactory : BaseFactory<AuditStamp>,
    RandomizableFactory<AuditStamp>,
    ScenarioFactory<AuditStamp> {

    override fun createDefault(): AuditStamp = create()


    fun create(
        sessionId: IdentityID? = IdentityIDRandom.generate(),
        session: Any? = null,
        timestamp: LocalDateTime? = LocalDateTime.now()
    ): AuditStamp = AuditStamp(
        sessionId = sessionId,
        session = session,
        timestamp = timestamp
    )

    fun createWithSession(session: Any) = create(session = session)

    fun createPast(daysAgo: Long = 30) = create(
        timestamp = LocalDateTime.now().minusDays(daysAgo)
    )

    fun createFuture(daysFromNow: Long = 30) = create(
        timestamp = LocalDateTime.now().plusDays(daysFromNow)
    )

    fun createWithSessionId(sessionId: IdentityID) = create(sessionId = sessionId)

    fun createWithSessionId(sessionId: Long) = create(sessionId = IdentityID(sessionId))

    fun createNow() = create(timestamp = LocalDateTime.now())

    override fun createRandom(): AuditStamp = create(
        sessionId = IdentityIDRandom.generate(),
        session = if (Boolean.random()) {
            String.randomAlphanumeric(Int.random(10, 50))
        } else null,
        timestamp = LocalDateTime.now().minusDays(Long.random(1, 365))
    )

    override fun createForScenario(scenario: String): AuditStamp = when (scenario) {
        "now" -> createNow()
        "past" -> createPast()
        "old" -> createPast(Long.random(365, 1365))
        "recent" -> createPast(Long.random(1, 8))
        "future" -> createFuture()
        "with-session" -> createWithSession(String.randomAlphanumeric(20))
        "system-session" -> create(sessionId = IdentityIDRandom.Common.SESSION_1)
        "user-session" -> create(sessionId = IdentityIDRandom.generate())
        "anonymous" -> create(sessionId = null, session = null)
        else -> createDefault()
    }

    /**
     * Builder DSL for AuditStamp
     */
    class Builder : BaseFactory.Builder<AuditStamp> {
        private var sessionId: IdentityID? = null
        private var session: Any? = null
        private var timestamp: LocalDateTime? = null

        fun sessionId(sessionId: IdentityID) = apply { this.sessionId = sessionId }
        fun sessionId(sessionId: Long) = apply { this.sessionId = IdentityID(sessionId) }
        fun session(session: Any) = apply { this.session = session }
        fun timestamp(timestamp: LocalDateTime) = apply { this.timestamp = timestamp }

        // Convenience methods for sessionId
        fun randomSessionId() = apply { this.sessionId = IdentityIDRandom.generate() }
        fun systemSessionId() = apply { this.sessionId = IdentityIDRandom.Common.SESSION_1 }
        fun userSessionId() = apply { this.sessionId = IdentityIDRandom.generate() }
        fun noSessionId() = apply { this.sessionId = null }

        // Convenience methods for session
        fun randomSession() = apply { this.session = String.randomAlphanumeric(Int.random(10, 50)) }
        fun stringSession(value: String) = apply { this.session = value }
        fun noSession() = apply { this.session = null }

        // Convenience methods for timestamp
        fun now() = apply { this.timestamp = LocalDateTime.now() }
        fun pastTimestamp(daysAgo: Long = 30) = apply {
            this.timestamp = LocalDateTime.now().minusDays(daysAgo)
        }

        fun futureTimestamp(daysFromNow: Long = 30) = apply {
            this.timestamp = LocalDateTime.now().plusDays(daysFromNow)
        }

        fun randomPastTimestamp(maxDaysAgo: Long = 365) = apply {
            val daysAgo = Long.random(1, maxDaysAgo + 1)
            this.timestamp = LocalDateTime.now().minusDays(daysAgo)
        }

        fun randomFutureTimestamp(maxDaysFromNow: Long = 30) = apply {
            val daysFromNow = Long.random(1, maxDaysFromNow + 1)
            this.timestamp = LocalDateTime.now().plusDays(daysFromNow)
        }

        fun specificTimestamp(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0) = apply {
            this.timestamp = LocalDateTime.of(year, month, day, hour, minute)
        }

        // Time-based convenience methods
        fun hoursAgo(hours: Long) = apply {
            this.timestamp = LocalDateTime.now().minusHours(hours)
        }

        fun minutesAgo(minutes: Long) = apply {
            this.timestamp = LocalDateTime.now().minusMinutes(minutes)
        }

        fun secondsAgo(seconds: Long) = apply {
            this.timestamp = LocalDateTime.now().minusSeconds(seconds)
        }

        // Common scenarios
        fun recentActivity() = apply {
            this.sessionId = IdentityIDRandom.generate()
            this.timestamp = LocalDateTime.now().minusHours(Long.random(1, 24))
        }

        fun oldActivity() = apply {
            this.sessionId = IdentityIDRandom.generate()
            this.timestamp = LocalDateTime.now().minusDays(Long.random(365, 1365))
        }

        fun systemActivity() = apply {
            this.sessionId = IdentityIDRandom.Common.SESSION_1
            this.session = "SYSTEM"
            this.timestamp = LocalDateTime.now()
        }

        fun userActivity(userId: IdentityID? = null) = apply {
            this.sessionId = IdentityIDRandom.generate()
            this.session = userId?.let { "USER_${it.value}" }
            this.timestamp = LocalDateTime.now()
        }

        fun userActivity(userId: Long) = userActivity(IdentityID(userId))

        fun anonymousActivity() = apply {
            this.sessionId = null
            this.session = null
            this.timestamp = LocalDateTime.now()
        }

        fun batchActivity() = apply {
            this.sessionId = IdentityIDRandom.generate()
            this.session = "BATCH_${Long.random(1000, 10999)}"
            this.timestamp = LocalDateTime.now()
        }

        fun migrationActivity() = apply {
            this.sessionId = IdentityIDRandom.Common.SESSION_1
            this.session = "MIGRATION_${Long.random(1000, 10999)}"
            this.timestamp = LocalDateTime.now()
        }

        // Session-specific builders
        fun withSessionData(sessionId: IdentityID, sessionData: Any) = apply {
            this.sessionId = sessionId
            this.session = sessionData
        }

        fun withSessionData(sessionId: Long, sessionData: Any) = withSessionData(IdentityID(sessionId), sessionData)

        fun forSession(sessionId: IdentityID, timestampDaysAgo: Long = 0) = apply {
            this.sessionId = sessionId
            this.timestamp = if (timestampDaysAgo == 0L) {
                LocalDateTime.now()
            } else {
                LocalDateTime.now().minusDays(timestampDaysAgo)
            }
        }

        fun forSession(sessionId: Long, timestampDaysAgo: Long = 0) = forSession(IdentityID(sessionId), timestampDaysAgo)

        override fun build(): AuditStamp = AuditStamp(
            sessionId = sessionId ?: IdentityIDRandom.generate(),
            session = session,
            timestamp = timestamp ?: LocalDateTime.now()
        )
    }

    override fun build(block: BaseFactory.Builder<AuditStamp>.() -> Unit): AuditStamp =Builder().apply(block).build()
}

// Usage Examples:
/*
// Basic usage - all generated
val stamp1 = AuditStampFactory.build()

// Set specific values, generate the rest
val stamp2 = AuditStampFactory.build {
    sessionId(12345L)
    pastTimestamp(10)
}

// Use convenience methods
val stamp3 = AuditStampFactory.build {
    systemSessionId()
    stringSession("SYSTEM_OPERATION")
    now()
}

// Time-based convenience
val stamp4 = AuditStampFactory.build {
    randomSessionId()
    hoursAgo(2)
}

// Scenario-based creation
val stamp5 = AuditStampFactory.build {
    recentActivity()
}

val stamp6 = AuditStampFactory.build {
    userActivity(userId = 99999L)
}

val stamp7 = AuditStampFactory.build {
    batchActivity()
    pastTimestamp(7)
}

// Session-specific
val stamp8 = AuditStampFactory.build {
    forSession(sessionId = 12345L, timestampDaysAgo = 30)
}

val stamp9 = AuditStampFactory.build {
    withSessionData(
        sessionId = 67890L,
        sessionData = mapOf("user" to "admin", "operation" to "create")
    )
}

// Specific timestamp
val stamp10 = AuditStampFactory.build {
    sessionId(11111L)
    specificTimestamp(2024, 1, 15, 14, 30)
}

// Anonymous activity
val stamp11 = AuditStampFactory.build {
    anonymousActivity()
    pastTimestamp(5)
}

// Predefined scenarios
val recentStamp = AuditStampFactory.createForScenario("recent")
val oldStamp = AuditStampFactory.createForScenario("old")
val systemStamp = AuditStampFactory.createForScenario("system-session")

// Random list
val randomStamps = AuditStampFactory.createRandomList(5)

// Integration with ResourceAuditFactory
val audit = ResourceAuditFactory.build {
    version(5)
    created {
        userActivity(userId = 12345L)
        pastTimestamp(100)
    }
    lastChanged {
        forSession(sessionId = 67890L, timestampDaysAgo = 2)
    }
}
*/
