
package net.blugrid.core.organisation.model.model

import io.swagger.v3.oas.annotations.media.Schema
import [object Object]

@Schema(description = "Base organisation interface representing a organisation within the system.")
interface IOrganisation {
    var parentOrganisationId: Long
    var effectiveTimestamp: LocalDateTime
}
