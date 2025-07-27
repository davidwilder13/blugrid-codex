package net.blugrid.server.persistence.repositories

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import net.blugrid.api.security.repository.model.RequestScopeEntity

@Repository
interface RequestScopeRepository : JpaRepository<RequestScopeEntity?, Long?> {

    @Query(value = "SELECT set_tenant_scope(CAST(:tenantId AS TEXT))", nativeQuery = true)
    fun setTenantId(tenantId: String): Int

    @Query(value = "SELECT set_business_unit_scope(CAST(:businessUnitId AS TEXT))", nativeQuery = true)
    fun setBusinessUnitId(
        businessUnitId: String,
    ): Int

    @Query(value = "SELECT reset_request_scope()", nativeQuery = true)
    fun resetRequestScope(): Int
}
