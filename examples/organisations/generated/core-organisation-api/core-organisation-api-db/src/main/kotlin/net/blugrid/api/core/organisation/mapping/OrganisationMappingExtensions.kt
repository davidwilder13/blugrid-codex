package net.blugrid.api.core.organisation.mapping

import net.blugrid.api.common.mapper.toAudit
import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.core.organisation.model.OrganisationCreate
import net.blugrid.api.core.organisation.model.OrganisationUpdate
import net.blugrid.api.core.organisation.repository.model.OrganisationEntity

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
        uuid = this.uuid,
        parentOrganisationId = this.parentOrganisationId,
        effectiveTimestamp = this.effectiveTimestamp
    )

fun OrganisationUpdate.toEntity(): OrganisationEntity =
    OrganisationEntity(
        uuid = this.uuid,
        parentOrganisationId = this.parentOrganisationId,
        effectiveTimestamp = this.effectiveTimestamp
    )

fun OrganisationEntity.toResource(): Organisation =
    Organisation(
        id = this.id!!,
        uuid = this.uuid,
        parentOrganisationId = this.parentOrganisationId,
        effectiveTimestamp = this.effectiveTimestamp,
        audit = this.audit.toAudit()
    )
