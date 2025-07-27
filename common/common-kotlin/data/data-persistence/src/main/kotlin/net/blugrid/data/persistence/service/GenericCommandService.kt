package net.blugrid.data.persistence.service

import net.blugrid.common.model.resource.BaseCreateResource
import net.blugrid.common.model.resource.BaseResource
import net.blugrid.common.model.resource.BaseUpdateResource
import net.blugrid.data.persistence.mapping.ResourceEntityMapper
import net.blugrid.data.persistence.model.PersistableResource

interface GenericCommandService<T : BaseResource<T>, U : BaseCreateResource<U>, V : BaseUpdateResource<V>, X : PersistableResource<X>, Y : ResourceEntityMapper<T, U, V, X>> {
    fun update(id: Long, update: V): T
    fun create(newResource: U): T
    fun delete(id: Long)
}
