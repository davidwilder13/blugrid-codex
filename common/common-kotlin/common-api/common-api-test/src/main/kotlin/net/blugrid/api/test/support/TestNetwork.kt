package net.blugrid.api.test.support

import org.testcontainers.containers.Network

object TestNetwork {
    val network: Network = Network.newNetwork()
}

