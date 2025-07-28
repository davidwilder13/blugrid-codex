package net.blugrid.security.tokens.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import jakarta.inject.Named
import jakarta.inject.Singleton

@Factory
class JwtObjectMapperConfig {

    @Bean
    @Singleton
    @Named("jwtObjectMapper")
    fun jwtObjectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            // Register Kotlin module for data class support
            registerModule(KotlinModule.Builder().build())

            // Use snake_case for JWT token properties
            propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE

            // Configure for JWT token parsing
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)

            // Handle null values gracefully
            configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        }
    }
}
