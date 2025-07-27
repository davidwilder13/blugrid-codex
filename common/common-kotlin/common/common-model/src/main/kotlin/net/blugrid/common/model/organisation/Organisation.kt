package net.blugrid.common.model.organisation

interface Organisation {
    val id: Long
    val displayName: String?
    val partyId: Long?
    val primaryPartyId: Long?
}
