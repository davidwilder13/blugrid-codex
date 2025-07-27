package net.blugrid.data.persistence.service

import io.micronaut.data.jpa.repository.criteria.Specification
import io.micronaut.transaction.annotation.ReadOnly
import net.blugrid.common.model.exception.NotFoundException
import net.blugrid.common.model.pagination.Page
import net.blugrid.common.model.pagination.Pageable
import net.blugrid.common.model.resource.BaseResource
import net.blugrid.data.persistence.mapping.toFrameworkAgnosticPage
import net.blugrid.data.persistence.mapping.toMicronautPageable
import net.blugrid.data.persistence.model.PersistableResource
import net.blugrid.data.persistence.repository.GenericEntityRepository
import java.util.Optional
import java.util.UUID

open class GenericQueryServiceImpl<
        F,
        T : BaseResource<T>,
        E : PersistableResource<E>
        >(
    private val repository: GenericEntityRepository<E>,
    private val mapper: (E) -> T,
    private val specBuilder: (F) -> Specification<E>
) : GenericQueryService<F, T> {

    @ReadOnly
    override fun getPage(pageable: Pageable): Page<T> {
        val micronautPageable = pageable.toMicronautPageable()
        val micronautPage = repository.findAll(micronautPageable)

        return micronautPage.toFrameworkAgnosticPage {
            it.toResponse()
        }
    }

    @ReadOnly
    override fun getAll(): List<T> {
        return repository.findAll()
            .map { it.toResponse() }
    }

    @ReadOnly
    override fun getById(id: Long): T {
        return repository.findById(id)
            .orElseThrow { NotFoundException("Entity with id $id not found") }
            .toResponse()
    }

    @ReadOnly
    override fun getByIdOptional(id: Long): Optional<T> {
        return repository.findById(id)
            .map { it.toResponse() }
    }

    @ReadOnly
    override fun getByUuid(uuid: UUID): T {
        return repository.findByUuid(uuid)
            .orElseThrow()
            .toResponse()
    }

    @ReadOnly
    override fun getByUuidOptional(uuid: UUID): Optional<T> {
        return repository.findByUuid(uuid)
            .map { it.toResponse() }
    }

    @ReadOnly
    override fun findByFilter(filter: F, pageable: Pageable): Page<T> {
        val spec = specBuilder(filter)
        val micronautPageable = pageable.toMicronautPageable()
        val micronautPage = repository.findAll(spec, micronautPageable)
        return micronautPage.toFrameworkAgnosticPage(mapper)
    }

    fun E.toResponse(): T {
        return mapper(this)
    }
}
