package net.blugrid.api

import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@MicronautTest
internal class ApplicationTest {
    @Inject
    var application: EmbeddedApplication<*>? = null

    @Test
    fun testItWorks() {
        Assertions.assertTrue(application!!.isRunning)
    }
}
