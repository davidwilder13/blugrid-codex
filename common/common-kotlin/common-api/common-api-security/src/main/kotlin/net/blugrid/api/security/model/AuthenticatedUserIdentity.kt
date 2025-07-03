package net.blugrid.api.security.model

import net.blugrid.api.userIdentity.model.UserIdentity
import java.util.UUID

data class AuthenticatedUserIdentity(
    override val id: Long,
    override val uuid: UUID,
    override val name: String,
    override val email: String,
    override val displayName: String?,
    override val emailVerified: Boolean?,
    override val providerId: String,
    override val partyId: Long?,
    override val nickName: String?,
    override val givenName: String?,
    override val familyName: String?,
    override val pictureUrl: String?
) : UserIdentity
