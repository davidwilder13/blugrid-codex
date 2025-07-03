package net.blugrid.api.core.organisation.assertion

import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.core.organisation.model.OrganisationCreate
import net.blugrid.api.core.organisation.model.OrganisationUpdate
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import java.time.LocalDateTime
import java.util.UUID

fun Organisation.assert(
    id: Long? = null,
    uuid: UUID? = null,
    parentOrganisationId: Long? = null,
    effectiveTimestamp: LocalDateTime? = null
): Organisation = apply {
    id?.let { assertThat(this.id, equalTo(it)) }
    uuid?.let { assertThat(this.uuid, equalTo(it)) }
    parentOrganisationId?.let { assertThat(this.parentOrganisationId, equalTo(it)) }
    effectiveTimestamp?.let { assertThat(this.effectiveTimestamp, equalTo(it)) }
}

fun Organisation.assertEqualTo(expected: Organisation): Organisation = apply {
    assert(
        id = expected.id,
        uuid = expected.uuid,
        parentOrganisationId = expected.parentOrganisationId,
        effectiveTimestamp = expected.effectiveTimestamp
    )
}

fun OrganisationCreate.assertValid(): OrganisationCreate = apply {
    requireNotNull(uuid)
    requireNotNull(parentOrganisationId)
    requireNotNull(effectiveTimestamp)
}

fun OrganisationUpdate.assertValid(): OrganisationUpdate = apply {
    requireNotNull(id)
    requireNotNull(uuid)
    requireNotNull(parentOrganisationId)
    requireNotNull(effectiveTimestamp)
}
