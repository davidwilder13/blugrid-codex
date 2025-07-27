package net.blugrid.common.model.pagination

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Represents a query with optional sorting criteria.")
class SortableQuery<T>(
    @Schema(description = "The query object containing the criteria for the search or filtering.", example = "SomeQueryObject")
    val query: T,

    @Schema(description = "The sorting criteria to apply to the query results.", example = "[{\"field\": \"name\", \"direction\": \"ASC\"}]")
    val sort: List<SortField>? = null,
)
