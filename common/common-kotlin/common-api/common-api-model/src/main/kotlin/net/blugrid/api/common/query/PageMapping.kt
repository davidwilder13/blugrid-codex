package net.blugrid.api.common.query

import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort


fun <T> Pageable.toPageableQuery(query: T): PageableQuery<T> {
    val pageRequest = this
    return PageableQuery(
        number = pageRequest.number,
        size = pageRequest.size,
        sort = pageRequest.sort.toSortFields().takeIf { it.isNotEmpty() },
        query = query
    )
}

fun PageableQuery<*>.toPageable(): Pageable {
    val sortOrUnsorted = sort
        ?.let {
            Sort.of(it.map { it.toSortOrder() })
        }
        ?: Sort.unsorted()

    return DefaultPageRequest(number, size, sortOrUnsorted)
}

private fun SortField.toSortOrder(): Sort.Order {
    return Sort.Order(field, direction, true)
}

fun Sort.toSortFields(): List<SortField> = this.orderBy.map { SortField(field = it.property, direction = it.direction) }.toList()
