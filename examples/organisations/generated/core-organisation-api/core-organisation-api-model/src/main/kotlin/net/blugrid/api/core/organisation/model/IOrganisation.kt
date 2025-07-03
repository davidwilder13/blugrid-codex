package net.blugrid.api.core.organisation.model

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Base organisation interface representing a organisation within the system.")
interface IOrganisation {
    var parentOrganisationId: Long
    var effectiveTimestamp: LocalDateTime
}
