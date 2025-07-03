package net.blugrid.api.security.model

interface BaseAuthenticatedOrganisation {
    val tenantId: String
    val partyId: String?
    val primaryPartyId: String?
    val displayName: String?
}
