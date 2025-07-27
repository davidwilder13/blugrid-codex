package net.blugrid.data.persistence.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.hypersistence.utils.hibernate.type.util.ObjectMapperSupplier
import net.blugrid.platform.serialization.config.CustomObjectMapperFactory


/**
 * Supplies the framework's configured ObjectMapper to Hibernate for JSON type handling.
 * This ensures consistent JSON serialization between REST endpoints and database storage.
 */
class CustomObjectMapperSupplier : ObjectMapperSupplier {
    override fun get(): ObjectMapper = CustomObjectMapperFactory.objectMapper
}
