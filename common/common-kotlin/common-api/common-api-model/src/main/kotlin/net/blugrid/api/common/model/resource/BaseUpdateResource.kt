package net.blugrid.api.common.model.resource

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.common.domain.IdentityID
import net.blugrid.common.domain.IdentityUUID

@Schema(description = "Base update resource")
abstract class BaseUpdateResource<T>(
    @Schema(
        description = "Unique identifier of the resource to be updated.",
        example = "1"
    )
    open var id: IdentityID,

    @Schema(
        description = "Universally unique identifier for the resource. This ID is generated upon creation and is used to uniquely identify the resource across systems.",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    )
    override var uuid: IdentityUUID
) : BaseCreateOrUpdateResource<T>
