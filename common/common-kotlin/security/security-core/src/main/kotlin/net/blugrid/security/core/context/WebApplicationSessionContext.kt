package net.blugrid.security.core.context

import net.blugrid.common.model.organisation.Organisation
import net.blugrid.common.model.useridentity.UserIdentity

data class WebApplicationSessionContext(
    override val id: Long,
    override val user: UserIdentity,
    override val webApplicationId: Long,
    val organisation: Organisation,
    val operatorId: Long
) : SessionContext
