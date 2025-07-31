package net.blugrid.platform.testing.support

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.kotlin.AbstractCoroutineStub
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerConfiguration
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import net.blugrid.platform.logging.logger
import net.blugrid.platform.testing.grpc.TestGrpcApplicationContext
import net.blugrid.platform.testing.grpc.TestGrpcClientInterceptor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@MicronautTest(
    environments = [
        "data-persistence",
        "platform-serialization",
        "security-core",
        "security-oauth",
        "security-tokens",
    ]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseGrpcIntegTest : PostgresTestSupport, TestPropertyProvider {

    protected val log = logger()

    @Inject
    lateinit var grpcConfig: GrpcServerConfiguration

    @Inject
    lateinit var testGrpcClientInterceptor: TestGrpcClientInterceptor

    protected lateinit var channel: ManagedChannel

    @BeforeAll
    fun setupChannel() {
        PostgresTestSupport.ensureStarted()
        log.info("PostgreSQL Testcontainer started on ${PostgresTestSupport.postgresContainer.host}:${PostgresTestSupport.postgresContainer.getMappedPort(5432)}")

        val port = grpcConfig.serverPort
        log.info("gRPC Embedded server started on port: $port")

        channel = ManagedChannelBuilder
            .forAddress("127.0.0.1", port)
            .intercept(testGrpcClientInterceptor)
            .usePlaintext()
            .build()
    }

    /**
     * Clean up test context after each test
     * Similar to how TestAuthFilter.JWT_TOKEN gets cleared
     */
    @AfterEach
    fun cleanupGrpcContext() {
        TestGrpcApplicationContext.clearContext()
    }

    override fun getProperties(): Map<String, String> = buildMap {
        putAll(PostgresTestSupport.testProperties)
    }

    /**
     * Returns a coroutine-based gRPC stub for the given gRPC stub factory.
     *
     * Usage: `val stub = createStub(::OrganisationServiceCoroutineStub)`
     */
    protected fun <T : AbstractCoroutineStub<T>> createStub(factory: (ManagedChannel) -> T): T {
        return factory(channel)
    }

    /**
     * Runs a coroutine block in the test scope (alias for runBlocking).
     */
    protected fun <T> runGrpcTest(block: suspend CoroutineScope.() -> T): T = runBlocking { block() }

    /**
     * Run a gRPC test with tenant context
     * Usage: runGrpcTestAsTenant { grpc(stub) { ... } }
     */
    protected fun runGrpcTestAsTenant(
        tenantId: Long = 1L,
        sessionId: Long = 1L,
        userIdentityId: Long = 1L,
        webApplicationId: Long = 1L,
        operatorId: Long = 1L,
        block: suspend () -> Unit
    ) = runGrpcTest {
        TestGrpcApplicationContext.configureTenantApplicationContext(
            tenantId = tenantId,
            sessionId = sessionId,
            userIdentityId = userIdentityId,
            webApplicationId = webApplicationId,
            operatorId = operatorId
        )
        block()
    }

    /**
     * Run a gRPC test with business unit context
     */
    protected fun runGrpcTestAsBusinessUnit(
        tenantId: Long = 1L,
        sessionId: Long = 1L,
        userIdentityId: Long = 1L,
        webApplicationId: Long = 1L,
        operatorId: Long = 1L,
        businessUnitId: Long = 1L,
        block: suspend () -> Unit
    ) = runGrpcTest {
        TestGrpcApplicationContext.configureBusinessUnitApplicationContext(
            tenantId = tenantId,
            sessionId = sessionId,
            userIdentityId = userIdentityId,
            webApplicationId = webApplicationId,
            operatorId = operatorId,
            businessUnitId = businessUnitId
        )
        block()
    }

    /**
     * Run a gRPC test with guest context
     */
    protected fun runGrpcTestAsGuest(
        sessionId: Long = 1L,
        userIdentityId: Long = 1L,
        webApplicationId: Long = 1L,
        block: suspend () -> Unit
    ) = runGrpcTest {
        TestGrpcApplicationContext.configureGuestApplicationContext(
            sessionId = sessionId,
            userIdentityId = userIdentityId,
            webApplicationId = webApplicationId
        )
        block()
    }
}
