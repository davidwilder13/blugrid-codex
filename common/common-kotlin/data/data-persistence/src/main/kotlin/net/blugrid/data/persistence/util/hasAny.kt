package net.blugrid.data.persistence.util

import net.blugrid.data.persistence.model.contract.HasUuid

fun <T : HasUuid> MutableSet<T>.hasAny(otherEntity: T) =
    filter { it.uuid == otherEntity.uuid }.any()
