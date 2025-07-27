package net.blugrid.platform.serialization

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.blugrid.platform.serialization.config.CustomObjectMapperFactory
import net.blugrid.platform.serialization.exception.JsonException
import java.io.IOException
import java.io.InputStream


val objectMapper: ObjectMapper = CustomObjectMapperFactory.objectMapper

inline fun <reified T : Any> String.fromJson() = objectMapper.readValue<T>(this)

inline fun <reified R : Any> R.toJson() = objectToJson(this)

fun <T> readValue(json: String?, valueType: Class<T>?): T =
    try {
        objectMapper.readValue(json, valueType)
    } catch (e: JsonProcessingException) {
        throw JsonException(e)
    }

fun <T> fromJson(bytes: ByteArray?, typeRef: TypeReference<T>?): T =
    try {
        objectMapper.readValue(bytes, typeRef)
    } catch (e: IOException) {
        throw JsonException(e)
    }

fun <T> fromJson(json: String?, typeRef: TypeReference<T>?): T {
    return try {
        objectMapper.readValue(json, typeRef)
    } catch (e: IOException) {
        throw JsonException(e)
    }
}

fun <T> fromNode(node: JsonNode, typeRef: TypeReference<T>?): T = try {
    objectMapper.readValue(node.toString(), typeRef)
} catch (e: IOException) {
    throw JsonException(e)
}

fun <T> fromObject(obj: Any?, typeRef: TypeReference<T>?): T = try {
    objectMapper.readValue(objectToJson(obj), typeRef)
} catch (e: IOException) {
    throw JsonException(e)
}

fun <T> fromInputStream(`is`: InputStream?, typeRef: TypeReference<T>?): T = try {
    objectMapper.readValue(`is`, typeRef)
} catch (e: IOException) {
    throw JsonException(e)
}

fun objectToJson(obj: Any?): String {
    return try {
        objectMapper.writeValueAsString(obj)
    } catch (e: IOException) {
        throw JsonException(e)
    }
}

fun toPrettyJson(obj: Any?): String = try {
    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
} catch (e: IOException) {
    throw JsonException(e)
}

fun toByteArray(obj: Any?): ByteArray = try {
    objectMapper.writeValueAsBytes(obj)
} catch (e: IOException) {
    throw JsonException(e)
}

fun mapFromJson(bytes: ByteArray?): Map<String, Any> = try {
    objectMapper.readValue(bytes, object : TypeReference<Map<String, Any>>() {})
} catch (e: IOException) {
    throw JsonException(e)
}

fun mapFromJson(json: String?): Map<String, Any> = try {
    objectMapper.readValue(json, object : TypeReference<Map<String, Any>>() {})
} catch (e: IOException) {
    throw JsonException(e)
}

fun <T> deserializeListFromJson(json: String?, type: Class<T>?): List<T> = try {
    objectMapper.readValue(json, objectMapper.typeFactory.constructCollectionType(MutableList::class.java, type))
} catch (e: IOException) {
    throw JsonException(e)
}

fun <T> deserializeFromJson(json: String?, type: Class<T>?): T = try {
    objectMapper.readValue(json, objectMapper.typeFactory.constructType(type))
} catch (e: IOException) {
    throw JsonException(e)
}

fun <T> deserializeFromObject(`object`: Any?, type: Class<T>?): T = try {
    val json = objectToJson(`object`)
    objectMapper.readValue(json, objectMapper.typeFactory.constructType(type))
} catch (e: IOException) {
    throw JsonException(e)
}

fun <T> deserializeFromObjectList(input: List<Any?>?, type: Class<T>?): List<T> {
    val content: MutableList<T> = ArrayList()
    input?.stream()?.forEach { item: Any? -> content.add(deserializeFromObject(item, type)) }
    return content
}

fun nodeFromJson(json: String?): JsonNode = try {
    objectMapper.readTree(json)
} catch (e: IOException) {
    throw JsonException(e)
}

fun nodeFromObject(obj: Any?): JsonNode = try {
    objectMapper.readTree(objectToJson(obj))
} catch (e: IOException) {
    throw JsonException(e)
}
