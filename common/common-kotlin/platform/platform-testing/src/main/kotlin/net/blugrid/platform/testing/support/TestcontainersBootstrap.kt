package net.blugrid.platform.testing.support

import kotlinx.coroutines.runBlocking

object TestcontainersBootstrap {
    private var bootstrapped = false

    fun ensureStarted() {
        if (bootstrapped) return
        bootstrapped = true

        runBlocking {
            EcrDockerLoginHelper.loginToEcr()
        }
    }
}
