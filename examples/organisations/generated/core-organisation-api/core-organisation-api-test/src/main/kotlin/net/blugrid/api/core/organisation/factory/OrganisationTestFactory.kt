package net.blugrid.api.core.organisation.factory

import net.blugrid.api.core.organisation.model.OrganisationCreate
import net.blugrid.api.core.organisation.model.OrganisationUpdate
import net.blugrid.api.test.factory.base.BaseFactory
import net.blugrid.api.test.factory.base.RandomizableFactory
import net.blugrid.api.test.factory.base.ScenarioFactory
import net.blugrid.api.test.generator.IdentityIDRandom
import net.blugrid.api.test.generator.IdentityUUIDRandom
import net.blugrid.api.test.generator.random
import net.blugrid.common.domain.IdentityID
import net.blugrid.common.domain.IdentityUUID
import java.time.LocalDateTime
import java.util.UUID

object OrganisationCreateFactory : BaseFactory<OrganisationCreate>,
    RandomizableFactory<OrganisationCreate>,
    ScenarioFactory<OrganisationCreate> {

    override fun createDefault(): OrganisationCreate = create()

    fun create(
        uuid: IdentityUUID = IdentityUUIDRandom.generate(),
        parentOrganisationId: Long = Long.random(),
        effectiveTimestamp: LocalDateTime = LocalDateTime.now()
    ): OrganisationCreate = OrganisationCreate(
        uuid = uuid,
        parentOrganisationId = parentOrganisationId,
        effectiveTimestamp = effectiveTimestamp
    )

    fun createWithParent(parentOrganisationId: Long) = create(
        parentOrganisationId = parentOrganisationId
    )

    fun createWithUuid(uuid: IdentityUUID) = create(
        uuid = uuid
    )

    fun createEffectiveAt(effectiveTimestamp: LocalDateTime) = create(
        effectiveTimestamp = effectiveTimestamp
    )

    override fun createRandom(): OrganisationCreate = create(
        uuid = IdentityUUIDRandom.generate(),
        parentOrganisationId = Long.random(),
        effectiveTimestamp = LocalDateTime.now().minusDays(Long.random(1, 365))
    )

    override fun createForScenario(scenario: String): OrganisationCreate = when (scenario) {
        "root" -> create(parentOrganisationId = -1L)
        "future" -> create(effectiveTimestamp = LocalDateTime.now().plusDays(Long.random(1, 30)))
        "past" -> create(effectiveTimestamp = LocalDateTime.now().minusDays(Long.random(1, 365)))
        "child" -> create(parentOrganisationId = Long.random(1, 999_999))
        else -> createDefault()
    }

    class Builder : BaseFactory.Builder<OrganisationCreate> {
        private var uuid: IdentityUUID? = null
        private var parentOrganisationId: Long? = null
        private var effectiveTimestamp: LocalDateTime? = null

        fun uuid(uuid: IdentityUUID) = apply { this.uuid = uuid }
        fun uuid(uuid: String) = apply { this.uuid = IdentityUUID(UUID.fromString(uuid)) }
        fun parentOrganisationId(parentOrganisationId: Long) = apply { this.parentOrganisationId = parentOrganisationId }
        fun effectiveTimestamp(effectiveTimestamp: LocalDateTime) = apply { this.effectiveTimestamp = effectiveTimestamp }

        // Convenience methods
        fun randomUuid() = apply { this.uuid = IdentityUUIDRandom.generate() }
        fun rootOrganisation() = apply { this.parentOrganisationId = -1L }
        fun childOrganisation(parentId: Long = Long.random(1, 999_999)) = apply { this.parentOrganisationId = parentId }
        fun effectiveNow() = apply { this.effectiveTimestamp = LocalDateTime.now() }
        fun effectiveFuture(daysFromNow: Long = 30) = apply {
            this.effectiveTimestamp = LocalDateTime.now().plusDays(daysFromNow)
        }

        fun effectivePast(daysAgo: Long = 30) = apply {
            this.effectiveTimestamp = LocalDateTime.now().minusDays(daysAgo)
        }

        fun effectiveRandom() = apply {
            this.effectiveTimestamp = LocalDateTime.now().minusDays(Long.random(1, 365))
        }

        override fun build(): OrganisationCreate = OrganisationCreate(
            uuid = uuid ?: IdentityUUIDRandom.generate(),
            parentOrganisationId = parentOrganisationId ?: Long.random(),
            effectiveTimestamp = effectiveTimestamp ?: LocalDateTime.now()
        )
    }

    override fun build(block: BaseFactory.Builder<OrganisationCreate>.() -> Unit): OrganisationCreate =
        Builder().apply(block).build()
}

object OrganisationUpdateFactory : BaseFactory<OrganisationUpdate>,
    RandomizableFactory<OrganisationUpdate>,
    ScenarioFactory<OrganisationUpdate> {

    override fun createDefault(): OrganisationUpdate = create()

    fun create(
        id: IdentityID = IdentityIDRandom.generate(),
        uuid: IdentityUUID = IdentityUUIDRandom.generate(),
        parentOrganisationId: Long = Long.random(),
        effectiveTimestamp: LocalDateTime = LocalDateTime.now().plusDays(1)
    ): OrganisationUpdate = OrganisationUpdate(
        id = id,
        uuid = uuid,
        parentOrganisationId = parentOrganisationId,
        effectiveTimestamp = effectiveTimestamp
    )

    fun createWithId(id: IdentityID) = create(id = id)

    fun createWithParent(parentOrganisationId: Long) = create(
        parentOrganisationId = parentOrganisationId
    )

    fun createEffectiveAt(effectiveTimestamp: LocalDateTime) = create(
        effectiveTimestamp = effectiveTimestamp
    )

    override fun createRandom(): OrganisationUpdate = create(
        id = IdentityIDRandom.generate(),
        uuid = IdentityUUIDRandom.generate(),
        parentOrganisationId = Long.random(),
        effectiveTimestamp = LocalDateTime.now().plusDays(Long.random(1, 30))
    )

    override fun createForScenario(scenario: String): OrganisationUpdate = when (scenario) {
        "root" -> create(parentOrganisationId = -1L)
        "future" -> create(effectiveTimestamp = LocalDateTime.now().plusDays(Long.random(1, 30)))
        "immediate" -> create(effectiveTimestamp = LocalDateTime.now())
        "child" -> create(parentOrganisationId = Long.random(1, 999_999))
        else -> createDefault()
    }

    class Builder : BaseFactory.Builder<OrganisationUpdate> {
        private var id: IdentityID? = null
        private var uuid: IdentityUUID? = null
        private var parentOrganisationId: Long? = null
        private var effectiveTimestamp: LocalDateTime? = null

        fun id(id: IdentityID) = apply { this.id = id }
        fun id(id: Long) = apply { this.id = IdentityID(id) }
        fun uuid(uuid: IdentityUUID) = apply { this.uuid = uuid }
        fun uuid(uuid: String) = apply { this.uuid = IdentityUUID(UUID.fromString(uuid)) }
        fun parentOrganisationId(parentOrganisationId: Long) = apply { this.parentOrganisationId = parentOrganisationId }
        fun effectiveTimestamp(effectiveTimestamp: LocalDateTime) = apply { this.effectiveTimestamp = effectiveTimestamp }

        // Convenience methods
        fun randomId() = apply { this.id = IdentityIDRandom.generate() }
        fun randomUuid() = apply { this.uuid = IdentityUUIDRandom.generate() }
        fun rootOrganisation() = apply { this.parentOrganisationId = -1L }
        fun childOrganisation(parentId: Long = Long.random(1, 999_999)) = apply { this.parentOrganisationId = parentId }
        fun effectiveNow() = apply { this.effectiveTimestamp = LocalDateTime.now() }
        fun effectiveFuture(daysFromNow: Long = 1) = apply {
            this.effectiveTimestamp = LocalDateTime.now().plusDays(daysFromNow)
        }

        fun effectivePast(daysAgo: Long = 1) = apply {
            this.effectiveTimestamp = LocalDateTime.now().minusDays(daysAgo)
        }

        fun effectiveRandom() = apply {
            this.effectiveTimestamp = LocalDateTime.now().plusDays(Long.random(1, 30))
        }

        override fun build(): OrganisationUpdate = OrganisationUpdate(
            id = id ?: IdentityIDRandom.generate(),
            uuid = uuid ?: IdentityUUIDRandom.generate(),
            parentOrganisationId = parentOrganisationId ?: Long.random(),
            effectiveTimestamp = effectiveTimestamp ?: LocalDateTime.now().plusDays(1)
        )
    }

    override fun build(block: BaseFactory.Builder<OrganisationUpdate>.() -> Unit): OrganisationUpdate =
        Builder().apply(block).build()
}
