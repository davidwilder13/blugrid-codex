package net.blugrid.common.model.pagination

/**
 * Framework-agnostic pageable interface
 * Replaces: io.micronaut.data.model.Pageable
 */
interface Pageable {
    val number: Int
    val size: Int
    val offset: Long
    val sort: Sort
    val isPaged: Boolean
    val isUnpaged: Boolean

    fun next(): Pageable
    fun previous(): Pageable
    fun first(): Pageable
    fun withPage(pageNumber: Int): Pageable
    fun withSize(pageSize: Int): Pageable
    fun withSort(sort: Sort): Pageable

    companion object {
        fun of(page: Int, size: Int): Pageable = PageRequest.of(page, size)
        fun of(page: Int, size: Int, sort: Sort): Pageable = PageRequest.of(page, size, sort)
        fun unpaged(): Pageable = UnpagedRequest
        fun from(page: Int, size: Int): Pageable = of(page, size)
        fun from(page: Int, size: Int, sort: Sort): Pageable = of(page, size, sort)
    }
}
