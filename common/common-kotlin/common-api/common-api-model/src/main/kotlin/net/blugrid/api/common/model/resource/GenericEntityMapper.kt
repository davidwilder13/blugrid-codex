package net.blugrid.api.common.model.resource

import net.blugrid.api.common.repository.model.PersistableResource

abstract class GenericEntityMapper<T : BaseResource<T>, U : BaseCreateResource<U>, V : BaseUpdateResource<V>, X : PersistableResource<X>> {

    abstract fun createToEntity(source: U): X

    abstract fun updateToEntity(source: V): X

    abstract fun entityToResource(source: X): T

    abstract fun resourceToCreate(source: T): U

    abstract fun resourceToUpdate(source: T): V
}
