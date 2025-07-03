package net.blugrid.api.security.model

import net.blugrid.api.organisation.model.Organisation

data class AuthenticatedOrganisationModel(
    override val id: Long,
    override val displayName: String?,
    override val partyId: Long?,
    override val primaryPartyId: Long?
) : Organisation
