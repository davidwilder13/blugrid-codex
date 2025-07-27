package net.blugrid.platform.testing.support

import org.testcontainers.containers.Network

object TestNetwork {
    val network: Network = Network.newNetwork()
}

