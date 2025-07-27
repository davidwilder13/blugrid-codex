package net.blugrid.security.core.context

import net.blugrid.common.model.useridentity.UserIdentity

sealed interface SessionContext {
    val id: Long
    val user: UserIdentity
    val webApplicationId: Long
}
