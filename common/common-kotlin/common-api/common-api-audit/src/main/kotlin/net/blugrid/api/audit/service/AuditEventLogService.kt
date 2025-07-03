package net.blugrid.api.audit.service

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.transaction.annotation.ReadOnly
import jakarta.inject.Singleton
import net.blugrid.api.audit.mapping.toAuditEventLog
import net.blugrid.api.audit.mapping.toAuditEventLogUpdateEntity
import net.blugrid.api.audit.model.AuditEventLogQuery
import net.blugrid.api.audit.repository.AuditEventLogInsertRepository
import net.blugrid.api.audit.repository.AuditEventLogReadRepository
import net.blugrid.api.audit.repository.AuditEventLogReadSpecifications.auditLogEventQueryToSpecification
import net.blugrid.api.audit.repository.AuditEventLogReadSpecifications.hasResourceTypeAndResourceId
import net.blugrid.api.common.model.audit.AuditEvent
import net.blugrid.api.common.model.audit.AuditEventLog
import net.blugrid.api.common.model.resource.ResourceType
import jakarta.transaction.Transactional
import jakarta.transaction.Transactional.TxType.REQUIRES_NEW

interface AuditEventLogService {
    fun createAuditEventLog(auditEventLog: AuditEvent)
    fun findAllByResourceId(resourceType: ResourceType, resourceId: Long): List<AuditEventLog>
    fun searchAuditEventLogs(query: AuditEventLogQuery, pageRequest: Pageable): Page<AuditEventLog?>
    fun isEmpty(resourceType: ResourceType): Boolean
}

@Singleton
open class AuditEventLogServiceImpl(
    private val auditEventLogInsertRepository: AuditEventLogInsertRepository,
    private val auditEventLogReadRepository: AuditEventLogReadRepository,
) : AuditEventLogService {


    @Transactional(value = REQUIRES_NEW)
    override fun createAuditEventLog(auditEventLog: AuditEvent) {
        auditEventLogInsertRepository.save(auditEventLog.toAuditEventLogUpdateEntity())
    }

    @ReadOnly
    override fun findAllByResourceId(resourceType: ResourceType, resourceId: Long): List<AuditEventLog> {
        return auditEventLogReadRepository.findAll(hasResourceTypeAndResourceId(resourceType, resourceId))
            .takeIf { it.isNotEmpty() }
            ?.map { it!!.toAuditEventLog() }
            ?: emptyList()
    }

    @ReadOnly
    override fun searchAuditEventLogs(query: AuditEventLogQuery, pageRequest: Pageable): Page<AuditEventLog?> {
        return auditEventLogReadRepository.findAll(auditLogEventQueryToSpecification(query), pageRequest)
            .map { it!!.toAuditEventLog() }
    }

    @ReadOnly
    override fun isEmpty(resourceType: ResourceType): Boolean {
        return auditEventLogReadRepository.countByResourceType(resourceType) == 0L
    }
}
