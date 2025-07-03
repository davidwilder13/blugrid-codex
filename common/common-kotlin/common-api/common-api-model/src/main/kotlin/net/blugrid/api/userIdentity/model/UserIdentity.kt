package net.blugrid.api.userIdentity.model

interface UserIdentity {
    val id: Long
    val uuid: java.util.UUID
    val name: String
    val email: String
    val displayName: String?
    val emailVerified: Boolean?
    val providerId: String
    val partyId: Long?
    val nickName: String?
    val givenName: String?
    val familyName: String?
    val pictureUrl: String?
}
