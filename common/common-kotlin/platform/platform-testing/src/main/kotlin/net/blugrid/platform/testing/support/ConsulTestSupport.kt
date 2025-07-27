package net.blugrid.platform.testing.support

import net.blugrid.platform.logging.logger
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName


interface ConsulTestSupport {

    companion object {
        const val NETWORK_ALIAS = "consul"
        const val INTERNAL_PORT = 8500
        private val log = logger()

        @JvmStatic
        val consulContainer: GenericContainer<*> by lazy {
            GenericContainer(
                DockerImageName.parse("hashicorp/consul:1.15")
            ).apply {
                withExposedPorts(INTERNAL_PORT)
                withCommand("agent", "-dev", "-client=0.0.0.0")
                withNetwork(TestNetwork.network)
                withNetworkAliases(NETWORK_ALIAS)
                withReuse(true)
                start()
                log.info("🚀 Consul container started on external port {}", firstMappedPort)
            }
        }

        fun ensureStarted() {
            consulContainer
        }

        val internalHost: String = NETWORK_ALIAS
        val internalPort: Int = INTERNAL_PORT

        val externalHost: String
            get() = consulContainer.host

        val externalPort: Int
            get() = consulContainer.getMappedPort(INTERNAL_PORT)

        val testProperties: Map<String, String> = mapOf(
            "CONSUL_HOST" to consulContainer.host,
            "CONSUL_PORT" to consulContainer.getMappedPort(8500).toString(),
            "consul.client.defaultZone" to "http://${consulContainer.host}:${consulContainer.getMappedPort(8500)}",
            "consul.client.registration.enabled" to "true",
        )
    }
}
