package net.blugrid.api.test.support

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
