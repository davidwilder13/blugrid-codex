package net.blugrid.common.model.pagination

/**
 * Cursor-based page result
 */
data class CursorPage<T, C : Comparable<C>>(
    val content: List<T>,
    val pageable: CursorPageRequest<C>,
    val nextCursor: C? = null,
    val hasNext: Boolean = false
) {
    val size: Int = pageable.size
    val numberOfElements: Int = content.size
    val hasContent: Boolean = content.isNotEmpty()
    val isEmpty: Boolean = content.isEmpty()

    fun <U> map(converter: (T) -> U): CursorPage<U, C> {
        return CursorPage(
            content = content.map(converter),
            pageable = pageable,
            nextCursor = nextCursor,
            hasNext = hasNext
        )
    }
}
