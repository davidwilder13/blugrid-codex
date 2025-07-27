package net.blugrid.common.util

import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

inline fun <reified R : Any> R.toMap() = objectToMap(this)

val emptyLinesRegex = Regex("\n\\s*\n")

fun String.removeEmptyLines(): String = this.replace(emptyLinesRegex, "")

fun String?.toWords(): List<String>? = this
    ?.trim()
    ?.splitToSequence(' ')
    ?.filter { it.isNotBlank() }
    ?.toList()

fun String?.toSearchTerms(): String? = this?.let {
    toWords()
        ?.joinToString(" ") {
            it.filter {
                !it.isWhitespace()
            }
        }
}

fun List<String?>.toSearchTerms(): String =
    filter { !it.isNullOrBlank() }
        .map { it.toSearchTerms() }
        .joinToString(" ")

@Suppress("UNCHECKED_CAST")
fun <T : Any> objectToMap(obj: T): Map<String, Any?> {
    return (obj::class as KClass<T>).memberProperties.associate { prop ->
        prop.name to prop.get(obj)
            ?.let { value ->
                if (value::class.isData) {
                    objectToMap(value)
                } else {
                    value
                }
            }
    }
}

@Suppress("UNCHECKED_CAST")
fun Map<String, Any?>.flatten(): Map<String, Any?> {
    // Flatten a nested JSON file

    // Keep iterating until the termination condition is satisfied
    var currentDictionary = this
    while (true) {
        // Keep unpacking the JSON file until all values are atomic elements (not dictionary or list)
        val flattenedDictionary = currentDictionary
            .filterNotNullValues()
            .flatMap { (key, value) -> unpack(key, value) }
            .toMap()
        // Terminate condition: not any value in the JSON file is dictionary or list
        if (!flattenedDictionary.any { it.value is Map<*, *> } && !flattenedDictionary.any { it.value is List<*> }) {
            return flattenedDictionary
        }
        currentDictionary = flattenedDictionary as Map<String, Any>
    }
}

@Suppress("UNCHECKED_CAST")
fun <K, V> Map<K, V?>.filterNotNullValues(): Map<K, V> =
    filterValues { it != null } as Map<K, V>

fun unpack(parentKey: String, parentValue: Any): Sequence<Pair<String, Any>> {
    // Unpack one level of nesting in the JSON file
    // Unpack one level only!!!

    return when (parentValue) {
        is Map<*, *> -> parentValue.entries.asSequence().flatMap { (key, value) ->
            val temp1 = "$parentKey${if (parentKey.isNotEmpty()) "_" else ""}$key"
            unpack(temp1, value!!)
        }

        is List<*> -> parentValue.asSequence().withIndex().flatMap { (index, value) ->
            val temp2 = "$parentKey${if (parentKey.isNotEmpty()) "_" else ""}$index"
            unpack(temp2, value!!)
        }

        else -> sequenceOf(parentKey to parentValue)
    }
}
