package net.blugrid.api.security.config

import io.micronaut.configuration.hibernate.jpa.JpaConfiguration
import io.micronaut.context.event.BeanCreatedEvent
import io.micronaut.context.event.BeanCreatedEventListener
import jakarta.inject.Inject
import jakarta.inject.Singleton
//import org.hibernate.MultiTenancyStrategy.DATABASE
import org.hibernate.cfg.AvailableSettings

@Singleton
open class JpaConfigurationCreatedEventListener : BeanCreatedEventListener<JpaConfiguration> {

    @Inject
    var connectionProvider: MultiTenantConnectionProvider? = null

    @Inject
    var tenantResolver: MultiTenantResolver? = null

    override fun onCreated(event: BeanCreatedEvent<JpaConfiguration>): JpaConfiguration {
        val jpaConfiguration = event.bean
        jpaConfiguration.properties[AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER] = connectionProvider
        jpaConfiguration.properties[AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER] = tenantResolver
//        jpaConfiguration.properties[AvailableSettings.MULTI_TENANT] = DATABASE
        jpaConfiguration.properties[AvailableSettings.STATEMENT_INSPECTOR] = CustomJdbcStatementInspector::class.java
        return jpaConfiguration
    }
}
