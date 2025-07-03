package net.blugrid.api.common.service

import net.blugrid.api.common.model.resource.BaseCreateResource
import net.blugrid.api.common.model.resource.BaseResource
import net.blugrid.api.common.model.resource.BaseUpdateResource
import net.blugrid.api.common.model.resource.GenericEntityMapper
import net.blugrid.api.common.repository.model.PersistableResource

interface GenericCommandService<T : BaseResource<T>, U : BaseCreateResource<U>, V : BaseUpdateResource<V>, X : PersistableResource<X>, Y : GenericEntityMapper<T, U, V, X>> {
    fun update(id: Long, update: V): T
    fun create(newResource: U): T
    fun delete(id: Long)
}
