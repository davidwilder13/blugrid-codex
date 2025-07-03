package net.blugrid.common.grpc.mapper

import net.blugrid.api.common.grpc.Sort
import net.blugrid.api.common.grpc.Order
import net.blugrid.api.common.grpc.Direction
import io.micronaut.data.model.Sort as MnSort

/**
 * Convert from gRPC proto Sort to Micronaut Data Sort.
 */
fun Sort.toMicronautSort(): MnSort {
    if (!this.sorted || this.orderByList.isEmpty()) {
        return MnSort.unsorted()
    }

    val orders = this.orderByList.map {
        val direction = when (it.direction) {
            Direction.DESC -> MnSort.Order.Direction.DESC
            Direction.ASC, Direction.UNRECOGNIZED, null -> MnSort.Order.Direction.ASC
        }

        MnSort.Order(it.property, direction, it.ignoreCase)
    }

    return MnSort.of(orders)
}

/**
 * Convert from Micronaut Data Sort to gRPC proto Sort.
 */
fun MnSort.toProto(): Sort {
    if (!this.isSorted || this.orderBy.isEmpty()) {
        return Sort.newBuilder()
            .setSorted(false)
            .build()
    }

    val orders = this.orderBy.map {
        val direction = when (it.direction) {
            MnSort.Order.Direction.DESC -> Direction.DESC
            MnSort.Order.Direction.ASC -> Direction.ASC
        }

        Order.newBuilder()
            .setProperty(it.property)
            .setDirection(direction)
            .setIgnoreCase(it.isIgnoreCase)
            .build()
    }

    return Sort.newBuilder()
        .addAllOrderBy(orders)
        .setSorted(true)
        .build()
}
