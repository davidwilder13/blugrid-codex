package net.blugrid.api.common.search

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Search page")
data class SearchPage<T, S>(
    val content: List<T> = emptyList(),
    val searchResults: S? = null,
    val size: Int? = 0,
    val number: Int? = 1,
    val first: Boolean? = true,
    val last: Boolean? = true,
    val numberOfElements: Int? = 0,
    val totalElements: Int? = 0,
    val totalPages: Int? = 1,
)
