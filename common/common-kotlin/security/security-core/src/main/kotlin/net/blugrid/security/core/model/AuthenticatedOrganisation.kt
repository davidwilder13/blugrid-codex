package net.blugrid.security.core.model

data class AuthenticatedOrganisation(
    override val tenantId: String,
    override val partyId: String? = null,
    override val primaryPartyId: String? = null,
    override val displayName: String? = null,
) : BaseAuthenticatedOrganisation
