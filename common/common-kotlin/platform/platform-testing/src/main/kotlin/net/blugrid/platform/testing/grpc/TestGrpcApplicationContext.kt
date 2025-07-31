package net.blugrid.platform.testing.grpc

/**
 * gRPC Test Context Builder - similar to TestApplicationContext but for gRPC metadata
 *
 * Mental Model: Context blueprint that gets converted to gRPC headers
 */
object TestGrpcApplicationContext {

    // Thread-local storage for test context (similar to how TestAuthFilter.JWT_TOKEN works)
    private val testContext = ThreadLocal<GrpcTestContext?>()

    /**
     * Configure tenant context for gRPC tests
     * This will be automatically injected into gRPC calls via TestGrpcClientInterceptor
     */
    fun configureTenantApplicationContext(
        tenantId: Long = 1L,
        sessionId: Long = 1L,
        userIdentityId: Long = 1L,
        webApplicationId: Long = 1L,
        operatorId: Long = 1L
    ) {
        val context = GrpcTestContext(
            sessionType = "TENANT",
            sessionId = sessionId.toString(),
            userId = userIdentityId.toString(),
            webAppId = webApplicationId.toString(),
            tenantId = tenantId.toString(),
            operatorId = operatorId.toString(),
            businessUnitId = null
        )
        testContext.set(context)
    }

    /**
     * Configure business unit context for gRPC tests
     */
    fun configureBusinessUnitApplicationContext(
        tenantId: Long = 1L,
        sessionId: Long = 1L,
        userIdentityId: Long = 1L,
        webApplicationId: Long = 1L,
        operatorId: Long = 1L,
        businessUnitId: Long = 1L
    ) {
        val context = GrpcTestContext(
            sessionType = "BUSINESS_UNIT",
            sessionId = sessionId.toString(),
            userId = userIdentityId.toString(),
            webAppId = webApplicationId.toString(),
            tenantId = tenantId.toString(),
            operatorId = operatorId.toString(),
            businessUnitId = businessUnitId.toString()
        )
        testContext.set(context)
    }

    /**
     * Configure guest context for gRPC tests
     */
    fun configureGuestApplicationContext(
        sessionId: Long = 1L,
        userIdentityId: Long = 1L,
        webApplicationId: Long = 1L
    ) {
        val context = GrpcTestContext(
            sessionType = "GUEST",
            sessionId = sessionId.toString(),
            userId = userIdentityId.toString(),
            webAppId = webApplicationId.toString(),
            tenantId = null,
            operatorId = null,
            businessUnitId = null
        )
        testContext.set(context)
    }

    /**
     * Get current test context (used by TestGrpcClientInterceptor)
     */
    internal fun getCurrentContext(): GrpcTestContext? = testContext.get()

    /**
     * Clear test context (call this in test cleanup)
     */
    fun clearContext() {
        testContext.remove()
    }
}

/**
 * Simple data holder for gRPC test context
 * Maps directly to the metadata headers your AuthServerInterceptor expects
 */
data class GrpcTestContext(
    val sessionType: String,        // "GUEST", "TENANT", "BUSINESS_UNIT"
    val sessionId: String,
    val userId: String,
    val webAppId: String,
    val tenantId: String? = null,
    val operatorId: String? = null,
    val businessUnitId: String? = null
)
