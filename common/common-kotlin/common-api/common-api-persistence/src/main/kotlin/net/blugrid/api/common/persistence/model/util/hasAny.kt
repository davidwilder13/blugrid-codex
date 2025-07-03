package net.blugrid.api.common.persistence.model.util

import net.blugrid.api.common.persistence.model.contract.HasUuid

fun <T : HasUuid> MutableSet<T>.hasAny(otherEntity: T) =
    filter { it.uuid == otherEntity.uuid }.any()
