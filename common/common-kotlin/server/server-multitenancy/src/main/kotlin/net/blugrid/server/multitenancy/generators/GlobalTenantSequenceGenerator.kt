package net.blugrid.server.multitenancy.generators

import io.micronaut.context.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import net.blugrid.common.domain.exception.TenantContextException
import net.blugrid.data.persistence.sequence.TenantSequence
import net.blugrid.security.core.context.CurrentRequestContext
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import java.io.Serializable

/**
 * Global tenant sequence generator - delegates to data-persistence implementation
 * This is now just a thin wrapper around the actual persistence logic
 */
@Singleton
class GlobalTenantSequenceGenerator : IdentifierGenerator {

    private var tenantSequence: TenantSequence? = null

    @Inject
    constructor(tenantSequence: TenantSequence) {
        this.tenantSequence = tenantSequence
    }

    constructor() {}

    private fun getTenantSequence(): TenantSequence {
        if (tenantSequence == null) {
            // Lazy initialization for Hibernate instantiation
            tenantSequence = ApplicationContext.run().getBean(TenantSequence::class.java)
        }
        return tenantSequence!!
    }

    override fun generate(session: SharedSessionContractImplementor, obj: Any): Serializable {
        val tenantId = CurrentRequestContext.currentTenantId
            ?: throw TenantContextException("No tenant context for sequence generation")

        val tableName = obj::class.simpleName?.lowercase()?.removeSuffix("entity")
            ?: throw IllegalArgumentException("Cannot determine table name")

        return getTenantSequence().tenantNextVal(session, tableName, tenantId)
    }
}
