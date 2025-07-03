package net.blugrid.api.security.context

import net.blugrid.api.userIdentity.model.UserIdentity

data class GuestSessionContext(
    override val id: Long,
    override val user: UserIdentity,
    override val webApplicationId: Long
) : SessionContext
