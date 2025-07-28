package net.blugrid.platform.serialization.config

import jakarta.inject.Singleton

/**
 * Configuration for platform serialization behavior.
 * This provides platform-agnostic configuration for JSON serialization/deserialization.
 */
@Singleton
class SerializationConfig {

    /**
     * Default property naming strategy
     */
    var defaultNamingStrategy: NamingStrategy = NamingStrategy.SNAKE_CASE

    /**
     * Whether to include null values in serialized output
     */
    var includeNulls: Boolean = false

    /**
     * Whether to fail on unknown properties during deserialization
     */
    var failOnUnknownProperties: Boolean = false

    /**
     * Whether to write dates as timestamps or ISO strings
     */
    var writeDatesAsTimestamps: Boolean = false

    /**
     * Default date format pattern
     */
    var dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    /**
     * Custom deserializer configurations
     */
    val customDeserializers: MutableMap<Class<*>, String> = mutableMapOf()

    /**
     * Custom serializer configurations
     */
    val customSerializers: MutableMap<Class<*>, String> = mutableMapOf()
}

/**
 * Supported naming strategies for JSON properties
 */
enum class NamingStrategy {
    SNAKE_CASE,      // property_name
    CAMEL_CASE,      // propertyName
    KEBAB_CASE,      // property-name
    PASCAL_CASE,     // PropertyName
    UPPER_CAMEL_CASE // PropertyName
}
