package net.blugrid.data.persistence.sequence

/**
 * Data holder for tenant sequence range information
 * Maps to the result of PostgreSQL function: tenant_sequence_details(tenant_id)
 */
data class TenantSequenceDetails(
    val minId: Long,
    val maxId: Long
) {
    /**
     * Calculate the range size for this tenant
     */
    val rangeSize: Long
        get() = maxId - minId + 1

    /**
     * Check if an ID falls within this tenant's range
     */
    fun contains(id: Long): Boolean = id in minId..maxId

    /**
     * Get the starting ID for new sequences (minId + 1)
     */
    val startingId: Long
        get() = minId + 1
}

// This data comes from your PostgreSQL function in:
// data-persistence/src/main/resources/db/migration/sequence/R__115__tenant_sequence_details.sql

/*
Your SQL function `tenant_sequence_details(tenant_id)` returns:
- min_id: Minimum ID for this tenant's sequence range
- max_id: Maximum ID for this tenant's sequence range

This allows each tenant to have isolated ID ranges, e.g.:
- Tenant 1: IDs 1,000,000 - 1,999,999
- Tenant 2: IDs 2,000,000 - 2,999,999
- etc.
*/
