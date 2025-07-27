package net.blugrid.server.persistence.config

import io.micronaut.configuration.hibernate.jpa.JpaConfiguration
import io.micronaut.context.event.BeanCreatedEvent
import io.micronaut.context.event.BeanCreatedEventListener
import jakarta.inject.Inject
import jakarta.inject.Singleton
import net.blugrid.platform.logging.logger
import net.blugrid.server.api.config.ServerMode
import net.blugrid.server.api.config.TenantContextHolder
import org.hibernate.cfg.AvailableSettings
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider

/**
 * Micronaut-based JPA configuration that integrates with TenantContextHolder
 */
@Singleton
open class JpaConfigurationCreatedEventListener : BeanCreatedEventListener<JpaConfiguration> {

    private val logger = logger()

    @Inject
    lateinit var tenantContextHolder: TenantContextHolder

    @Inject
    var multiTenantConnectionProvider: MultiTenantConnectionProvider<String>? = null

    override fun onCreated(event: BeanCreatedEvent<JpaConfiguration>): JpaConfiguration {
        val jpaConfiguration = event.bean
        val tenantContext = tenantContextHolder.getCurrentTenant()

        logger.info("Configuring JPA for mode: {}, tenant: {}", tenantContext.mode, tenantContext.tenantId)

        when (tenantContext.mode) {
            ServerMode.MULTI_TENANT -> configureMultiTenant(jpaConfiguration)
            ServerMode.STANDALONE -> configureStandalone(jpaConfiguration)
        }

        return jpaConfiguration
    }

    private fun configureMultiTenant(jpaConfiguration: JpaConfiguration) {
        logger.info("Configuring multi-tenant JPA settings")

        // Fix type inference by being explicit about the type
        val connectionProvider = multiTenantConnectionProvider
        if (connectionProvider != null) {
            jpaConfiguration.properties[AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER] = connectionProvider
            logger.debug("Set multi-tenant connection provider: {}", connectionProvider::class.simpleName)
        } else {
            logger.warn("No MultiTenantConnectionProvider available for multi-tenant mode")
        }

        // Use string class name instead of class reference to avoid type issues
        jpaConfiguration.properties[AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER] =
            "net.blugrid.server.persistence.config.CurrentRequestContextTenantResolver"

        // Uncomment when ready to enable multi-tenancy strategy
        // jpaConfiguration.properties[AvailableSettings.MULTI_TENANT] = MultiTenancyStrategy.SCHEMA

        // Add custom JDBC statement inspector for multi-tenant queries
        jpaConfiguration.properties[AvailableSettings.STATEMENT_INSPECTOR] =
            "net.blugrid.server.multitenancy.inspection.CustomJdbcStatementInspector"

        logger.info("Multi-tenant JPA configuration complete")
    }

    private fun configureStandalone(jpaConfiguration: JpaConfiguration) {
        logger.info("Configuring standalone JPA settings")

        // Standalone configuration - no multi-tenancy settings needed
        // Can still add statement inspector for logging/debugging
        jpaConfiguration.properties[AvailableSettings.STATEMENT_INSPECTOR] =
            "net.blugrid.server.standalone.inspection.SimpleJdbcStatementInspector"

        logger.info("Standalone JPA configuration complete")
    }
}
