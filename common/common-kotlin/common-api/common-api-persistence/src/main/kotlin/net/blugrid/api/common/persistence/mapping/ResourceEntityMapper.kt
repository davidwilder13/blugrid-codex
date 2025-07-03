package net.blugrid.api.common.persistence.mapping

import net.blugrid.api.common.model.resource.BaseCreateResource
import net.blugrid.api.common.model.resource.BaseResource
import net.blugrid.api.common.model.resource.BaseUpdateResource
import net.blugrid.api.common.persistence.model.PersistableResource

abstract class ResourceEntityMapper<T : BaseResource<T>, U : BaseCreateResource<U>, V : BaseUpdateResource<V>, X : PersistableResource<X>> {

    abstract fun createToEntity(source: U): X

    abstract fun updateToEntity(source: V): X

    abstract fun entityToResource(source: X): T

    abstract fun resourceToCreate(source: T): U

    abstract fun resourceToUpdate(source: T): V
}
