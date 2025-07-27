package net.blugrid.platform.testing.factory

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.typeOf

// Copied from https://blog.kotlin-academy.com/creating-a-random-instance-of-any-class-in-kotlin-b6168655b64a
inline fun <reified T : Any> randomInstance(
    random: Random = Random,
    config: randomInstanceConfig = randomInstanceConfig()
): T {
    val producer = RandomInstanceProducer(random, config)
    return producer.makeRandomInstance(T::class, typeOf<T>()) as T
}

class NoUsableConstructor : Error()

class randomInstanceConfig(
    var possibleCollectionSizes: IntRange = 1..5,
    var possibleStringSizes: IntRange = 1..10,
    var any: Any = "Anything"
)

class RandomInstanceProducer(
    private val random: Random,
    private val config: randomInstanceConfig
) {

    private fun makeRandomInstanceForParam(paramType: KType, classRef: KClass<*>, type: KType): Any {
        return when (val classifier = paramType.classifier) {
            is KClass<*> -> makeRandomInstance(classifier, paramType)
            is KTypeParameter -> {
                val typeParameterName = classifier.name
                val typeParameterId = classRef.typeParameters.indexOfFirst { it.name == typeParameterName }
                val parameterType = type.arguments[typeParameterId].type ?: typeOf<Any>()
                makeRandomInstance(parameterType.classifier as KClass<*>, parameterType)
            }

            else -> throw Error("Type of the classifier $classifier is not supported")
        }
    }

    fun makeRandomInstance(classRef: KClass<*>, type: KType): Any {
        val primitive = makeStandardInstanceOrNull(classRef, type)
        if (primitive != null) {
            return primitive
        }

        val constructors = classRef.constructors.shuffled(random)

        for (constructor in constructors) {
            try {
                val arguments = constructor.parameters
                    .map { makeRandomInstanceForParam(it.type, classRef, type) }
                    .toTypedArray()

                return constructor.call(*arguments)
            } catch (e: Throwable) {
                e.printStackTrace()
                // no-op. We catch any possible error here that might occur during class creation
            }
        }

        throw NoUsableConstructor()
    }

    @Suppress("IMPLICIT_CAST_TO_ANY", "TYPE_MISMATCH_WARNING")
    private fun makeStandardInstanceOrNull(classRef: KClass<*>, type: KType) = when (classRef) {
        Any::class -> config.any
        Boolean::class -> true
        Char::class -> makeRandomChar(random)
        Double::class -> random.nextDouble()
        Float::class -> random.nextFloat()
        Int::class -> random.nextInt()
        List::class, Collection::class -> makeRandomList(classRef, type)
        LocalDate::class -> LocalDate.now()
        LocalTime::class -> LocalTime.now()
        LocalDateTime::class -> LocalDateTime.now()
        Long::class -> random.nextLong()
        Map::class -> makeRandomMap(classRef, type)
        Set::class -> makeRandomSet(classRef, type)
        Short::class -> random.nextInt().toShort()
        String::class -> makeRandomString(random)
        UUID::class -> UUID.randomUUID()
        else -> if (classRef.isSubclassOf(Enum::class)) {
            makeRandomEnum(classRef, random)
        } else {
            null
        }
    }

    private fun makeRandomEnum(classRef: KClass<*>, random: Random): Any? {
        val values: Array<out Any>? = classRef.java.enumConstants
        return values?.get(random.nextInt(values.size))
    }

    private fun makeRandomList(classRef: KClass<*>, type: KType): List<Any?> {
        val numOfElements = random.nextInt(config.possibleCollectionSizes.start, config.possibleCollectionSizes.endInclusive + 1)
        val elemType = type.arguments[0].type!!
        return (1..numOfElements)
            .map { makeRandomInstanceForParam(elemType, classRef, type) }
    }

    private fun makeRandomSet(classRef: KClass<*>, type: KType): Set<Any?> {
        val numOfElements = random.nextInt(config.possibleCollectionSizes.start, config.possibleCollectionSizes.endInclusive + 1)
        val elemType = type.arguments[0].type!!
        return (1..numOfElements)
            .map { makeRandomInstanceForParam(elemType, classRef, type) }
            .toSet()
    }

    private fun makeRandomMap(classRef: KClass<*>, type: KType): Map<Any?, Any?> {
        val numOfElements = random.nextInt(config.possibleCollectionSizes.start, config.possibleCollectionSizes.endInclusive + 1)
        val keyType = type.arguments[0].type!!
        val valType = type.arguments[1].type!!
        val keys = (1..numOfElements)
            .map { makeRandomInstanceForParam(keyType, classRef, type) }
        val values = (1..numOfElements)
            .map { makeRandomInstanceForParam(valType, classRef, type) }
        return keys.zip(values).toMap()
    }

    private fun makeRandomChar(random: Random) = ('A'..'z').random(random)
    private fun makeRandomString(random: Random) =
        (1..random.nextInt(config.possibleStringSizes.start, config.possibleStringSizes.endInclusive + 1))
            .map { makeRandomChar(random) }
            .joinToString(separator = "") { "$it" }
}

inline fun <reified T : Enum<T>> randomEnum(
    random: Random = Random,
): T {
    val reg = RandomEnumGenerator(T::class.java, random)
    return reg.randomEnum()
}

class RandomEnumGenerator<T : Enum<T>>(e: Class<T>, private val random: Random = Random) {
    private val values: Array<T>

    init {
        values = e.enumConstants
    }

    fun randomEnum(): T {
        return values[random.nextInt(values.size)]
    }
}
