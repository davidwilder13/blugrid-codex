package net.blugrid.common.model.pagination

/**
 * Converts Pageable to PageableQuery with a query object
 */
fun <T> Pageable.toPageableQuery(query: T): PageableQuery<T> {
    return PageableQuery(
        number = this.number,
        size = this.size,
        sort = this.sort.toSortFields().takeIf { it.isNotEmpty() },
        query = query
    )
}

/**
 * Converts PageableQuery back to Pageable
 */
fun PageableQuery<*>.toPageable(): Pageable {
    val sortOrUnsorted = sort
        ?.let { sortFields ->
            Sort.by(sortFields.map { it.toSortOrder() })
        }
        ?: Sort.unsorted()

    return PageRequest.of(number, size, sortOrUnsorted)
}

/**
 * Converts SortField to SortOrder
 */
private fun SortField.toSortOrder(): SortOrder {
    return SortOrder(
        property = field,
        direction = direction // Both use SortDirection now
    )
}

/**
 * Converts Sort to list of SortFields
 */
fun Sort.toSortFields(): List<SortField> =
    this.orders.map { order ->
        SortField(
            field = order.property,
            direction = order.direction // Both use SortDirection now
        )
    }

/**
 * No conversion needed anymore - both use SortDirection
 */

// Extension functions for easier usage
/**
 * Creates a PageableQuery from PageRequest and query
 */
fun <T> PageRequest.withQuery(query: T): PageableQuery<T> {
    return this.toPageableQuery(query)
}

/**
 * Creates a PageRequest from PageableQuery (discarding the query part)
 */
fun PageableQuery<*>.toPageRequest(): PageRequest {
    return this.toPageable() as PageRequest
}

/**
 * Converts Sort to PageableQuery sort format
 */
fun Sort.toQuerySort(): List<SortField>? {
    val fields = this.toSortFields()
    return if (fields.isEmpty()) null else fields
}

/**
 * Creates Sort from SortFields
 */
fun List<SortField>.toSort(): Sort {
    return Sort(this.map { it.toSortOrder() })
}
