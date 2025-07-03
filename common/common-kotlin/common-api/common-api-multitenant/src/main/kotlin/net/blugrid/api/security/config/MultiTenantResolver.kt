package net.blugrid.api.security.config

import jakarta.inject.Named
import jakarta.inject.Singleton
import org.hibernate.context.spi.CurrentTenantIdentifierResolver

@Singleton
@Named("currentTenantResolver")
class MultiTenantResolver : CurrentTenantIdentifierResolver<String> {

    companion object {
        private const val DEFAULT_REGION_ID = "region_1"
    }

    override fun resolveCurrentTenantIdentifier(): String {
        return DEFAULT_REGION_ID
    }

    override fun validateExistingCurrentSessions(): Boolean {
        return true
    }
}
