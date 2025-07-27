package net.blugrid.platform.logging

import net.blugrid.platform.logging.api.PlatformLogger
import net.blugrid.platform.logging.api.Slf4jLogger
import org.slf4j.LoggerFactory
import kotlin.reflect.full.companionObject

/**
 * Extension function to create a logger for any class, preserving compatibility with existing code.
 * This function provides a platform-agnostic way to get loggers.
 */
inline fun <reified R : Any> R.logger(): PlatformLogger = logger(R::class.java)

/**
 * Create a logger for a specific Java class
 */
fun logger(clazz: Class<*>): PlatformLogger {
    val slf4jLogger = LoggerFactory.getLogger(unwrapCompanionClass(clazz))
    return Slf4jLogger(slf4jLogger)
}

/**
 * Unwrap companion class to enclosing class given a Java Class.
 * This ensures that companion objects get the logger of their enclosing class.
 */
private fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
    return if (ofClass.enclosingClass != null && ofClass.enclosingClass.kotlin.companionObject?.java == ofClass) {
        ofClass.enclosingClass
    } else {
        ofClass
    }
}