package net.blugrid.api.common.repository

import net.blugrid.api.common.repository.model.PersistableResource

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
