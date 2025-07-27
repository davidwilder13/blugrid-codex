package net.blugrid.platform.testing.support

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.TestInstance

@MicronautTest(environments = ["debug-logging", "json", "security", "db"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseIntegTest
