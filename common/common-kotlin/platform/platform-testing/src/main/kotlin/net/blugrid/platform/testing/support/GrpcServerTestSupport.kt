package net.blugrid.platform.testing.support

import net.blugrid.platform.logging.logger
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.PullPolicy
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
                .withEnv("MICRONAUT_ENVIRONMENTS", "data-persistence,platform-debug-logging,platform-serialization,security-core,security-oauth,security-tokens,test")

                // === DATABASE CONFIGURATION ===
                // Primary datasource connection
                .withEnv("DATASOURCE_URL", "jdbc:postgresql://${PostgresTestSupport.internalHost}:${PostgresTestSupport.internalPort}/bizkinetics")
                .withEnv("DATASOURCE_USERNAME", "postgres")
                .withEnv("DATASOURCE_PASSWORD", "password")

                // Legacy Postgres connection variables (if still used)
                .withEnv("POSTGRES_HOST_1", PostgresTestSupport.internalHost)
                .withEnv("POSTGRES_PORT_1", PostgresTestSupport.internalPort.toString())
                .withEnv("POSTGRES_USERNAME_1", "postgres")
                .withEnv("POSTGRES_PASSWORD_1", "password")

                // === SERVICE CONFIGURATION ===
                .withEnv("SERVICE_NAME", "organisation-grpc")
                .withEnv("SERVICE_BASE_URI", "http://${GRPC_NETWORK_ALIAS}:${HTTP_HEALTH_PORT}")

                // === DATABASE SCHEMA ===
                .withEnv("ENV_DB_SCHEMA", "public")

                // === WEB CONFIGURATION ===
                .withEnv("WEB_BASE_URI", "http://localhost:4201")

                // === CONSUL DISCOVERY CONFIGURATION ===
                .withEnv("CONSUL_CLIENT_DEFAULTZONE", "http://${ConsulTestSupport.internalHost}:${ConsulTestSupport.internalPort}")
                .withEnv("CONSUL_CLIENT_REGISTRATION_ENABLED", "true")
                .withEnv("MICRONAUT_CONSUL_CLIENT_REGISTRATION_ENABLED", "true")
                .withEnv("MICRONAUT_CONSUL_CLIENT_DEFAULTZONE", "http://${ConsulTestSupport.internalHost}:${ConsulTestSupport.internalPort}")

                // === MICRONAUT SERVER CONFIGURATION ===
                .withEnv("MICRONAUT_SERVER_PORT", HTTP_HEALTH_PORT.toString())
                .withEnv("MICRONAUT_SERVER_HOST", "0.0.0.0")

                // === GRPC SERVER CONFIGURATION ===
                .withEnv("GRPC_SERVER_PORT", GRPC_INTERNAL_PORT.toString())
                .withEnv("GRPC_SERVER_INSTANCE_ID", "organisation-grpc-test")

                // === HEALTH/ADMIN ENDPOINTS ===
                .withEnv("ENDPOINTS_ALL_ENABLED", "true")
                .withEnv("ENDPOINTS_ALL_SENSITIVE", "false")
                .withEnv("ENDPOINTS_HEALTH_ENABLED", "true")
                .withEnv("ENDPOINTS_HEALTH_SENSITIVE", "false")

                // === LOGGING CONFIGURATION ===
                .withEnv("MICRONAUT_LOGGER_LEVELS_ROOT", "INFO")
                .withEnv("MICRONAUT_LOGGER_LEVELS_NET_BLUGRID", "DEBUG")

                // === CONTAINER CONFIGURATION ===
                .withExposedPorts(GRPC_INTERNAL_PORT, HTTP_HEALTH_PORT)
                .withNetwork(TestNetwork.network)
                .withNetworkAliases(GRPC_NETWORK_ALIAS)
                .dependsOn(PostgresTestSupport.postgresContainer, ConsulTestSupport.consulContainer)
                .waitingFor(
                    Wait.forListeningPorts(GRPC_INTERNAL_PORT, HTTP_HEALTH_PORT)
                        .withStartupTimeout(java.time.Duration.ofMinutes(2))
                )
                .apply {
                    log.info("🔧 Starting gRPC container with PostgreSQL at {}:{}",
                        PostgresTestSupport.internalHost, PostgresTestSupport.internalPort)
                    start()
                    log.info("🚀 gRPC container started successfully")
                    log.info("   - gRPC port: {} (internal), {} (external)", GRPC_INTERNAL_PORT, getMappedPort(GRPC_INTERNAL_PORT))
                    log.info("   - HTTP health port: {} (internal), {} (external)", HTTP_HEALTH_PORT, getMappedPort(HTTP_HEALTH_PORT))
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
