package net.blugrid.data.persistence.service

import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import net.blugrid.common.domain.exception.NotFoundException
import net.blugrid.common.model.resource.BaseCreateResource
import net.blugrid.common.model.resource.BaseResource
import net.blugrid.common.model.resource.BaseUpdateResource
import net.blugrid.data.persistence.mapping.ResourceEntityMapper
import net.blugrid.data.persistence.model.PersistableResource
import net.blugrid.data.persistence.repository.GenericEntityRepository


@Singleton
open class GenericCommandServiceImpl<T : BaseResource<T>, U : BaseCreateResource<U>, V : BaseUpdateResource<V>, X : PersistableResource<X>, Y : ResourceEntityMapper<T, U, V, X>>(
    private val repository: GenericEntityRepository<X>,
    private val mapper: ResourceEntityMapper<T, U, V, X>,
) : GenericCommandService<T, U, V, X, Y> {

    @Transactional
    override fun update(id: Long, update: V): T {
        return repository.findById(id)
            .orElseThrow { NotFoundException("resource", id) }
            .let {
                repository.update(it.update(update.toEntity()))
                    .toResponse()
            }
    }

    @Transactional
    override fun create(newResource: U): T {
        return repository.saveAndFlush(newResource.toEntity())
            .toResponse()
    }

    @Transactional
    override fun delete(id: Long) {
        repository.findById(id)
            .orElseThrow { NotFoundException("resource", id) }
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
