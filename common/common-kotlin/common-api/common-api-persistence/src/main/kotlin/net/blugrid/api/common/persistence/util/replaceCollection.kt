package net.blugrid.api.common.persistence.util

import net.blugrid.api.common.persistence.model.PersistableResource
import net.blugrid.api.common.persistence.repository.GenericEntityRepository

fun <E : PersistableResource<*>, R : GenericEntityRepository<E>> replaceCollection(
    existingEntities: Set<E>,
    incomingEntities: Set<E>,
    repository: R,
): Set<E> {
    // delete entities
    (existingEntities - incomingEntities).takeIf {
        it.isNotEmpty()
    }?.let(
        repository::deleteAll
    )

    // insert new entities
    val newEntities = (incomingEntities - existingEntities).takeIf {
        it.isNotEmpty()
    }?.let {
        repository.saveAll(it)
    }?.toSet() ?: emptySet()

    val unmodifiedEntities = existingEntities intersect incomingEntities

    return (unmodifiedEntities + newEntities)
        .takeIf { it.isNotEmpty() }
        ?: emptySet()
}
