package net.blugrid.common.model.pagination

/**
 * Cursor-based pagination request for large datasets
 * Alternative to offset-based pagination
 */
data class CursorPageRequest<T : Comparable<T>>(
    val cursor: T? = null,
    val size: Int = 20,
    val sort: List<SortOrder> = emptyList()
) {
    init {
        require(size > 0) { "Page size must be positive" }
    }

    companion object {
        fun <T : Comparable<T>> of(size: Int): CursorPageRequest<T> =
            CursorPageRequest(null, size)

        fun <T : Comparable<T>> of(cursor: T, size: Int): CursorPageRequest<T> =
            CursorPageRequest(cursor, size)
    }
}
