package net.blugrid.api.common.persistence.model.util

import net.blugrid.api.common.persistence.model.contract.HasUuid

fun <T : HasUuid> MutableSet<T>.except(otherEntities: MutableSet<T>) =
    if (otherEntities.isEmpty())
        this
    else
        filter { existing -> !otherEntities.hasAny(existing) }

