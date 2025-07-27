package net.blugrid.common.model.pagination

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Represents a field by which results can be sorted, including the direction of the sort.")
class SortField(
    @Schema(description = "The name of the field by which to sort the results.", example = "name")
    val field: String,

    @Schema(description = "The direction of the sort, either ascending or descending.", example = "ASC")
    val direction: SortDirection = SortDirection.ASC
) {

    companion object {
        fun asc(field: String): SortField = SortField(field, SortDirection.ASC)
        fun desc(field: String): SortField = SortField(field, SortDirection.DESC)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SortField) return false
        return field == other.field && direction == other.direction
    }

    override fun hashCode(): Int {
        return 31 * field.hashCode() + direction.hashCode()
    }

    override fun toString(): String {
        return "SortField(field='$field', direction=$direction)"
    }
}
