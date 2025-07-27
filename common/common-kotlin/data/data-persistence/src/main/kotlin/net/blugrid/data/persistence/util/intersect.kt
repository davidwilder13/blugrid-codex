package net.blugrid.data.persistence.util

import net.blugrid.data.persistence.model.contract.HasUuid

fun <T : HasUuid> MutableSet<T>.intersect(otherEntities: MutableSet<T>) =
    if (otherEntities.isEmpty())
        emptySet()
    else
        filter { existing -> otherEntities.hasAny(existing) }
