package net.blugrid.api.core.organisation.mapping

import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.core.organisation.model.OrganisationCreate
import net.blugrid.api.core.organisation.model.OrganisationUpdate
import net.blugrid.api.core.organisation.repository.model.OrganisationEntity
import net.blugrid.common.domain.IdentityID
import net.blugrid.common.domain.IdentityUUID
import net.blugrid.data.persistence.mapping.toResourceAudit

fun Organisation.toCreate(): OrganisationCreate =
    OrganisationCreate(
        uuid = this.uuid,
        parentOrganisationId = this.parentOrganisationId,
        effectiveTimestamp = this.effectiveTimestamp
    )

fun Organisation.toUpdate(): OrganisationUpdate =
    OrganisationUpdate(
        id = this.id,
        uuid = this.uuid,
        parentOrganisationId = this.parentOrganisationId,
        effectiveTimestamp = this.effectiveTimestamp
    )

fun OrganisationCreate.toEntity(): OrganisationEntity =
    OrganisationEntity(
        uuid = this.uuid.value,
        parentOrganisationId = this.parentOrganisationId,
        effectiveTimestamp = this.effectiveTimestamp
    )

fun OrganisationUpdate.toEntity(): OrganisationEntity =
    OrganisationEntity(
        uuid = this.uuid.value,
        parentOrganisationId = this.parentOrganisationId,
        effectiveTimestamp = this.effectiveTimestamp
    )

fun OrganisationEntity.toResource(): Organisation =
    Organisation(
        id = IdentityID(this.id!!),
        uuid = IdentityUUID(this.uuid),
        parentOrganisationId = this.parentOrganisationId,
        effectiveTimestamp = this.effectiveTimestamp,
        audit = this.audit.toResourceAudit()
    )
