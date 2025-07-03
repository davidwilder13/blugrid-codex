package net.blugrid.api.test.support

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import net.blugrid.api.test.support.PostgresTestSupport.Companion.DATABASE_NAME
import net.blugrid.api.test.support.PostgresTestSupport.Companion.SCHEMA
import net.blugrid.api.test.support.PostgresTestSupport.Companion.postgresContainer
import org.junit.jupiter.api.TestInstance

@MicronautTest(
    environments = ["debug-logging", "json", "security", "db"],
    startApplication = false,
    transactional = true
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseServiceIntegTest : PostgresTestSupport, TestPropertyProvider {

    override fun getProperties(): Map<String, String> = buildMap {
        putAll(PostgresTestSupport.testProperties)
    }
}
