package net.blugrid.security.core.context

import net.blugrid.common.model.organisation.Organisation
import net.blugrid.common.model.useridentity.UserIdentity

data class BusinessUnitSessionContext(
    override val id: Long,
    override val user: UserIdentity,
    override val webApplicationId: Long,
    val organisation: Organisation,
    val operatorId: Long,
    val businessUnitId: Long
) : SessionContext
