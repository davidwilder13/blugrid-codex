package net.blugrid.common.model.pagination

/**
 * Framework-agnostic page result
 * Replaces: io.micronaut.data.model.Page
 */
data class Page<T>(
    val content: List<T>,
    val pageable: PageRequest,
    val totalElements: Long
) {
    val totalPages: Int = if (pageable.size == 0) 1 else ((totalElements + pageable.size - 1) / pageable.size).toInt()
    val number: Int = pageable.number
    val size: Int = pageable.size
    val numberOfElements: Int = content.size
    val hasContent: Boolean = content.isNotEmpty()
    val hasNext: Boolean = number + 1 < totalPages
    val hasPrevious: Boolean = number > 0
    val isFirst: Boolean = !hasPrevious
    val isLast: Boolean = !hasNext
    val isEmpty: Boolean = content.isEmpty()

    fun <U> map(converter: (T) -> U): Page<U> {
        return Page(
            content = content.map(converter),
            pageable = pageable,
            totalElements = totalElements
        )
    }

    companion object {
        fun <T> empty(pageable: PageRequest = PageRequest.of(0, 20)): Page<T> {
            return Page(emptyList(), pageable, 0)
        }

        fun <T> of(content: List<T>, pageable: PageRequest, total: Long): Page<T> {
            return Page(content, pageable, total)
        }
    }
}
