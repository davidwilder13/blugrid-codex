package net.blugrid.api.common.service

import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import net.blugrid.api.common.audit.aspect.LogAuditEvent
import net.blugrid.api.common.exception.NotFoundException
import net.blugrid.api.common.model.audit.AuditEventType.CREATE
import net.blugrid.api.common.model.audit.AuditEventType.UPDATE
import net.blugrid.api.common.model.resource.BaseCreateResource
import net.blugrid.api.common.model.resource.BaseResource
import net.blugrid.api.common.model.resource.BaseUpdateResource
import net.blugrid.api.common.model.resource.GenericEntityMapper
import net.blugrid.api.common.repository.GenericEntityRepository
import net.blugrid.api.common.repository.model.PersistableResource


@Singleton
open class GenericCommandServiceImpl<T : BaseResource<T>, U : BaseCreateResource<U>, V : BaseUpdateResource<V>, X : PersistableResource<X>, Y : GenericEntityMapper<T, U, V, X>>(
    private val repository: GenericEntityRepository<X>,
    private val mapper: GenericEntityMapper<T, U, V, X>,
) : GenericCommandService<T, U, V, X, Y> {

    @Transactional
    @LogAuditEvent(eventType = UPDATE)
    override fun update(id: Long, update: V): T {
        return repository.findById(id)
            .orElseThrow { NotFoundException("Entity with id $id not found") }
            .let {
                repository.update(it.update(update.toEntity()))
                    .toResponse()
            }
    }

    @Transactional
    @LogAuditEvent(eventType = CREATE)
    override fun create(newResource: U): T {
        return repository.saveAndFlush(newResource.toEntity())
            .toResponse()
    }

    @Transactional
    override fun delete(id: Long) {
        repository.findById(id)
            .orElseThrow { NotFoundException("Entity with id $id not found") }
        repository.deleteById(id)
    }

    fun U.toEntity(): X {
        return mapper.createToEntity(this)
    }

    fun V.toEntity(): X {
        return mapper.updateToEntity(this)
    }

    fun X.toResponse(): T {
        return mapper.entityToResource(this)
    }
}
