package net.blugrid.api.security.context

import net.blugrid.api.userIdentity.model.UserIdentity

sealed interface SessionContext {
    val id: Long
    val user: UserIdentity
    val webApplicationId: Long
}
