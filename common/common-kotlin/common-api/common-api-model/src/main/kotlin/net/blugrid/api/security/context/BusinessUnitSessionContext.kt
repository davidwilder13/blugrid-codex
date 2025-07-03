package net.blugrid.api.security.context

import net.blugrid.api.organisation.model.Organisation
import net.blugrid.api.userIdentity.model.UserIdentity

data class BusinessUnitSessionContext(
    override val id: Long,
    override val user: UserIdentity,
    override val webApplicationId: Long,
    val organisation: Organisation,
    val operatorId: Long,
    val businessUnitId: Long
) : SessionContext
