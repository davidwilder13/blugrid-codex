package net.blugrid.common.model.resource

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.common.domain.IdentityID
import net.blugrid.common.domain.IdentityUUID

@Schema(description = "Base resource")
interface BaseResource<T> {
    var id: IdentityID
    var uuid: IdentityUUID
}
