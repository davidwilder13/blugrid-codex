package net.blugrid.data.persistence.service

import net.blugrid.common.model.pagination.Page
import net.blugrid.common.model.pagination.Pageable
import java.util.Optional
import java.util.UUID

interface GenericQueryService<F, T> {
    fun getPage(pageable: Pageable): Page<T>
    fun getById(id: Long): T
    fun getByIdOptional(id: Long): Optional<T>
    fun getAll(): List<T>
    fun getByUuid(uuid: UUID): T
    fun getByUuidOptional(uuid: UUID): Optional<T>
    fun findByFilter(filter: F, pageable: Pageable): Page<T>
}
