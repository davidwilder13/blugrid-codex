package net.blugrid.security.authentication.mapping

import net.blugrid.common.model.useridentity.UserIdentity
import net.blugrid.security.core.model.AuthenticatedUser

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
