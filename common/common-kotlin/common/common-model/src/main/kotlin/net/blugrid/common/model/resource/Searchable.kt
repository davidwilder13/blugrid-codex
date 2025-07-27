package net.blugrid.common.model.resource

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Base searchable interface")
interface Searchable {

    @get:JsonIgnore
    val resourceType: ResourceType

    @get:JsonIgnore
    val searchTerms: String?
}
