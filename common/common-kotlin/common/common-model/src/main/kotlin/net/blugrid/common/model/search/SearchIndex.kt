package net.blugrid.common.model.search

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.common.model.resource.ResourceType
import java.time.LocalDateTime

@Schema(description = "Represents a search index entry, storing searchable metadata for a resource.")
data class SearchIndex<T>(
    @Schema(description = "The unique identifier of the resource.", example = "12345")
    val resourceId: Long,

    @Schema(description = "The type of resource being indexed, such as 'Document', 'User', etc.", example = "DOCUMENT")
    val resourceType: ResourceType,

    @Schema(description = "The resource object itself, which can be of any type.", example = "ResourceObject")
    val resource: T,

    @Schema(description = "The unique identifier of the tenant associated with this resource.", example = "1001")
    val tenantId: Long,

    @Schema(description = "A string containing search terms related to the resource, used for indexing.", example = "sample, document, user guide")
    val searchTerms: String,

    @Schema(description = "The timestamp when the resource was created in the index.", example = "2024-08-25T10:15:30")
    val createdTimestamp: LocalDateTime? = null,

    @Schema(description = "The timestamp when the resource was last changed in the index.", example = "2024-08-26T12:30:45")
    val lastChangedTimestamp: LocalDateTime? = null
)
