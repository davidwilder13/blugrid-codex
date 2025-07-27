package net.blugrid.integration.grpc.mapper

import net.blugrid.api.common.grpc.Sort as SortProto
import net.blugrid.api.common.grpc.Order
import net.blugrid.api.common.grpc.Direction
import net.blugrid.common.model.pagination.Sort
import net.blugrid.common.model.pagination.SortOrder
import net.blugrid.common.model.pagination.SortDirection

/**
 * Convert from gRPC proto Sort to common Sort.
 */
fun SortProto.toCommonSort(): Sort {
    if (!this.sorted || this.orderByList.isEmpty()) {
        return Sort.unsorted()
    }

    val orders = this.orderByList.map { protoOrder ->
        val direction = when (protoOrder.direction) {
            Direction.DESC -> SortDirection.DESC
            Direction.ASC,
            Direction.UNRECOGNIZED,
            null -> SortDirection.ASC
        }

        SortOrder(protoOrder.property, direction, protoOrder.ignoreCase)
    }

    return Sort.by(orders)
}

/**
 * Convert from common Sort model to gRPC proto Sort.
 */
fun Sort.toProto(): SortProto {
    if (this.orders.isEmpty()) {
        return SortProto.newBuilder()
            .setSorted(false)
            .build()
    }

    val protoOrders = this.orders.map { order ->
        val direction = when (order.direction) {
            SortDirection.DESC -> Direction.DESC
            SortDirection.ASC -> Direction.ASC
        }

        Order.newBuilder()
            .setProperty(order.property)
            .setDirection(direction)
            .setIgnoreCase(order.ignoreCase)
            .build()
    }

    return SortProto.newBuilder()
        .addAllOrderBy(protoOrders)
        .setSorted(true)
        .build()
}

/**
 * Extension function to easily convert string to Sort
 */
fun String.toSort(direction: SortDirection = SortDirection.ASC): Sort =
    Sort.by(direction, this)

/**
 * Extension function for multiple properties
 */
fun List<String>.toSort(direction: SortDirection = SortDirection.ASC): Sort =
    Sort.by(this.map { SortOrder(it, direction) })
