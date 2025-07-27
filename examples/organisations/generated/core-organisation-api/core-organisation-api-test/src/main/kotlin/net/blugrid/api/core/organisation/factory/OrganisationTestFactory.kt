package net.blugrid.api.core.organisation.factory

import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.core.organisation.model.OrganisationCreate
import net.blugrid.api.core.organisation.model.OrganisationUpdate
import net.blugrid.common.domain.IdentityID
import net.blugrid.common.domain.IdentityUUID
import net.blugrid.common.model.audit.ResourceAudit
import net.blugrid.platform.testing.factory.base.BaseFactory
import net.blugrid.platform.testing.factory.base.RandomizableFactory
import net.blugrid.platform.testing.factory.base.ScenarioFactory
import net.blugrid.platform.testing.generator.IdentityIDRandom
import net.blugrid.platform.testing.generator.IdentityUUIDRandom
import net.blugrid.platform.testing.generator.random
import java.time.LocalDateTime
import java.util.UUID

// =====================================================
// SIMPLIFIED CREATE FACTORY (Your Primary Usage)
// =====================================================
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

    // Simple convenience methods for common scenarios
    fun createRoot() = create(parentOrganisationId = -1L)
    fun createChild(parentId: Long) = create(parentOrganisationId = parentId)
    fun createFuture(days: Long = 30) = create(effectiveTimestamp = LocalDateTime.now().plusDays(days))
    fun createPast(days: Long = 30) = create(effectiveTimestamp = LocalDateTime.now().minusDays(days))

    override fun createRandom(): OrganisationCreate = create(
        uuid = IdentityUUIDRandom.generate(),
        parentOrganisationId = Long.random(),
        effectiveTimestamp = LocalDateTime.now().minusDays(Long.random(1, 365))
    )

    override fun createForScenario(scenario: String): OrganisationCreate = when (scenario) {
        "root" -> createRoot()
        "child" -> createChild(Long.random(1, 999_999))
        "future" -> createFuture()
        "past" -> createPast()
        else -> createDefault()
    }

    // Fix the 'up' method that's referenced in your test
    fun up(block: OrganisationUpdate.() -> Unit): OrganisationUpdate {
        val base = OrganisationUpdate(
            id = IdentityIDRandom.generate(),
            uuid = IdentityUUIDRandom.generate(),
            parentOrganisationId = Long.random(),
            effectiveTimestamp = LocalDateTime.now()
        )
        return base.apply(block)
    }

    // Simple builder for when you need more control
    override fun build(block: BaseFactory.Builder<OrganisationCreate>.() -> Unit): OrganisationCreate =
        SimpleBuilder().apply(block).build()

    private class SimpleBuilder : BaseFactory.Builder<OrganisationCreate> {
        private var uuid: IdentityUUID? = null
        private var parentOrganisationId: Long? = null
        private var effectiveTimestamp: LocalDateTime? = null

        fun uuid(uuid: IdentityUUID) = apply { this.uuid = uuid }
        fun uuid(uuid: String) = apply { this.uuid = IdentityUUID(UUID.fromString(uuid)) }
        fun parentOrganisationId(parentId: Long) = apply { this.parentOrganisationId = parentId }
        fun effectiveTimestamp(timestamp: LocalDateTime) = apply { this.effectiveTimestamp = timestamp }

        // Convenience methods
        fun rootOrganisation() = apply { this.parentOrganisationId = -1L }
        fun childOf(parentId: Long) = apply { this.parentOrganisationId = parentId }
        fun effectiveNow() = apply { this.effectiveTimestamp = LocalDateTime.now() }

        override fun build(): OrganisationCreate = OrganisationCreate(
            uuid = uuid ?: IdentityUUIDRandom.generate(),
            parentOrganisationId = parentOrganisationId ?: Long.random(),
            effectiveTimestamp = effectiveTimestamp ?: LocalDateTime.now()
        )
    }
}

// =====================================================
// MINIMAL UPDATE FACTORY
// =====================================================
object OrganisationUpdateFactory : BaseFactory<OrganisationUpdate> {

    override fun createDefault(): OrganisationUpdate = create()

    fun create(
        id: IdentityID = IdentityIDRandom.generate(),
        uuid: IdentityUUID = IdentityUUIDRandom.generate(),
        parentOrganisationId: Long = Long.random(),
        effectiveTimestamp: LocalDateTime = LocalDateTime.now()
    ): OrganisationUpdate = OrganisationUpdate(
        id = id,
        uuid = uuid,
        parentOrganisationId = parentOrganisationId,
        effectiveTimestamp = effectiveTimestamp
    )

    // Convert from existing organisation
    fun from(organisation: Organisation, changes: OrganisationUpdate.() -> Unit = {}): OrganisationUpdate =
        OrganisationUpdate(
            id = organisation.id,
            uuid = organisation.uuid,
            parentOrganisationId = organisation.parentOrganisationId,
            effectiveTimestamp = organisation.effectiveTimestamp
        ).apply(changes)

    override fun build(block: BaseFactory.Builder<OrganisationUpdate>.() -> Unit): OrganisationUpdate =
        SimpleBuilder().apply(block).build()

    private class SimpleBuilder : BaseFactory.Builder<OrganisationUpdate> {
        private var id: IdentityID? = null
        private var uuid: IdentityUUID? = null
        private var parentOrganisationId: Long? = null
        private var effectiveTimestamp: LocalDateTime? = null

        fun id(id: IdentityID) = apply { this.id = id }
        fun id(id: Long) = apply { this.id = IdentityID(id) }
        fun uuid(uuid: IdentityUUID) = apply { this.uuid = uuid }
        fun parentOrganisationId(parentId: Long) = apply { this.parentOrganisationId = parentId }
        fun effectiveTimestamp(timestamp: LocalDateTime) = apply { this.effectiveTimestamp = timestamp }

        override fun build(): OrganisationUpdate = OrganisationUpdate(
            id = id ?: IdentityIDRandom.generate(),
            uuid = uuid ?: IdentityUUIDRandom.generate(),
            parentOrganisationId = parentOrganisationId ?: Long.random(),
            effectiveTimestamp = effectiveTimestamp ?: LocalDateTime.now()
        )
    }
}

// =====================================================
// MINIMAL RESOURCE FACTORY (For Repository/Entity Tests)
// =====================================================
object OrganisationFactory : BaseFactory<Organisation> {

    override fun createDefault(): Organisation = create()

    fun create(
        id: IdentityID = IdentityIDRandom.generate(),
        uuid: IdentityUUID = IdentityUUIDRandom.generate(),
        parentOrganisationId: Long = Long.random(),
        effectiveTimestamp: LocalDateTime = LocalDateTime.now(),
        audit: ResourceAudit? = null
    ): Organisation = Organisation(
        id = id,
        uuid = uuid,
        parentOrganisationId = parentOrganisationId,
        effectiveTimestamp = effectiveTimestamp,
        audit = audit
    )

    // Create from other models (primary use case)
    fun from(createModel: OrganisationCreate, id: IdentityID = IdentityIDRandom.generate()): Organisation =
        create(
            id = id,
            uuid = createModel.uuid,
            parentOrganisationId = createModel.parentOrganisationId,
            effectiveTimestamp = createModel.effectiveTimestamp
        )

    override fun build(block: BaseFactory.Builder<Organisation>.() -> Unit): Organisation =
        SimpleBuilder().apply(block).build()

    private class SimpleBuilder : BaseFactory.Builder<Organisation> {
        private var id: IdentityID? = null
        private var uuid: IdentityUUID? = null
        private var parentOrganisationId: Long? = null
        private var effectiveTimestamp: LocalDateTime? = null
        private var audit: ResourceAudit? = null

        fun id(id: IdentityID) = apply { this.id = id }
        fun uuid(uuid: IdentityUUID) = apply { this.uuid = uuid }
        fun parentOrganisationId(parentId: Long) = apply { this.parentOrganisationId = parentId }
        fun effectiveTimestamp(timestamp: LocalDateTime) = apply { this.effectiveTimestamp = timestamp }
        fun audit(audit: ResourceAudit?) = apply { this.audit = audit }

        override fun build(): Organisation = Organisation(
            id = id ?: IdentityIDRandom.generate(),
            uuid = uuid ?: IdentityUUIDRandom.generate(),
            parentOrganisationId = parentOrganisationId ?: Long.random(),
            effectiveTimestamp = effectiveTimestamp ?: LocalDateTime.now(),
            audit = audit
        )
    }
}

// =====================================================
// EXTENSION FUNCTIONS FOR EASY CHAINING
// =====================================================

/**
 * Convert OrganisationCreate to OrganisationUpdate with the given ID and UUID
 */
fun OrganisationCreate.toUpdate(id: IdentityID, uuid: IdentityUUID = this.uuid): OrganisationUpdate =
    OrganisationUpdate(
        id = id,
        uuid = uuid,
        parentOrganisationId = this.parentOrganisationId,
        effectiveTimestamp = this.effectiveTimestamp
    )

/**
 * Convert Organisation to OrganisationUpdate for modification
 */
fun Organisation.toUpdate(): OrganisationUpdate = OrganisationUpdate(
    id = this.id,
    uuid = this.uuid,
    parentOrganisationId = this.parentOrganisationId,
    effectiveTimestamp = this.effectiveTimestamp
)

/**
 * Apply changes to an OrganisationUpdate
 */
fun OrganisationUpdate.with(block: OrganisationUpdate.() -> Unit): OrganisationUpdate =
    this.copy().apply(block)
