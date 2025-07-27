package net.blugrid.data.persistence.util

import net.blugrid.data.persistence.model.contract.HasUuid

fun <T : HasUuid> MutableSet<T>.except(otherEntities: MutableSet<T>) =
    if (otherEntities.isEmpty())
        this
    else
        filter { existing -> !otherEntities.hasAny(existing) }

