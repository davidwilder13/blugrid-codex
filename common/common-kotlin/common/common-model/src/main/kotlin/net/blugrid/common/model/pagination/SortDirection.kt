package net.blugrid.common.model.pagination

/**
 * Sort direction enumeration
 * Replaces: io.micronaut.data.model.Sort.Order.Direction
 */
enum class SortDirection {
    ASC,
    DESC;

    fun isAscending(): Boolean = this == ASC
    fun isDescending(): Boolean = this == DESC
}
