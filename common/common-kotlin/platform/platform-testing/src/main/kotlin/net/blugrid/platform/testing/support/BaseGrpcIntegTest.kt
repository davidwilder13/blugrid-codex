package net.blugrid.platform.testing.support

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.kotlin.AbstractCoroutineStub
import io.micronaut.grpc.server.GrpcServerConfiguration
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import net.blugrid.platform.logging.logger
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@MicronautTest(
    environments = [
        "data-persistence",
        "platform-debug-logging",
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

    protected lateinit var channel: ManagedChannel

    @BeforeAll
    fun setupChannel() {
        PostgresTestSupport.ensureStarted()
        log.info("PostgreSQL Testcontainer started on ${PostgresTestSupport.postgresContainer.host}:${PostgresTestSupport.postgresContainer.getMappedPort(5432)}")

        val port = grpcConfig.serverPort
        log.info("gRPC Embedded server started on port: $port")

        channel = ManagedChannelBuilder
            .forAddress("127.0.0.1", port)
            .usePlaintext()
            .build()
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
}
