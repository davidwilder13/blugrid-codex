package net.blugrid.platform.testing.support

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import org.junit.jupiter.api.TestInstance

@MicronautTest(
    environments = [
        "data-persistence",
        "platform-debug-logging",
        "platform-serialization",
        "security-core",
        "security-oath",
        "security-tokens",
    ],
    transactional = true
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseServiceIntegTest : PostgresTestSupport, TestPropertyProvider {

    override fun getProperties(): Map<String, String> = buildMap {
        putAll(PostgresTestSupport.testProperties)
    }
}
