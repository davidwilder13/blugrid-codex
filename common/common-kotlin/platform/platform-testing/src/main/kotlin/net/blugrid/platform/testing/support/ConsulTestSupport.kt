package net.blugrid.platform.testing.support

import net.blugrid.platform.logging.logger
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import java.time.Duration


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
                withCommand("agent", "-dev", "-client=0.0.0.0", "-log-level=INFO")
                withNetwork(TestNetwork.network)
                withNetworkAliases(NETWORK_ALIAS)
                withReuse(true)

                // Add health check wait strategy
                waitingFor(
                    Wait.forHttp("/v1/status/leader")
                        .forPort(INTERNAL_PORT)
                        .forStatusCode(200)
                        .withStartupTimeout(Duration.ofSeconds(60))
                )

                start()
                log.info("🚀 Consul container started successfully")
                log.info("   - Internal: {}:{}", NETWORK_ALIAS, INTERNAL_PORT)
                log.info("   - External: {}:{}", host, firstMappedPort)

                // Verify Consul is ready
                try {
                    Thread.sleep(2000) // Give Consul a moment to fully initialize
                    log.info("✅ Consul is ready for service registration")
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
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

        /**
         * Properties for external clients (outside Docker network)
         */
        val externalTestProperties: Map<String, String>
            get() = mapOf(
                "CONSUL_HOST" to externalHost,
                "CONSUL_PORT" to externalPort.toString(),
                "consul.client.defaultZone" to "http://$externalHost:$externalPort",
                "consul.client.registration.enabled" to "true",
                "micronaut.consul.client.defaultZone" to "http://$externalHost:$externalPort",
                "micronaut.consul.client.registration.enabled" to "true"
            )

        /**
         * Properties for internal clients (inside Docker network)
         */
        val internalTestProperties: Map<String, String> = mapOf(
            "CONSUL_HOST" to internalHost,
            "CONSUL_PORT" to internalPort.toString(),
            "consul.client.defaultZone" to "http://$internalHost:$internalPort",
            "consul.client.registration.enabled" to "true",
            "micronaut.consul.client.defaultZone" to "http://$internalHost:$internalPort",
            "micronaut.consul.client.registration.enabled" to "true"
        )

        /**
         * Legacy compatibility - external properties
         */
        val testProperties: Map<String, String>
            get() = externalTestProperties
    }
}
