package net.blugrid.common.model.pagination

/**
 * Framework-agnostic page request implementation
 * Replaces: io.micronaut.data.model.Pageable implementation
 */
data class PageRequest(
    override val number: Int = 0,
    override val size: Int = 20,
    private val sortOrders: List<SortOrder> = emptyList()
) : Pageable {

    init {
        require(number >= 0) { "Page number must not be negative" }
        require(size > 0) { "Page size must be positive" }
    }

    override val offset: Long get() = (number * size).toLong()
    override val sort: Sort get() = Sort(sortOrders)
    override val isPaged: Boolean get() = true
    override val isUnpaged: Boolean get() = false

    override fun next(): Pageable = PageRequest(number + 1, size, sortOrders)

    override fun previous(): Pageable =
        if (number == 0) this else PageRequest(number - 1, size, sortOrders)

    override fun first(): Pageable = PageRequest(0, size, sortOrders)

    override fun withPage(pageNumber: Int): Pageable =
        PageRequest(pageNumber, size, sortOrders)

    override fun withSize(pageSize: Int): Pageable =
        PageRequest(number, pageSize, sortOrders)

    override fun withSort(sort: Sort): Pageable =
        PageRequest(number, size, sort.orders)

    companion object {
        fun of(page: Int, size: Int): PageRequest = PageRequest(page, size)

        fun of(page: Int, size: Int, sort: Sort): PageRequest =
            PageRequest(page, size, sort.orders)

        fun of(page: Int, size: Int, sortOrders: List<SortOrder>): PageRequest =
            PageRequest(page, size, sortOrders)

        fun of(page: Int, size: Int, vararg sortOrders: SortOrder): PageRequest =
            PageRequest(page, size, sortOrders.toList())
    }
}
