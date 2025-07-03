package net.blugrid.api.test.support

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import org.junit.jupiter.api.TestInstance

@MicronautTest(
    environments = ["debug-logging", "json", "security", "db"],
    transactional = true
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseServiceIntegTest : PostgresTestSupport, TestPropertyProvider {

    override fun getProperties(): Map<String, String> = buildMap {
        putAll(PostgresTestSupport.testProperties)
    }
}
