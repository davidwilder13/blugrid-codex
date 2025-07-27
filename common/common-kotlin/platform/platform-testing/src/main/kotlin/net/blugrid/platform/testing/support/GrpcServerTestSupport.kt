package net.blugrid.platform.testing.support

import net.blugrid.platform.logging.logger
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

interface GrpcServerTestSupport {

    companion object {
        private val log = logger()
        private const val GRPC_IMAGE_TAG = "core-organisation-api-grpc:local"
        private const val GRPC_INTERNAL_PORT = 50051
        private const val HTTP_HEALTH_PORT = 8080
        private const val GRPC_NETWORK_ALIAS = "organisation-grpc"

        @JvmStatic
        val grpcContainer: GenericContainer<*> by lazy {
            GenericContainer(DockerImageName.parse(GRPC_IMAGE_TAG))
                .withEnv("MICRONAUT_ENVIRONMENTS", "test,debug-logging,json,security,db")
                // Inject Postgres connection info
                .withEnv("POSTGRES_HOST_1", PostgresTestSupport.internalHost)
                .withEnv("POSTGRES_PORT_1", PostgresTestSupport.internalPort.toString())

                // Inject Consul discovery info
                .withEnv("CONSUL_HOST", ConsulTestSupport.internalHost)
                .withEnv("CONSUL_PORT", ConsulTestSupport.internalPort.toString())
                // Enable Micronaut HTTP server + health endpoint
                .withEnv("MICRONAUT_SERVER_PORT", HTTP_HEALTH_PORT.toString())
//                .withEnv("MICRONAUT_SERVER_HOST", "0.0.0.0")
                .withEnv("ENDPOINTS_ALL_ENABLED", "true")
                .withEnv("ENDPOINTS_ALL_SENSITIVE", "false")
                .withEnv("ENDPOINTS_HEALTH_ENABLED", "true")
                .withEnv("ENDPOINTS_HEALTH_SENSITIVE", "false")
                .withEnv("CONSUL_CLIENT_REGISTRATION_IP_ADDR", DockerHostIpSupport.ip)
                .withEnv("DOCKER_HOST_IP", DockerHostIpSupport.ip)

                .withExposedPorts(GRPC_INTERNAL_PORT, HTTP_HEALTH_PORT)
                .withNetwork(TestNetwork.network)
                .withNetworkAliases(GRPC_NETWORK_ALIAS)
                .waitingFor(
                    Wait
                        .forHttp("/admin/health")
                        .forPort(8080)
                        .forStatusCode(200)
                )
                .apply {
                    start()
                    log.info("🚀 gRPC container started on external port {}", GRPC_INTERNAL_PORT)
                }
        }

        fun ensureStarted() {
            grpcContainer
        }

        val internalHost = GRPC_NETWORK_ALIAS
        val internalPort = GRPC_INTERNAL_PORT

        val externalHost: String
            get() = grpcContainer.host

        val externalPort: Int
            get() = grpcContainer.getMappedPort(GRPC_INTERNAL_PORT)
    }
}
