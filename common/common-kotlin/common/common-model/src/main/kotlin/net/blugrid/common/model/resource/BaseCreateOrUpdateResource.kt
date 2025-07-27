package net.blugrid.common.model.resource

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.common.domain.IdentityUUID

@Schema(description = "Base create or update resource")
interface BaseCreateOrUpdateResource<T> {
    var uuid: IdentityUUID
}
