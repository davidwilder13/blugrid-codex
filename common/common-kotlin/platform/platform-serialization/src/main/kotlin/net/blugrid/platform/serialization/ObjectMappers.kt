package net.blugrid.platform.serialization

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

/**
 * Platform-wide object mapper with standard configuration.
 * Renamed from jwtObjectMapper for broader platform usage.
 */
val platformObjectMapper: ObjectMapper = ObjectMapper().apply {
    propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    registerModule(KotlinModule.Builder().build())
    registerModule(JavaTimeModule())
}

/**
 * Alternative object mapper for strict JSON parsing (fails on unknown properties)
 */
val strictObjectMapper: ObjectMapper = ObjectMapper().apply {
    propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
    enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    registerModule(KotlinModule.Builder().build())
    registerModule(JavaTimeModule())
}

/**
 * Camel case object mapper for APIs that require camelCase naming
 */
val camelCaseObjectMapper: ObjectMapper = ObjectMapper().apply {
    propertyNamingStrategy = PropertyNamingStrategies.LOWER_CAMEL_CASE
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    registerModule(KotlinModule.Builder().build())
    registerModule(JavaTimeModule())
}