package net.blugrid.security.core.context

import net.blugrid.common.model.useridentity.UserIdentity

data class GuestSessionContext(
    override val id: Long,
    override val user: UserIdentity,
    override val webApplicationId: Long
) : SessionContext
