package net.blugrid.server.standalone.config

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import javax.sql.DataSource

/**
 * Simple datasource configuration for standalone mode
 */
@Factory
@Requires(property = "app.server.mode", value = "standalone", defaultValue = "standalone")
class StandaloneDataConfiguration {

    @Singleton
    fun dataSource(): DataSource {
        // Simple H2 or SQLite datasource for standalone deployments
        // This would be configured via application.yml
        return createStandaloneDataSource()
    }

    private fun createStandaloneDataSource(): DataSource {
        // Implementation depends on your preferred embedded database
        // Could be H2, SQLite, or even PostgreSQL for standalone
        TODO("Configure based on your standalone database preference")
    }
}
