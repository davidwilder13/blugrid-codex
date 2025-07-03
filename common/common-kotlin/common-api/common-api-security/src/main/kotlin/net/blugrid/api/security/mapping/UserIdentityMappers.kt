package net.blugrid.api.security.mapping

import net.blugrid.api.security.model.AuthenticatedUserIdentity
import net.blugrid.api.security.model.BaseAuthenticatedUser
import net.blugrid.api.userIdentity.model.UserIdentity
import java.util.UUID

fun BaseAuthenticatedUser.toUserIdentity(): UserIdentity =
    AuthenticatedUserIdentity(
        id = userIdentityId.toLong(),
        uuid = try {
            UUID.fromString(userIdentityId)
        } catch (e: Exception) {
            UUID(0L, 0L)
        },
        name = displayName ?: email,
        email = email,
        displayName = displayName,
        emailVerified = emailVerified,
        providerId = providerId,
        partyId = partyId?.toLongOrNull(),
        nickName = nickName,
        givenName = givenName,
        familyName = familyName,
        pictureUrl = pictureUrl
    )
