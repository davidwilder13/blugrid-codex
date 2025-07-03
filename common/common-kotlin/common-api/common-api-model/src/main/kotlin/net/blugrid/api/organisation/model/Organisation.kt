package net.blugrid.api.organisation.model

interface Organisation {
    val id: Long
    val displayName: String?
    val partyId: Long?
    val primaryPartyId: Long?
}
