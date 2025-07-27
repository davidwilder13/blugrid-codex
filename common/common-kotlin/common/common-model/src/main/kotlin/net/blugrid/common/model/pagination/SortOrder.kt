package net.blugrid.common.model.pagination

/**
 * Framework-agnostic sort order specification
 * Replaces: io.micronaut.data.model.Sort.Order
 */
data class SortOrder(
    val property: String,
    val direction: SortDirection = SortDirection.ASC,
    val ignoreCase: Boolean = false
) {
    companion object {
        fun asc(property: String): SortOrder = SortOrder(property, SortDirection.ASC)
        fun desc(property: String): SortOrder = SortOrder(property, SortDirection.DESC)

        fun asc(property: String, ignoreCase: Boolean): SortOrder =
            SortOrder(property, SortDirection.ASC, ignoreCase)

        fun desc(property: String, ignoreCase: Boolean): SortOrder =
            SortOrder(property, SortDirection.DESC, ignoreCase)
    }
}
