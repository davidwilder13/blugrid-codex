package net.blugrid.common.model.search

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Represents a paginated response for search results, including content and metadata about the search.")
data class SearchPage<T, S>(
    @Schema(description = "The list of content items returned by the search query.", example = "List of items")
    val content: List<T> = emptyList(),

    @Schema(description = "Additional search results or metadata related to the search query.", example = "SearchResultMetadata")
    val searchResults: S? = null,

    @Schema(description = "The number of items per page.", example = "20")
    val size: Int? = 0,

    @Schema(description = "The current page number, starting from 1.", example = "1")
    val number: Int? = 1,

    @Schema(description = "Indicates whether this is the first page of results.", example = "true")
    val first: Boolean? = true,

    @Schema(description = "Indicates whether this is the last page of results.", example = "true")
    val last: Boolean? = true,

    @Schema(description = "The number of elements in the current page.", example = "20")
    val numberOfElements: Int? = 0,

    @Schema(description = "The total number of elements across all pages.", example = "100")
    val totalElements: Int? = 0,

    @Schema(description = "The total number of pages available.", example = "5")
    val totalPages: Int? = 1,
)
