package net.blugrid.data.persistence.repository

import io.micronaut.data.jpa.repository.JpaRepository
import io.micronaut.data.jpa.repository.JpaSpecificationExecutor
import net.blugrid.data.persistence.model.PersistableResource
import java.util.Optional
import java.util.UUID

interface GenericEntityRepository<T : PersistableResource<T>> : JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    fun update(update: T): T

    fun findByUuid(uuid: UUID): Optional<T>
}
