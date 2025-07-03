package net.blugrid.api.security.mapping

import net.blugrid.api.security.model.AuthenticatedUser
import net.blugrid.api.userIdentity.model.UserIdentity

fun UserIdentity.toAuthenticatedUser(): AuthenticatedUser =
    AuthenticatedUser(
        userIdentityId = id.toString(),
        displayName = displayName,
        email = email,
        emailVerified = emailVerified,
        partyId = partyId.toString(),
        providerId = providerId,
        nickName = nickName,
        givenName = givenName,
        familyName = familyName,
        pictureUrl = pictureUrl
    )
