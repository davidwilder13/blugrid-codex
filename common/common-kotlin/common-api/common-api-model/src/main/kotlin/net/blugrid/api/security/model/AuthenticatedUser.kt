package net.blugrid.api.security.model

data class AuthenticatedUser(
    override val userIdentityId: String,
    override val email: String,
    override val providerId: String,
    override val displayName: String? = null,
    override val emailVerified: Boolean? = true,
    override val partyId: String? = null,
    override val tenantId: String? = null,
    override val nickName: String? = null,
    override val givenName: String? = null,
    override val familyName: String? = null,
    override val pictureUrl: String? = null
) : BaseAuthenticatedUser
