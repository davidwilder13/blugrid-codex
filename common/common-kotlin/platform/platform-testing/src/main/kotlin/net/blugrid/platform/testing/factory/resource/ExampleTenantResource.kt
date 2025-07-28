// ===========================================
// Example: Complete Resource Factory using the pattern
// ===========================================

package net.blugrid.platform.testing.factory.resource

import net.blugrid.common.domain.IdentityID
import net.blugrid.common.domain.IdentityUUID
import net.blugrid.common.model.audit.ResourceAudit
import net.blugrid.common.model.scope.TenantScope
import net.blugrid.platform.testing.factory.audit.ResourceAuditFactory
import net.blugrid.platform.testing.factory.base.BaseFactory
import net.blugrid.platform.testing.factory.base.RandomizableFactory
import net.blugrid.platform.testing.factory.base.ScenarioFactory
import net.blugrid.platform.testing.factory.scope.TenantScopeFactory
import net.blugrid.platform.testing.generator.IdentityIDRandom
import net.blugrid.platform.testing.generator.IdentityUUIDRandom
import net.blugrid.platform.testing.generator.random
import net.blugrid.platform.testing.generator.randomAlphanumeric
import net.blugrid.platform.testing.generator.randomCompany
import net.blugrid.platform.testing.generator.randomSentence
import java.util.UUID

// Example resource for demonstration
data class ExampleTenantResource(
    val id: IdentityID,
    val uuid: IdentityUUID,
    val name: String,
    val description: String?,
    val active: Boolean,
    val scope: TenantScope?,
    val audit: ResourceAudit?
)

object ExampleTenantResourceFactory : BaseFactory<ExampleTenantResource>,
    RandomizableFactory<ExampleTenantResource>,
    ScenarioFactory<ExampleTenantResource> {

    override fun createDefault(): ExampleTenantResource = create()

    fun create(
        id: IdentityID = IdentityIDRandom.generate(),
        uuid: IdentityUUID = IdentityUUIDRandom.generate(),
        name: String = String.randomCompany(),
        description: String? = String.randomSentence(),
        active: Boolean = true,
        scope: TenantScope? = TenantScopeFactory.createDefault(),
        audit: ResourceAudit? = ResourceAuditFactory.createDefault()
    ): ExampleTenantResource = ExampleTenantResource(
        id = id,
        uuid = uuid,
        name = name,
        description = description,
        active = active,
        scope = scope,
        audit = audit
    )

    // Helper methods for common variations
    fun createMinimal() = create(
        description = null,
        scope = null,
        audit = null
    )

    fun createWithId(id: IdentityID) = create(id = id)

    fun createWithTenant(tenantId: IdentityID) = create(
        scope = TenantScopeFactory.createForTenant(tenantId)
    )

    fun createInactive() = create(active = false)

    fun createWithName(name: String) = create(name = name)

    fun createExternal() = create(
        scope = TenantScopeFactory.createExternal()
    )

    override fun createRandom(): ExampleTenantResource = create(
        id = IdentityIDRandom.generate(),
        uuid = IdentityUUIDRandom.generate(),
        name = String.randomCompany(),
        description = if (Boolean.random()) {
            String.randomSentence()
        } else null,
        active = Boolean.random(),
        scope = if (Boolean.random()) TenantScopeFactory.createRandom() else null,
        audit = if (Boolean.random()) ResourceAuditFactory.createRandom() else null
    )

    override fun createForScenario(scenario: String): ExampleTenantResource = when (scenario) {
        "minimal" -> createMinimal()
        "new" -> create(
            audit = ResourceAuditFactory.createNew()
        )

        "inactive" -> createInactive()
        "external" -> createExternal()
        "modified" -> create(
            audit = ResourceAuditFactory.createModified()
        )

        "tenant-scoped" -> createWithTenant(IdentityIDRandom.Common.TENANT_1)
        "no-scope" -> create(scope = null)
        "no-audit" -> create(audit = null)
        "full" -> create() // all fields populated
        else -> createDefault()
    }

    /**
     * Builder DSL for ExampleTenantResource
     */
    class Builder : BaseFactory.Builder<ExampleTenantResource> {
        private var id: IdentityID? = null
        private var uuid: IdentityUUID? = null
        private var name: String? = null
        private var description: String? = null
        private var active: Boolean? = null
        private var scope: TenantScope? = null
        private var audit: ResourceAudit? = null

        fun id(id: IdentityID) = apply { this.id = id }
        fun id(id: Long) = apply { this.id = IdentityID(id) }
        fun uuid(uuid: IdentityUUID) = apply { this.uuid = uuid }
        fun uuid(uuid: String) = apply { this.uuid = IdentityUUID(UUID.fromString(uuid)) }
        fun name(name: String) = apply { this.name = name }
        fun description(description: String?) = apply { this.description = description }
        fun active(active: Boolean) = apply { this.active = active }
        fun scope(scope: TenantScope?) = apply { this.scope = scope }
        fun audit(audit: ResourceAudit?) = apply { this.audit = audit }

        // Convenience methods
        fun randomId() = apply { this.id = IdentityIDRandom.generate() }
        fun randomUuid() = apply { this.uuid = IdentityUUIDRandom.generate() }
        fun active() = apply { this.active = true }
        fun inactive() = apply { this.active = false }
        fun noDescription() = apply { this.description = null }
        fun noScope() = apply { this.scope = null }
        fun noAudit() = apply { this.audit = null }

        // Scope convenience methods
        fun internalScope() = apply {
            this.scope = TenantScopeFactory.createInternal()
        }

        fun externalScope() = apply {
            this.scope = TenantScopeFactory.createExternal()
        }

        fun forTenant(tenantId: IdentityID) = apply {
            this.scope = TenantScopeFactory.createForTenant(tenantId)
        }

        fun forTenant(tenantId: Long) = forTenant(IdentityID(tenantId))

        // Audit convenience methods
        fun newResource() = apply {
            this.active = true
            this.audit = ResourceAuditFactory.createNew()
        }

        fun modifiedResource() = apply {
            this.audit = ResourceAuditFactory.createModified()
        }

        fun oldResource() = apply {
            this.audit = ResourceAuditFactory.createOld()
        }

        // Builder methods for nested objects
        fun scope(block: TenantScopeFactory.Builder.() -> Unit) = apply {
            this.scope = TenantScopeFactory.build()
        }

        fun audit(block: ResourceAuditFactory.Builder.() -> Unit) = apply {
            this.audit = ResourceAuditFactory.build()
        }

        // Name generation convenience
        fun randomName() = apply {
            this.name = "Company ${Int.random(1000, 9999)}"
        }

        fun nameWithPrefix(prefix: String) = apply {
            this.name = "$prefix ${Int.random(1000, 9999)}"
        }

        fun randomDescription() = apply {
            this.description = "Random description: ${String.randomAlphanumeric(30)}"
        }

        // Common scenarios
        fun minimalResource() = apply {
            this.description = null
            this.scope = null
            this.audit = null
        }

        fun fullResource() = apply {
            this.id = this.id ?: IdentityIDRandom.generate()
            this.uuid = this.uuid ?: IdentityUUIDRandom.generate()
            this.name = this.name ?: String.randomCompany()
            this.description = this.description ?: String.randomSentence()
            this.active = this.active ?: true
            this.scope = this.scope ?: TenantScopeFactory.createDefault()
            this.audit = this.audit ?: ResourceAuditFactory.createDefault()
        }

        override fun build(): ExampleTenantResource = ExampleTenantResource(
            id = id ?: IdentityIDRandom.generate(),
            uuid = uuid ?: IdentityUUIDRandom.generate(),
            name = name ?: String.randomCompany(),
            description = description,
            active = active ?: true,
            scope = scope,
            audit = audit
        )
    }

    override fun build(block: BaseFactory.Builder<ExampleTenantResource>.() -> Unit): ExampleTenantResource =
        Builder().apply(block).build()
}

// Usage Examples:
/*
// Basic usage - all generated
val resource1 = ExampleTenantResourceFactory.build()

// Set specific values
val resource2 = ExampleTenantResourceFactory.build {
    id(12345L)
    name("Test Resource")
    active()
    forTenant(9999L)
}

// Use convenience methods
val resource3 = ExampleTenantResourceFactory.build {
    randomId()
    randomName()
    randomDescription()
    internalScope()
    newResource()
}

// Nested builder usage
val resource4 = ExampleTenantResourceFactory.build {
    name("Complex Resource")
    scope {
        tenantId(1001L)
        internal()
    }
    audit {
        version(5)
        createdPast(30)
        lastChangedPast(2)
    }
}

// Scenario-based creation
val minimalResource = ExampleTenantResourceFactory.createForScenario("minimal")
val externalResource = ExampleTenantResourceFactory.createForScenario("external")
val modifiedResource = ExampleTenantResourceFactory.createForScenario("modified")

// Random resources
val randomResources = ExampleTenantResourceFactory.createRandomList(10)

// Helper methods
val inactiveResource = ExampleTenantResourceFactory.createInactive()
val tenantResource = ExampleTenantResourceFactory.createWithTenant(IdentityID(5000L))
*/
