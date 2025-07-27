package net.blugrid.security.core.model

interface BaseAuthenticatedOrganisation {
    val tenantId: String
    val partyId: String?
    val primaryPartyId: String?
    val displayName: String?
}
