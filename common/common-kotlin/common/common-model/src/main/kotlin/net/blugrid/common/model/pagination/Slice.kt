package net.blugrid.common.model.pagination

/**
 * Framework-agnostic slice result (page without total count)
 * Replaces: io.micronaut.data.model.Slice
 */
data class Slice<T>(
    val content: List<T>,
    val pageable: PageRequest,
    val hasNext: Boolean = false
) {
    val number: Int = pageable.number
    val size: Int = pageable.size
    val numberOfElements: Int = content.size
    val hasContent: Boolean = content.isNotEmpty()
    val hasPrevious: Boolean = number > 0
    val isFirst: Boolean = !hasPrevious
    val isLast: Boolean = !hasNext
    val isEmpty: Boolean = content.isEmpty()

    fun <U> map(converter: (T) -> U): Slice<U> {
        return Slice(
            content = content.map(converter),
            pageable = pageable,
            hasNext = hasNext
        )
    }

    companion object {
        fun <T> empty(pageable: PageRequest = PageRequest.of(0, 20)): Slice<T> {
            return Slice(emptyList(), pageable, false)
        }

        fun <T> of(content: List<T>, pageable: PageRequest, hasNext: Boolean = false): Slice<T> {
            return Slice(content, pageable, hasNext)
        }
    }
}
