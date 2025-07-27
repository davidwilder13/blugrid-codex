package net.blugrid.security.authentication.mapping

import net.blugrid.common.model.organisation.Organisation
import net.blugrid.security.core.model.AuthenticatedOrganisation

fun Organisation.toAuthenticatedOrganisation(): AuthenticatedOrganisation =
    AuthenticatedOrganisation(
        tenantId = id.toString(),
        partyId = partyId?.toString(),
        primaryPartyId = primaryPartyId?.toString(),
        displayName = displayName
    )
