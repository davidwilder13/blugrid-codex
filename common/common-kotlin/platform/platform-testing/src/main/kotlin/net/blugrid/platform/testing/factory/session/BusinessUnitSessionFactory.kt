package net.blugrid.platform.testing.factory.session

import net.blugrid.platform.testing.factory.base.BaseFactory
import net.blugrid.platform.testing.factory.base.RandomizableFactory
import net.blugrid.platform.testing.factory.base.ScenarioFactory
import net.blugrid.platform.testing.generator.randomAlphanumeric
import net.blugrid.platform.testing.generator.randomId
import net.blugrid.security.core.session.BusinessUnitSession
import java.util.UUID

object BusinessUnitSessionFactory : BaseFactory<BusinessUnitSession>,
    RandomizableFactory<BusinessUnitSession>,
    ScenarioFactory<BusinessUnitSession> {

    override fun createDefault(): BusinessUnitSession = create()

    fun create(
        sessionId: String = "session${Long.randomId()}",
        userId: String = "user${Long.randomId()}",
        tenantId: String = "tenant${Long.randomId()}",
        businessUnitId: String = "bu${Long.randomId()}",
        webApplicationId: String = "100001",
        operatorId: String = "operator${Long.randomId()}"
    ): BusinessUnitSession = BusinessUnitSession(
        sessionId = sessionId,
        userId = userId,
        tenantId = tenantId,
        businessUnitId = businessUnitId,
        webApplicationId = webApplicationId,
        operatorId = operatorId
    )

    fun createForBusinessUnit(businessUnitId: String, tenantId: String) = create(
        businessUnitId = businessUnitId,
        tenantId = tenantId
    )

    fun createForTenant(tenantId: String) = create(
        tenantId = tenantId
    )

    fun createWithOperator(operatorId: String) = create(
        operatorId = operatorId
    )

    fun createWithUser(userId: String) = create(
        userId = userId
    )

    override fun createRandom(): BusinessUnitSession = create(
        sessionId = "session${Long.randomId()}",
        userId = "user${Long.randomId()}",
        tenantId = "tenant${Long.randomId()}",
        businessUnitId = "bu${Long.randomId()}",
        webApplicationId = Long.randomId().toString(),
        operatorId = "operator${Long.randomId()}"
    )

    override fun createForScenario(scenario: String): BusinessUnitSession = when (scenario) {
        "headquarters" -> create(businessUnitId = "bu_hq")
        "branch" -> create(businessUnitId = "bu_branch_${Long.randomId()}")
        "department" -> create(businessUnitId = "bu_dept_${String.randomAlphanumeric(3).uppercase()}")
        "store" -> create(businessUnitId = "bu_store_${Long.randomId()}")
        "multi-unit-1" -> createForBusinessUnit("bu101", "tenant1001")
        "multi-unit-2" -> createForBusinessUnit("bu201", "tenant1001")
        "admin-operator" -> create(operatorId = "admin")
        "manager-operator" -> create(operatorId = "manager${Long.randomId()}")
        "mobile-app" -> create(webApplicationId = "200001")
        else -> createDefault()
    }

    /**
     * Builder DSL for BusinessUnitSession
     */
    class Builder : BaseFactory.Builder<BusinessUnitSession> {
        private var sessionId: String? = null
        private var userId: String? = null
        private var tenantId: String? = null
        private var businessUnitId: String? = null
        private var webApplicationId: String? = null
        private var operatorId: String? = null

        fun sessionId(sessionId: String) = apply { this.sessionId = sessionId }
        fun userId(userId: String) = apply { this.userId = userId }
        fun tenantId(tenantId: String) = apply { this.tenantId = tenantId }
        fun tenantId(tenantId: Long) = apply { this.tenantId = tenantId.toString() }
        fun businessUnitId(businessUnitId: String) = apply { this.businessUnitId = businessUnitId }
        fun businessUnitId(businessUnitId: Long) = apply { this.businessUnitId = businessUnitId.toString() }
        fun webApplicationId(webApplicationId: String) = apply { this.webApplicationId = webApplicationId }
        fun operatorId(operatorId: String) = apply { this.operatorId = operatorId }

        // Convenience methods
        fun randomSessionId() = apply { this.sessionId = "session${Long.randomId()}" }
        fun randomUserId() = apply { this.userId = "user${Long.randomId()}" }
        fun randomTenantId() = apply { this.tenantId = "tenant${Long.randomId()}" }
        fun randomBusinessUnitId() = apply { this.businessUnitId = "bu${Long.randomId()}" }
        fun randomOperatorId() = apply { this.operatorId = "operator${Long.randomId()}" }
        fun randomWebApplicationId() = apply { this.webApplicationId = Long.randomId().toString() }

        // Session patterns
        fun uuidSessionId() = apply { this.sessionId = UUID.randomUUID().toString() }
        fun timestampSessionId() = apply { this.sessionId = "session_${System.currentTimeMillis()}" }
        fun prefixedSessionId(prefix: String) = apply { this.sessionId = "${prefix}_${Long.randomId()}" }

        // Business Unit patterns
        fun headquartersUnit() = apply { this.businessUnitId = "bu_hq" }
        fun branchUnit(branchNumber: Int) = apply { this.businessUnitId = "bu_branch_$branchNumber" }
        fun departmentUnit(deptCode: String) = apply { this.businessUnitId = "bu_dept_${deptCode.uppercase()}" }
        fun storeUnit(storeNumber: Int) = apply { this.businessUnitId = "bu_store_$storeNumber" }
        fun warehouseUnit(warehouseCode: String) = apply { this.businessUnitId = "bu_wh_$warehouseCode" }
        fun regionUnit(regionCode: String) = apply { this.businessUnitId = "bu_region_$regionCode" }
        fun divisionUnit(divisionName: String) = apply { this.businessUnitId = "bu_div_${divisionName.lowercase()}" }

        // Tenant patterns
        fun tenant1() = apply { this.tenantId = "tenant1001" }
        fun tenant2() = apply { this.tenantId = "tenant2001" }
        fun numericTenantId(id: Long) = apply { this.tenantId = "tenant$id" }

        // Operator patterns
        fun adminOperator() = apply { this.operatorId = "admin" }
        fun managerOperator() = apply { this.operatorId = "manager${Long.randomId()}" }
        fun supervisorOperator() = apply { this.operatorId = "supervisor${Long.randomId()}" }
        fun employeeOperator() = apply { this.operatorId = "employee${Long.randomId()}" }
        fun systemOperator() = apply { this.operatorId = "system" }
        fun selfServiceOperator() = apply { this.operatorId = this.userId ?: "user${Long.randomId()}" }

        // Web Application patterns
        fun defaultWebApp() = apply { this.webApplicationId = "100001" }
        fun mobileApp() = apply { this.webApplicationId = "200001" }
        fun adminApp() = apply { this.webApplicationId = "300001" }
        fun posApp() = apply { this.webApplicationId = "500001" }
        fun kioskApp() = apply { this.webApplicationId = "600001" }

        // Scenario builders
        fun headquartersSession() = apply {
            headquartersUnit()
            adminOperator()
            adminApp()
        }

        fun branchManagerSession(branchNumber: Int) = apply {
            branchUnit(branchNumber)
            managerOperator()
            defaultWebApp()
        }

        fun storeEmployeeSession(storeNumber: Int) = apply {
            storeUnit(storeNumber)
            employeeOperator()
            posApp()
        }

        fun warehouseSession(warehouseCode: String) = apply {
            warehouseUnit(warehouseCode)
            this.operatorId = "wh_operator_${Long.randomId()}"
            defaultWebApp()
        }

        fun regionalManagerSession(regionCode: String) = apply {
            regionUnit(regionCode)
            managerOperator()
            adminApp()
        }

        fun mobileFieldSession() = apply {
            prefixedSessionId("mobile")
            mobileApp()
            this.businessUnitId = "bu_field_${Long.randomId()}"
        }

        fun multiUnitSession(tenantId: String, unitNumber: Int) = apply {
            this.tenantId = tenantId
            this.businessUnitId = "bu${unitNumber}"
            this.sessionId = "mu_session_${tenantId}_${unitNumber}_${Long.randomId()}"
        }

        fun hierarchicalSession(tenant: String, division: String, branch: String) = apply {
            this.tenantId = tenant
            this.businessUnitId = "bu_${division}_${branch}"
            this.sessionId = "hier_${tenant}_${division}_${branch}"
        }

        fun testSession() = apply {
            this.sessionId = "test_session_${Long.randomId()}"
            this.userId = "test_user_${Long.randomId()}"
            this.tenantId = "test_tenant_${Long.randomId()}"
            this.businessUnitId = "test_bu_${Long.randomId()}"
            this.operatorId = "test_operator"
            this.webApplicationId = "999999"
        }

        override fun build(): BusinessUnitSession = BusinessUnitSession(
            sessionId = sessionId ?: "session${Long.randomId()}",
            userId = userId ?: "user${Long.randomId()}",
            tenantId = tenantId ?: "tenant${Long.randomId()}",
            businessUnitId = businessUnitId ?: "bu${Long.randomId()}",
            webApplicationId = webApplicationId ?: "100001",
            operatorId = operatorId ?: "operator${Long.randomId()}"
        )
    }

    override fun build(block: BaseFactory.Builder<BusinessUnitSession>.() -> Unit): BusinessUnitSession =
        Builder().apply(block).build()
}
