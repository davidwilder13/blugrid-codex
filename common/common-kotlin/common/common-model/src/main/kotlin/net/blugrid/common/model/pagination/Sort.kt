package net.blugrid.common.model.pagination

/**
 * Framework-agnostic sort specification
 * Replaces: io.micronaut.data.model.Sort
 */
data class Sort(
    val orders: List<SortOrder>
) {
    constructor(vararg orders: SortOrder) : this(orders.toList())

    fun and(sort: Sort): Sort = Sort(orders + sort.orders)

    fun ascending(property: String): Sort =
        Sort(orders + SortOrder.asc(property))

    fun descending(property: String): Sort =
        Sort(orders + SortOrder.desc(property))

    companion object {
        fun by(orders: List<SortOrder>): Sort = Sort(orders)
        fun by(vararg orders: SortOrder): Sort = Sort(orders.toList())
        fun by(property: String): Sort = Sort(SortOrder.asc(property))
        fun by(direction: SortDirection, property: String): Sort = Sort(SortOrder(property, direction))
        fun unsorted(): Sort = Sort(emptyList())
    }
}
