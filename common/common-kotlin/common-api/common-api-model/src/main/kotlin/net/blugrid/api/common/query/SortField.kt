package net.blugrid.api.common.query

import io.micronaut.data.model.Sort
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Represents a field by which results can be sorted, including the direction of the sort.")
class SortField(
    @Schema(description = "The name of the field by which to sort the results.", example = "name")
    val field: String,

    @Schema(description = "The direction of the sort, either ascending or descending.", example = "ASC")
    val direction: Sort.Order.Direction = Sort.Order.Direction.ASC
)
