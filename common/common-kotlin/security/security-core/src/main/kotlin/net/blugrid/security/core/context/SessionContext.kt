package net.blugrid.security.core.context

import net.blugrid.common.model.organisation.Organisation
import net.blugrid.common.model.useridentity.UserIdentity

sealed interface SessionContext {
    val id: Long
    val user: UserIdentity
    val webApplicationId: Long
}

data class GuestSessionContext(
    override val id: Long,
    override val user: UserIdentity,
    override val webApplicationId: Long
) : SessionContext


data class WebApplicationSessionContext(
    override val id: Long,
    override val user: UserIdentity,
    override val webApplicationId: Long,
    val organisation: Organisation,
    val operatorId: Long
) : SessionContext

data class BusinessUnitSessionContext(
    override val id: Long,
    override val user: UserIdentity,
    override val webApplicationId: Long,
    val organisation: Organisation,
    val operatorId: Long,
    val businessUnitId: Long
) : SessionContext
