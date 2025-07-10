package net.blugrid.api.test.factory.security

import net.blugrid.api.security.model.AuthenticatedOrganisation
import net.blugrid.api.test.factory.base.BaseFactory
import net.blugrid.api.test.factory.base.RandomizableFactory
import net.blugrid.api.test.factory.base.ScenarioFactory
import net.blugrid.api.test.generator.random
import net.blugrid.api.test.generator.randomCompany
import net.blugrid.api.test.generator.randomId

object AuthenticatedOrganisationFactory : BaseFactory<AuthenticatedOrganisation>,
    RandomizableFactory<AuthenticatedOrganisation>,
    ScenarioFactory<AuthenticatedOrganisation> {

    override fun createDefault(): AuthenticatedOrganisation = create()

    fun create(
        tenantId: String = Long.randomId().toString(),
        displayName: String? = String.randomCompany(),
        partyId: String? = null,
        primaryPartyId: String? = null
    ): AuthenticatedOrganisation = AuthenticatedOrganisation(
        tenantId = tenantId,
        displayName = displayName,
        partyId = partyId,
        primaryPartyId = primaryPartyId
    )

    fun createWithParty(
        tenantId: String = Long.randomId().toString(),
        partyId: String = Long.randomId().toString(),
        primaryPartyId: String? = partyId
    ) = create(
        tenantId = tenantId,
        partyId = partyId,
        primaryPartyId = primaryPartyId
    )

    fun createMinimal(
        tenantId: String = Long.randomId().toString()
    ) = create(
        tenantId = tenantId,
        displayName = null,
        partyId = null,
        primaryPartyId = null
    )

    fun createWithDisplayName(
        tenantId: String = Long.randomId().toString(),
        displayName: String
    ) = create(
        tenantId = tenantId,
        displayName = displayName
    )

    override fun createRandom(): AuthenticatedOrganisation = create(
        tenantId = Long.randomId().toString(),
        displayName = if (Boolean.random()) String.randomCompany() else null,
        partyId = if (Boolean.random()) Long.randomId().toString() else null,
        primaryPartyId = if (Boolean.random()) Long.randomId().toString() else null
    )

    override fun createForScenario(scenario: String): AuthenticatedOrganisation = when (scenario) {
        "minimal" -> createMinimal()
        "with-party" -> createWithParty()
        "full" -> create(
            displayName = String.randomCompany(),
            partyId = Long.randomId().toString(),
            primaryPartyId = Long.randomId().toString()
        )

        "enterprise" -> create(
            displayName = "${String.randomCompany()} Enterprise",
            partyId = Long.randomId().toString(),
            primaryPartyId = Long.randomId().toString()
        )

        "startup" -> create(
            displayName = "${String.randomCompany()} Startup",
            partyId = Long.randomId().toString()
        )

        "government" -> create(
            displayName = "${String.randomCompany()} Government",
            partyId = Long.randomId().toString()
        )

        else -> createDefault()
    }

    /**
     * Builder DSL for AuthenticatedOrganisation
     */
    class Builder : BaseFactory.Builder<AuthenticatedOrganisation> {
        private var tenantId: String? = null
        private var displayName: String? = null
        private var partyId: String? = null
        private var primaryPartyId: String? = null

        fun tenantId(tenantId: String) = apply { this.tenantId = tenantId }
        fun tenantId(tenantId: Long) = apply { this.tenantId = tenantId.toString() }
        fun displayName(displayName: String?) = apply { this.displayName = displayName }
        fun partyId(partyId: String?) = apply { this.partyId = partyId }
        fun partyId(partyId: Long?) = apply { this.partyId = partyId?.toString() }
        fun primaryPartyId(primaryPartyId: String?) = apply { this.primaryPartyId = primaryPartyId }
        fun primaryPartyId(primaryPartyId: Long?) = apply { this.primaryPartyId = primaryPartyId?.toString() }

        // Convenience methods
        fun randomTenantId() = apply { this.tenantId = Long.randomId().toString() }
        fun randomDisplayName() = apply { this.displayName = String.randomCompany() }
        fun randomPartyId() = apply { this.partyId = Long.randomId().toString() }
        fun randomPrimaryPartyId() = apply { this.primaryPartyId = Long.randomId().toString() }
        fun withoutDisplayName() = apply { this.displayName = null }
        fun withoutParty() = apply {
            this.partyId = null
            this.primaryPartyId = null
        }

        // Party convenience methods
        fun withPartyIds(partyId: Long, primaryPartyId: Long? = null) = apply {
            this.partyId = partyId.toString()
            this.primaryPartyId = (primaryPartyId ?: partyId).toString()
        }

        fun withSamePartyIds(partyId: Long) = apply {
            withPartyIds(partyId, partyId)
        }

        fun randomPartyIds() = apply {
            val partyId = Long.randomId()
            withPartyIds(partyId, if (Boolean.random()) partyId else Long.randomId())
        }

        // Name convenience methods
        fun namedOrganisation(baseName: String) = apply {
            this.displayName = baseName
        }

        fun corporationName() = apply {
            this.displayName = "${String.randomCompany()} Corporation"
        }

        fun limitedCompanyName() = apply {
            this.displayName = "${String.randomCompany()} Ltd."
        }

        fun incorporatedName() = apply {
            this.displayName = "${String.randomCompany()} Inc."
        }

        fun groupName() = apply {
            this.displayName = "${String.randomCompany()} Group"
        }

        // Scenario builders
        fun minimalOrganisation() = apply {
            withoutDisplayName()
            withoutParty()
        }

        fun smallBusiness() = apply {
            randomDisplayName()
            this.partyId = Long.randomId().toString()
            this.primaryPartyId = this.partyId
        }

        fun mediumBusiness() = apply {
            randomDisplayName()
            val partyId = Long.randomId()
            withSamePartyIds(partyId)
        }

        fun largeBusiness() = apply {
            corporationName()
            val partyId = Long.randomId()
            withSamePartyIds(partyId)
        }

        fun enterprise() = apply {
            groupName()
            randomPartyIds()
        }

        fun multiTenant(tenantIdBase: Long) = apply {
            this.tenantId = tenantIdBase.toString()
            incorporatedName()
            val partyBase = tenantIdBase * 1000
            withPartyIds(partyBase, partyBase + 1)
        }

        override fun build(): AuthenticatedOrganisation = AuthenticatedOrganisation(
            tenantId = tenantId ?: Long.randomId().toString(),
            displayName = displayName,
            partyId = partyId,
            primaryPartyId = primaryPartyId
        )
    }

    override fun build(block: BaseFactory.Builder<AuthenticatedOrganisation>.() -> Unit): AuthenticatedOrganisation =
        Builder().apply(block).build()
}
