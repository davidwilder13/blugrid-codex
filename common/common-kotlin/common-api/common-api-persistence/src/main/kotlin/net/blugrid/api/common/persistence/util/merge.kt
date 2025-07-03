package net.blugrid.api.common.persistence.util

import net.blugrid.api.common.persistence.model.PersistableResource
import net.blugrid.api.common.persistence.model.contract.HasUuid

fun <T : HasUuid, E : PersistableResource<T>> MutableSet<E>.merge(newEntities: MutableSet<E>, updateFn: (existing: E, update: E) -> E) {
    // delete entities
    this.except(newEntities).takeIf { it.isNotEmpty() }
        ?.let { existing ->
            this.removeAll(existing.toSet())
        }

    // insert new entities
    newEntities.except(this).takeIf { it.isNotEmpty() }
        ?.let { this.addAll(it) }

    // update existing
    this.intersect(newEntities).takeIf { it.isNotEmpty() }
        ?.forEach { existing ->
            newEntities.find { it.uuid == existing.uuid }
                ?.let { update -> updateFn(existing, update) }
        }
}
