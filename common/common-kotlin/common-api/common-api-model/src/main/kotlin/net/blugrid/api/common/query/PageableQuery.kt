package net.blugrid.api.common.query

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Represents a pageable query with customizable pagination and sorting options.")
open class PageableQuery<T>(
    @Schema(description = "The query object containing the criteria for the search or filtering.", example = "SomeQueryObject")
    open val query: T,

    @Schema(description = "The page number to retrieve, starting from 0.", example = "0")
    open val number: Int = DEFAULT_NUMBER,

    @Schema(description = "The number of records to retrieve per page.", example = "50")
    open val size: Int = DEFAULT_SIZE,

    @Schema(description = "The sorting criteria to apply to the query results.", example = "[{\"field\": \"name\", \"direction\": \"ASC\"}]")
    open val sort: List<SortField>? = null,
) {
    companion object {
        const val DEFAULT_NUMBER = 0
        const val DEFAULT_SIZE = 50
    }
}
