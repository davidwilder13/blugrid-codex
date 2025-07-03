package net.blugrid.api.security.mapping

import net.blugrid.api.organisation.model.Organisation
import net.blugrid.api.security.model.AuthenticatedOrganisationModel
import net.blugrid.api.security.model.BaseAuthenticatedOrganisation

fun BaseAuthenticatedOrganisation.toOrganisation(): Organisation =
    AuthenticatedOrganisationModel(
        id = tenantId.toLongOrNull() ?: -1L,  // fallback if invalid
        displayName = displayName,
        partyId = partyId?.toLongOrNull(),
        primaryPartyId = primaryPartyId?.toLongOrNull()
    )
