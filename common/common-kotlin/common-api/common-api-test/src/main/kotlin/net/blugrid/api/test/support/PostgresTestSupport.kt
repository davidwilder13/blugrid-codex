package net.blugrid.api.test.support

import kotlinx.coroutines.runBlocking
import net.blugrid.api.logging.logger
import net.blugrid.api.test.support.GrpcServerTestSupport.Companion.grpcContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName


interface PostgresTestSupport {

    companion object {
        const val DATABASE_NAME = "bizkinetics"
        const val USERNAME = "postgres"
        const val PASSWORD = "password"
        const val SCHEMA = "public"
        const val NETWORK_ALIAS = "postgres"
        const val INTERNAL_PORT = 5432
        const val MAX_CONNCETIONS = 1000

        private val log = logger()

        init {
            runBlocking {
                EcrDockerLoginHelper.loginToEcr()
            }
        }

        val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer(
            DockerImageName.parse("453360777153.dkr.ecr.ap-southeast-2.amazonaws.com/blugrid/docker-postgresql:latest")
                .asCompatibleSubstituteFor("postgres")
        )
            .apply {
                withEnv("POSTGRES_USER", USERNAME)
                withEnv("POSTGRES_PASSWORD", PASSWORD)
                withEnv("POSTGRES_MULTIPLE_DATABASES", DATABASE_NAME)
                withUsername(USERNAME)
                withPassword(PASSWORD)
                withCommand("postgres", "-c", "max_connections=$MAX_CONNCETIONS")
                withNetwork(TestNetwork.network)
                withNetworkAliases(NETWORK_ALIAS)
                withReuse(true)
                start()
                log.info("🚀 PostgreSQL container started on external port {}", INTERNAL_PORT)
            }

        val internalHost = NETWORK_ALIAS
        val internalPort = INTERNAL_PORT

        val externalHost: String
            get() = postgresContainer.host

        val externalPort: Int
            get() = postgresContainer.firstMappedPort

        val testProperties: Map<String, String> = mapOf(
            "datasources.default.url" to "jdbc:postgresql://${postgresContainer.host}:${postgresContainer.firstMappedPort}/$DATABASE_NAME",
            "datasources.default.username" to "postgres",
            "datasources.default.password" to "password",
            "env.db.schema" to SCHEMA,
            "POSTGRES_HOST_1" to postgresContainer.host,
            "POSTGRES_PORT_1" to postgresContainer.firstMappedPort.toString(),
            "POSTGRES_DB_1" to DATABASE_NAME,
            "POSTGRES_USERNAME_1" to "postgres",
            "POSTGRES_PASSWORD_1" to "password"
        )

        fun ensureStarted() {
            postgresContainer
        }
    }
}
