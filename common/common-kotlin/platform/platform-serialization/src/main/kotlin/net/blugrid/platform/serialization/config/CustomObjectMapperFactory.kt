package net.blugrid.platform.serialization.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.StreamReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.micronaut.context.annotation.Replaces
import io.micronaut.jackson.JacksonConfiguration
import io.micronaut.jackson.ObjectMapperFactory
import jakarta.inject.Singleton
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Singleton
@Replaces(ObjectMapperFactory::class)
open class CustomObjectMapperFactory : ObjectMapperFactory() {

    companion object {
        lateinit var objectMapper: ObjectMapper

        private const val LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS"
    }

    @Singleton
    @Replaces(ObjectMapper::class)
    override fun objectMapper(jacksonConfiguration: JacksonConfiguration?, jsonFactory: JsonFactory?): ObjectMapper {
        val factory = JsonFactory.builder()
            .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
            .build()

        objectMapper = super.objectMapper(jacksonConfiguration, factory)

        // JsonGenerator
        objectMapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)

        // Serialization
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
        // configure(WRITE_NUMBERS_AS_STRINGS.mappedFeature(), true)
        // NB: Consul registration fail if port number (Integer) is string. Replaced with custom serializer for Longs only.

        // Deserialization
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.registerModule(Jdk8Module())
        objectMapper.registerModule(KotlinModule.Builder().build())
        objectMapper.registerModule(
            SimpleModule()
                .addSerializer(
                    Long::class.java,
                    LongToStringSerializer()
                )
        )
        objectMapper.registerModule(
            JavaTimeModule()
                .addSerializer(LocalDateTimeSerializer(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT))) // .addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT)))
                .addDeserializer(
                    LocalDateTime::class.java,
                    LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT)),
                ),
        )
        return objectMapper
    }
}

class LongToStringSerializer : StdSerializer<Long>(Long::class.java) {
    override fun serialize(value: Long?, generator: JsonGenerator, provider: SerializerProvider) {
        value?.let {
            generator.writeString(it.toString())
        }
            ?: generator.writeNull()
    }
}



