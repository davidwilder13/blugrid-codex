package net.blugrid.platform.testing.factory.audit

import net.blugrid.common.model.audit.AuditStamp
import net.blugrid.common.model.audit.ResourceAudit
import net.blugrid.platform.testing.factory.base.BaseFactory
import net.blugrid.platform.testing.factory.base.RandomizableFactory
import net.blugrid.platform.testing.factory.base.ScenarioFactory
import net.blugrid.platform.testing.generator.random

object ResourceAuditFactory : BaseFactory<ResourceAudit>,
    RandomizableFactory<ResourceAudit>,
    ScenarioFactory<ResourceAudit> {

    override fun createDefault(): ResourceAudit = create()

    fun create(
        version: Int = Int.random(1, 10),
        created: AuditStamp? = AuditStampFactory.createDefault(),
        lastChanged: AuditStamp? = null
    ): ResourceAudit = ResourceAudit(
        version = version,
        created = created,
        lastChanged = lastChanged
    )

    fun createMinimal() = create(
        version = 1,
        created = AuditStampFactory.createDefault(),
        lastChanged = null
    )

    fun createWithVersion(version: Int) = create(version = version)

    fun createNew() = create(
        version = 1,
        created = AuditStampFactory.createDefault(),
        lastChanged = null
    )

    fun createModified(timesModified: Int = Int.random(2, 10)) = create(
        version = timesModified,
        created = AuditStampFactory.createPast(Long.random(365) + 30),
        lastChanged = AuditStampFactory.createPast(Long.random(30))
    )

    fun createOld(createdDaysAgo: Long = 365) = create(
        version = Int.random(10, 100),
        created = AuditStampFactory.createPast(createdDaysAgo),
        lastChanged = AuditStampFactory.createPast(Long.random(createdDaysAgo / 2))
    )

    override fun createRandom(): ResourceAudit = create(
        version = Int.random(1, 50),
        created = AuditStampFactory.createPast(Long.random(365)),
        lastChanged = if (Boolean.random()) {
            AuditStampFactory.createPast(Long.random(30))
        } else null
    )

    override fun createForScenario(scenario: String): ResourceAudit = when (scenario) {
        "new" -> createNew()
        "minimal" -> createMinimal()
        "modified" -> createModified()
        "old" -> createOld()
        "heavily-modified" -> createModified(Int.random(20, 100))
        "version-1" -> create(version = 1)
        "version-conflict" -> create(version = Int.random(50, 100))
        "recent-change" -> create(
            version = Int.random(2, 10),
            created = AuditStampFactory.createPast(Long.random(30, 365)),
            lastChanged = AuditStampFactory.createPast(Long.random(1, 5))
        )

        else -> createDefault()
    }

    /**
     * Builder DSL for ResourceAudit
     */
    class Builder : BaseFactory.Builder<ResourceAudit> {
        private var version: Int? = null
        private var created: AuditStamp? = null
        private var lastChanged: AuditStamp? = null

        fun version(version: Int) = apply { this.version = version }
        fun created(created: AuditStamp) = apply { this.created = created }
        fun lastChanged(lastChanged: AuditStamp) = apply { this.lastChanged = lastChanged }

        // Version convenience methods
        fun version1() = apply { this.version = 1 }
        fun randomVersion(max: Int = 100) = apply { this.version = Int.random(1, max) }
        fun highVersion() = apply { this.version = Int.random(50, 200) }

        // Audit stamp convenience methods
        fun createdNow() = apply { this.created = AuditStampFactory.createDefault() }
        fun createdPast(daysAgo: Long) = apply { this.created = AuditStampFactory.createPast(daysAgo) }
        fun lastChangedNow() = apply { this.lastChanged = AuditStampFactory.createDefault() }
        fun lastChangedPast(daysAgo: Long) = apply { this.lastChanged = AuditStampFactory.createPast(daysAgo) }
        fun noLastChanged() = apply { this.lastChanged = null }

        // Common scenarios
        fun newResource() = apply {
            this.version = 1
            this.created = AuditStampFactory.createDefault()
            this.lastChanged = null
        }

        fun modifiedResource(timesModified: Int = Int.random(2, 10)) = apply {
            this.version = timesModified
            this.created = AuditStampFactory.createPast(Long.random(30, 395))
            this.lastChanged = AuditStampFactory.createPast(Long.random(1, 30))
        }

        fun oldResource(createdDaysAgo: Long = 365) = apply {
            this.version = Int.random(10, 100)
            this.created = AuditStampFactory.createPast(createdDaysAgo)
            this.lastChanged = AuditStampFactory.createPast(Long.random(1, createdDaysAgo / 2))
        }

        fun heavilyModifiedResource() = apply {
            this.version = Int.random(20, 100)
            this.created = AuditStampFactory.createPast(Long.random(365, 1000))
            this.lastChanged = AuditStampFactory.createPast(Long.random(1, 7))
        }

        fun recentlyChangedResource() = apply {
            this.version = Int.random(2, 10)
            this.created = AuditStampFactory.createPast(Long.random(30, 365))
            this.lastChanged = AuditStampFactory.createPast(Long.random(1, 5))
        }

        fun versionConflictResource() = apply {
            this.version = Int.random(50, 100)
            this.created = AuditStampFactory.createPast(Long.random(100, 500))
            this.lastChanged = AuditStampFactory.createPast(Long.random(1, 10))
        }

        override fun build(): ResourceAudit = ResourceAudit(
            version = version ?: Int.random(1, 10),
            created = created ?: AuditStampFactory.createDefault(),
            lastChanged = lastChanged
        )
    }

    override fun build(block: BaseFactory.Builder<ResourceAudit>.() -> Unit): ResourceAudit = Builder().apply(block).build()
}

// Usage Examples:
/*
// Basic usage - all generated
val audit1 = ResourceAuditFactory.build()

// New resource scenario
val audit2 = ResourceAuditFactory.build {
    newResource()
}

// Modified resource with specific version
val audit3 = ResourceAuditFactory.build {
    version(5)
    created {
        sessionId(12345L)
        pastTimestamp(100)
    }
    lastChanged {
        sessionId(67890L)
        pastTimestamp(2)
    }
}

// Old resource scenario
val audit4 = ResourceAuditFactory.build {
    oldResource(createdDaysAgo = 730) // 2 years old
}

// Mix explicit and generated
val audit5 = ResourceAuditFactory.build {
    version1()
    createdRandomPast(365)
    noLastChanged()
}

// Session-specific audit
val audit6 = ResourceAuditFactory.build {
    withSameSessionAudit(sessionId = 99999L)
    version(10)
}

// Convenience methods
val audit7 = ResourceAuditFactory.build {
    highVersion()
    createdPast(180)
    lastChangedRandomPast(7)
}

// Scenario-based creation
val heavilyModified = ResourceAuditFactory.build {
    heavilyModifiedResource()
}

val recentChange = ResourceAuditFactory.build {
    recentlyChangedResource()
}

// Predefined scenarios
val newResourceAudit = ResourceAuditFactory.createForScenario("new")
val modifiedResourceAudit = ResourceAuditFactory.createForScenario("modified")
val versionConflictAudit = ResourceAuditFactory.createForScenario("version-conflict")

// Random list
val randomAudits = ResourceAuditFactory.createRandomList(5)
*/
