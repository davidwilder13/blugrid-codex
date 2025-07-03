package net.blugrid.api.core.organisation.factory

import io.github.serpro69.kfaker.faker
import net.blugrid.api.core.organisation.model.OrganisationCreate
import net.blugrid.api.core.organisation.model.OrganisationUpdate
import net.blugrid.common.domain.IdentityID
import net.blugrid.common.domain.IdentityUUID
import java.time.LocalDateTime
import java.util.UUID

private val fake = faker { }

fun organisationCreate(block: OrganisationCreateBuilder.() -> Unit = {}): OrganisationCreate =
    OrganisationCreateBuilder().apply(block).build()

fun organisationUpdate(block: OrganisationUpdateBuilder.() -> Unit): OrganisationUpdate =
    OrganisationUpdateBuilder().apply(block).build()

class OrganisationCreateBuilder {
    var uuid: IdentityUUID = IdentityUUID(UUID.randomUUID())
    var parentOrganisationId: Long = fake.random.nextLong()
    var effectiveTimestamp: LocalDateTime = LocalDateTime.now()

    fun build() = OrganisationCreate(
        uuid = uuid,
        parentOrganisationId = parentOrganisationId,
        effectiveTimestamp = effectiveTimestamp
    )
}

class OrganisationUpdateBuilder {
    var id: IdentityID = IdentityID(fake.random.nextLong())
    var uuid: IdentityUUID = IdentityUUID(UUID.randomUUID())
    var parentOrganisationId: Long = fake.random.nextLong()
    var effectiveTimestamp: LocalDateTime = LocalDateTime.now().plusDays(1)

    fun build() = OrganisationUpdate(
        id = id,
        uuid = uuid,
        parentOrganisationId = parentOrganisationId,
        effectiveTimestamp = effectiveTimestamp
    )
}
