package net.blugrid.data.persistence.sequence

import org.hibernate.engine.spi.SharedSessionContractImplementor

/**
 * Tenant-aware sequence generation for database identity management
 */
interface TenantSequence {
    /**
     * Generate next value for a tenant-scoped or global sequence
     *
     * @param session Hibernate session for database operations
     * @param inputName Table or sequence name
     * @param tenantId Tenant identifier for scoping
     * @return Next sequence value
     */
    fun tenantNextVal(session: SharedSessionContractImplementor, inputName: String, tenantId: Long): Long
}
