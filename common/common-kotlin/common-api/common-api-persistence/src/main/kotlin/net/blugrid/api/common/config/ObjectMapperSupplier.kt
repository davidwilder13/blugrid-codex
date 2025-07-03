package net.blugrid.api.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.hypersistence.utils.hibernate.type.util.ObjectMapperSupplier
import net.blugrid.api.json.config.CustomObjectMapperFactory


class CustomObjectMapperSupplier : ObjectMapperSupplier {
    override fun get(): ObjectMapper = CustomObjectMapperFactory.objectMapper
}
