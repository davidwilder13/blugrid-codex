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
    id?.let { assertThat(this.id.value, equalTo(it)) }
    uuid?.let { assertThat(this.uuid.value, equalTo(it)) }
    parentOrganisationId?.let { assertThat(this.parentOrganisationId, equalTo(it)) }
    effectiveTimestamp?.let { assertThat(this.effectiveTimestamp, equalTo(it)) }
}

fun Organisation.assertEqualTo(expected: Organisation): Organisation = apply {
    assert(
        id = expected.id.value,
        uuid = expected.uuid.value,
        parentOrganisationId = expected.parentOrganisationId,
        effectiveTimestamp = expected.effectiveTimestamp
    )
}

fun OrganisationCreate.assertValid(): OrganisationCreate = apply {
    requireNotNull(uuid) { "uuid must not be null" }
    requireNotNull(parentOrganisationId) { "parentOrganisationId must not be null" }
    requireNotNull(effectiveTimestamp) { "effectiveTimestamp must not be null" }
}

fun OrganisationUpdate.assertValid(): OrganisationUpdate = apply {
    requireNotNull(id) { "id must not be null" }
    requireNotNull(uuid) { "uuid must not be null" }
    requireNotNull(parentOrganisationId) { "parentOrganisationId must not be null" }
    requireNotNull(effectiveTimestamp) { "effectiveTimestamp must not be null" }
}
