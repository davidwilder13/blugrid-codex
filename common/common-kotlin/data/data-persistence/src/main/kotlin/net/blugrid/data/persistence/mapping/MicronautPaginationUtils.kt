package net.blugrid.data.persistence.mapping

import net.blugrid.common.model.pagination.Page
import net.blugrid.common.model.pagination.PageRequest
import net.blugrid.common.model.pagination.Pageable
import net.blugrid.common.model.pagination.Sort
import net.blugrid.common.model.pagination.SortDirection
import net.blugrid.common.model.pagination.SortOrder

/**
 * Extension functions for converting between framework-agnostic pagination types
 * and Micronaut's pagination types.
 */

// Framework-agnostic to Micronaut
fun Pageable.toMicronautPageable(): io.micronaut.data.model.Pageable {
    return if (this.isUnpaged) {
        io.micronaut.data.model.Pageable.UNPAGED
    } else {
        val micronautSort = this.sort.toMicronautSort()
        io.micronaut.data.model.Pageable.from(this.number, this.size, micronautSort)
    }
}

fun Sort.toMicronautSort(): io.micronaut.data.model.Sort {
    if (this.orders.isEmpty()) {
        return io.micronaut.data.model.Sort.UNSORTED
    }

    val micronautOrders: List<io.micronaut.data.model.Sort.Order> = this.orders.map { order ->
        val direction = when (order.direction) {
            SortDirection.ASC -> io.micronaut.data.model.Sort.Order.Direction.ASC
            SortDirection.DESC -> io.micronaut.data.model.Sort.Order.Direction.DESC
        }
        // Use constructor - io.micronaut.data.model.Sort.Order(property, direction, ignoreCase)
        io.micronaut.data.model.Sort.Order(order.property, direction, order.ignoreCase)
    }

    return io.micronaut.data.model.Sort.of(micronautOrders)
}

// Micronaut to framework-agnostic
fun <T, R> io.micronaut.data.model.Page<T>.toFrameworkAgnosticPage(mapper: (T) -> R): Page<R> {
    val mappedContent = this.content.map(mapper)
    val pageable = PageRequest.of(this.pageNumber, this.size)

    // Micronaut Page has getTotalSize() method
    return Page.of(mappedContent, pageable, this.totalSize)
}

fun io.micronaut.data.model.Sort.toFrameworkAgnosticSort(): Sort {
    if (!this.isSorted) {
        return Sort.unsorted()
    }

    val orders = this.orderBy.map { order ->
        val direction = when (order.direction) {
            io.micronaut.data.model.Sort.Order.Direction.ASC -> SortDirection.ASC
            io.micronaut.data.model.Sort.Order.Direction.DESC -> SortDirection.DESC
        }
        SortOrder(order.property, direction, order.isIgnoreCase)
    }

    return Sort.by(orders)
}
