package net.blugrid.api.audit.repository

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import io.micronaut.data.jpa.repository.JpaSpecificationExecutor
import net.blugrid.api.audit.repository.model.AuditEventLogInsertEntity
import java.util.UUID

@Repository
interface AuditEventLogInsertRepository : JpaRepository<AuditEventLogInsertEntity, UUID>, JpaSpecificationExecutor<AuditEventLogInsertEntity?>

