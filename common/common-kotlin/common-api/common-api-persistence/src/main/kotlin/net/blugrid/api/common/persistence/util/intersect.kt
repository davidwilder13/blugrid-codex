package net.blugrid.api.common.persistence.util

import net.blugrid.api.common.persistence.model.contract.HasUuid

fun <T : HasUuid> MutableSet<T>.intersect(otherEntities: MutableSet<T>) =
    if (otherEntities.isEmpty())
        emptySet()
    else
        filter { existing -> otherEntities.hasAny(existing) }
