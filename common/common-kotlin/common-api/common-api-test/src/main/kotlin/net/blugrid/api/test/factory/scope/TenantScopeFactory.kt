package net.blugrid.api.test.factory.scope

import net.blugrid.api.common.model.scope.TenantScope
import net.blugrid.api.test.factory.base.BaseFactory
import net.blugrid.api.test.factory.base.RandomizableFactory
import net.blugrid.api.test.factory.base.ScenarioFactory
import net.blugrid.api.test.generator.IdentityIDRandom
import net.blugrid.common.domain.IdentityID
import net.blugrid.api.test.generator.random

object TenantScopeFactory : BaseFactory<TenantScope>,
                           RandomizableFactory<TenantScope>,
                           ScenarioFactory<TenantScope> {

    override fun createDefault(): TenantScope = create()

    fun create(
        tenantId: IdentityID? = IdentityIDRandom.Common.tenantId(),
        isExternalResource: Boolean? = false
    ): TenantScope = TenantScope(tenantId).apply {
        this.isExternalResource = isExternalResource
    }

    fun createExternal(tenantId: IdentityID? = null) = create(
        tenantId = tenantId,
        isExternalResource = true
    )

    fun createInternal(tenantId: IdentityID? = null) = create(
        tenantId = tenantId,
        isExternalResource = false
    )

    fun createForTenant(tenantId: IdentityID) = create(
        tenantId = tenantId,
        isExternalResource = false
    )

    override fun createRandom(): TenantScope = create(
        tenantId = IdentityIDRandom.Common.tenantId(),
        isExternalResource = Boolean.random()
    )

    override fun createForScenario(scenario: String): TenantScope = when (scenario) {
        "external" -> createExternal()
        "internal" -> createInternal()
        "specific-tenant" -> create(tenantId = IdentityIDRandom.Common.TENANT_1)
        "multi-tenant-1" -> create(tenantId = IdentityIDRandom.Common.TENANT_1, isExternalResource = false)
        "multi-tenant-2" -> create(tenantId = IdentityIDRandom.Common.TENANT_2, isExternalResource = false)
        else -> createDefault()
    }

    /**
     * Builder DSL for TenantScope
     */
    class Builder : BaseFactory.Builder<TenantScope> {
        private var tenantId: IdentityID? = null
        private var isExternalResource: Boolean? = null

        fun tenantId(tenantId: IdentityID) = apply { this.tenantId = tenantId }
        fun tenantId(tenantId: Long) = apply { this.tenantId = IdentityID(tenantId) }
        fun isExternalResource(isExternal: Boolean) = apply { this.isExternalResource = isExternal }

        // Convenience methods
        fun external() = apply { this.isExternalResource = true }
        fun internal() = apply { this.isExternalResource = false }
        fun randomExternalFlag() = apply { this.isExternalResource = Boolean.random() }

        // Common tenant IDs
        fun tenant1() = apply { this.tenantId = IdentityIDRandom.Common.TENANT_1 }
        fun tenant2() = apply { this.tenantId = IdentityIDRandom.Common.TENANT_2 }
        fun randomTenantId() = apply { this.tenantId = IdentityIDRandom.Common.tenantId() }

        // Scenario shortcuts
        fun externalResource() = apply {
            this.isExternalResource = true
            this.tenantId = tenantId ?: IdentityIDRandom.Common.tenantId()
        }

        fun internalResource() = apply {
            this.isExternalResource = false
            this.tenantId = tenantId ?: IdentityIDRandom.Common.tenantId()
        }

        fun forTenant(tenantId: IdentityID, external: Boolean = false) = apply {
            this.tenantId = tenantId
            this.isExternalResource = external
        }

        fun forTenant(tenantId: Long, external: Boolean = false) = forTenant(IdentityID(tenantId), external)

        override fun build(): TenantScope = TenantScope(
            tenantId = tenantId ?: IdentityIDRandom.Common.tenantId()
        ).apply {
            this.isExternalResource = this@Builder.isExternalResource ?: false
        }
    }

    override fun build(block: BaseFactory.Builder<TenantScope>.() -> Unit): TenantScope = Builder().apply(block).build()
}

// Usage Examples:
/*
// Basic usage - all generated
val scope1 = TenantScopeFactory.build()

// Set specific values, generate the rest
val scope2 = TenantScopeFactory.build {
    tenantId(1001L)
    external()
}

// Use convenience methods
val scope3 = TenantScopeFactory.build {
    tenant1()
    internal()
}

// Random variations
val scope4 = TenantScopeFactory.build {
    randomTenantId()
    randomExternalFlag()
}

// Scenario-based creation
val externalScope = TenantScopeFactory.build {
    externalResource()
}

val internalScope = TenantScopeFactory.build {
    forTenant(2001L, external = false)
}

// Predefined scenarios
val multiTenantScope1 = TenantScopeFactory.createForScenario("multi-tenant-1")
val externalScenario = TenantScopeFactory.createForScenario("external")

// Random list
val randomScopes = TenantScopeFactory.createRandomList(5)
*/
