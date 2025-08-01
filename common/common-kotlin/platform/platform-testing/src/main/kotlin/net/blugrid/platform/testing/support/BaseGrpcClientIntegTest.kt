package net.blugrid.platform.testing.support

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@MicronautTest(
    environments = [
        "platform-debug-logging",
        "platform-serialization",
        "security-core",
        "security-oauth",
        "security-tokens",
    ]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseGrpcClientIntegTest :
    BaseIntegTest(),
    ConsulTestSupport,
    PostgresTestSupport,
    GrpcClientTestSupport,
    GrpcServerTestSupport,
    TestPropertyProvider {

    @BeforeAll
    fun ensureGrpcContainerRunning() {
        ConsulTestSupport.ensureStarted()
        GrpcServerTestSupport.ensureStarted()
        log.info("🔍 gRPC container is running: ${GrpcServerTestSupport.grpcContainer.isRunning}")
    }

    override fun getProperties(): Map<String, String> = buildMap {
        putAll(PostgresTestSupport.testProperties)
        putAll(ConsulTestSupport.testProperties)
    }
}

