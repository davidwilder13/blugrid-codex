package net.blugrid.api.security.model

interface BaseAuthenticatedUser {
    val userIdentityId: String
    val email: String
    val displayName: String?
    val providerId: String

    val emailVerified: Boolean?
    val partyId: String?
    val tenantId: String?

    val nickName: String?
    val givenName: String?
    val familyName: String?
    val pictureUrl: String?
}
