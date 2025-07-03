package net.blugrid.api.security.mapping

import net.blugrid.api.organisation.model.Organisation
import net.blugrid.api.security.model.AuthenticatedOrganisation

fun Organisation.toAuthenticatedOrganisation(): AuthenticatedOrganisation =
    AuthenticatedOrganisation(
        tenantId = id.toString(),
        partyId = partyId?.toString(),
        primaryPartyId = primaryPartyId?.toString(),
        displayName = displayName
    )
