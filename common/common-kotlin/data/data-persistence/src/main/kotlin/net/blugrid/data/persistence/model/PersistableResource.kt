package net.blugrid.data.persistence.model

import net.blugrid.data.persistence.model.contract.HasUuid
import java.util.UUID

interface PersistableResource<T> : HasUuid {

    var id: Long?

    override var uuid: UUID

    fun update(update: T): T
}
