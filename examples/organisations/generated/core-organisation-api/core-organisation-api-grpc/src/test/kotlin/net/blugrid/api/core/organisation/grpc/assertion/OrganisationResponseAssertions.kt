package net.blugrid.api.core.organisation.grpc.assertion

import net.blugrid.api.core.organisation.grpc.OrganisationResponse
import net.blugrid.api.core.organisation.model.Organisation
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.equalTo
import java.time.LocalDateTime
import java.util.UUID

fun OrganisationResponse.assert(
    id: Long? = null,
    uuid: UUID? = null,
    parentOrganisationId: Long? = null,
    effectiveTimestamp: LocalDateTime? = null
): OrganisationResponse = apply {
    id?.let { assertThat(this.id, equalTo(it)) }
    uuid?.let { assertThat(UUID.fromString(this.uuid), equalTo(it)) }
    parentOrganisationId?.let { assertThat(this.parentOrganisationId, equalTo(it)) }
    effectiveTimestamp?.let { assertThat(LocalDateTime.parse(this.effectiveTimestamp), equalTo(it)) }
}
