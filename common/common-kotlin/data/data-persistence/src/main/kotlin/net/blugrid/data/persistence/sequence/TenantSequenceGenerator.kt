package net.blugrid.data.persistence.sequence

import jakarta.inject.Inject
import jakarta.inject.Singleton
import net.blugrid.platform.logging.logger
import net.blugrid.security.core.context.RequestContext
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import java.io.Serializable

/**
 * Hibernate identifier generator that uses tenant-aware sequences
 * Integrates with existing CurrentRequestContext for tenant resolution
 */
@Singleton
class TenantSequenceGenerator @Inject constructor(
    private val tenantSequence: TenantSequence
) : IdentifierGenerator {

    private val log = logger()

    override fun generate(session: SharedSessionContractImplementor, obj: Any): Serializable {
        val tenantId = getCurrentTenantId()
        val tableName = getTableName(obj)

        log.debug("Generating ID for entity: {}, tenant: {}", obj::class.simpleName, tenantId)

        return tenantSequence.tenantNextVal(session, tableName, tenantId)
    }

    private fun getCurrentTenantId(): Long {
        return RequestContext.currentTenantId
            ?: throw IllegalStateException("No tenant context available for sequence generation")
    }

    private fun getTableName(obj: Any): String {
        // Extract table name from entity annotation or class name
        val entityName = obj::class.simpleName?.lowercase()?.removeSuffix("entity")
            ?: throw IllegalArgumentException("Cannot determine table name for ${obj::class}")

        return entityName
    }
}
