// package net.blugrid.platform.serialization.config
//
// import net.blugrid.api.common.logging.logger
// import io.jsonwebtoken.io.Deserializer
// import io.micronaut.context.annotation.Primary
// import io.micronaut.core.type.Argument
// import io.micronaut.serde.Decoder
// import io.micronaut.serde.Deserializer
// import io.micronaut.serde.Deserializer.DecoderContext
// import jakarta.inject.Singleton
// import java.time.LocalDateTime
// import java.time.format.DateTimeFormatter
// import java.time.format.DateTimeParseException
//
// @Singleton
// @Primary
// class DbLocalDateTimeDeserializer : Deserializer<LocalDateTime> {
//
//    private val log = logger()
//
//    companion object {
//        private val FORMATTERS = listOf(
//            DateTimeFormatter.ISO_DATE_TIME,
//            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSxxx"),
//        )
//    }
//
//    override fun deserialize(decoder: Decoder, context: DecoderContext, type: Argument<in LocalDateTime>): LocalDateTime? {
//        val str = decoder.decodeString()
//        return FORMATTERS.firstNotNullOf { it.tryDeserialize(str) }
//    }
//
//    private fun DateTimeFormatter.tryDeserialize(date: String): LocalDateTime? {
//        return try {
//            LocalDateTime.parse(date, this)
//        } catch (e: DateTimeParseException) {
//            log.warn("Cannot deserialize date ($date) with Format (${this}): ${e.cause}")
//            null
//        }
//    }
// }
