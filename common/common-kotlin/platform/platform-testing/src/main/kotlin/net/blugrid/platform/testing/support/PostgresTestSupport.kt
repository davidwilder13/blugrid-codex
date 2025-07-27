package net.blugrid.platform.testing.support

import net.blugrid.platform.logging.logger
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
        const val MAX_CONNECTIONS = 1000

        private val log = logger()

//        TODO: Replace with ECR image when available
//        For now, using public PostgreSQL image instead of Blugrid ECR
//        init {
//            runBlocking {
//                EcrDockerLoginHelper.loginToEcr()
//            }
//        }

        // TODO: Replace with Bizkinetics ECR image when available
        // Currently using public PostgreSQL image instead of Blugrid ECR
        val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer(
            DockerImageName.parse("postgres:17.2-alpine")
        )
            .apply {
                // Use standard PostgreSQL environment variables
                withEnv("POSTGRES_USER", USERNAME)
                withEnv("POSTGRES_PASSWORD", PASSWORD)
                withEnv("POSTGRES_DB", DATABASE_NAME)
                withUsername(USERNAME)
                withPassword(PASSWORD)
                withDatabaseName(DATABASE_NAME)
                withCommand("postgres", "-c", "max_connections=$MAX_CONNECTIONS")
                withNetwork(TestNetwork.network)
                withNetworkAliases(NETWORK_ALIAS)
                withReuse(true)
                start()
                log.info("🚀 PostgreSQL container started on external port {}", firstMappedPort)
                log.info("📊 Database '{}' created successfully", DATABASE_NAME)
            }

        val internalHost = NETWORK_ALIAS
        val internalPort = INTERNAL_PORT

        val externalHost: String
            get() = postgresContainer.host

        val externalPort: Int
            get() = postgresContainer.firstMappedPort

        val testProperties: Map<String, String> = mapOf(
            // Standard datasource configuration
            "datasources.default.url" to "jdbc:postgresql://${postgresContainer.host}:${postgresContainer.firstMappedPort}/$DATABASE_NAME",
            "datasources.default.username" to USERNAME,
            "datasources.default.password" to PASSWORD,
            "datasources.default.schema" to SCHEMA,

            // Db props
            "env.db.schema" to SCHEMA,
            "env.db.dbname" to DATABASE_NAME,

            // Legacy properties (keep for compatibility)
            "POSTGRES_HOST_1" to postgresContainer.host,
            "POSTGRES_PORT_1" to postgresContainer.firstMappedPort.toString(),
            "POSTGRES_DB_1" to DATABASE_NAME,
            "POSTGRES_USERNAME_1" to USERNAME,
            "POSTGRES_PASSWORD_1" to PASSWORD,

            // Hibernate properties to ensure correct schema usage
            "jpa.default.properties.hibernate.default_schema" to SCHEMA,
            "hibernate.default_schema" to SCHEMA
        )

        fun ensureStarted() {
            postgresContainer
        }
    }
}
