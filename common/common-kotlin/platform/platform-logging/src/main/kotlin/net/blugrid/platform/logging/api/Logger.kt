package net.blugrid.platform.logging.api

import org.slf4j.Logger

/**
 * Platform-agnostic logger interface that can be implemented across different platforms.
 * This interface provides the core logging functionality needed across all platforms.
 */
interface PlatformLogger {
    fun trace(message: String)
    fun trace(message: String, vararg args: Any?)
    fun trace(message: String, throwable: Throwable)

    fun debug(message: String)
    fun debug(message: String, vararg args: Any?)
    fun debug(message: String, throwable: Throwable)

    fun info(message: String)
    fun info(message: String, vararg args: Any?)
    fun info(message: String, throwable: Throwable)

    fun warn(message: String)
    fun warn(message: String, vararg args: Any?)
    fun warn(message: String, throwable: Throwable)

    fun error(message: String)
    fun error(message: String, vararg args: Any?)
    fun error(message: String, throwable: Throwable)

    fun isTraceEnabled(): Boolean
    fun isDebugEnabled(): Boolean
    fun isInfoEnabled(): Boolean
    fun isWarnEnabled(): Boolean
    fun isErrorEnabled(): Boolean
}

/**
 * SLF4J implementation of PlatformLogger for JVM platforms
 */
class Slf4jLogger(private val slf4jLogger: Logger) : PlatformLogger {
    override fun trace(message: String) = slf4jLogger.trace(message)
    override fun trace(message: String, vararg args: Any?) = slf4jLogger.trace(message, *args)
    override fun trace(message: String, throwable: Throwable) = slf4jLogger.trace(message, throwable)

    override fun debug(message: String) = slf4jLogger.debug(message)
    override fun debug(message: String, vararg args: Any?) = slf4jLogger.debug(message, *args)
    override fun debug(message: String, throwable: Throwable) = slf4jLogger.debug(message, throwable)

    override fun info(message: String) = slf4jLogger.info(message)
    override fun info(message: String, vararg args: Any?) = slf4jLogger.info(message, *args)
    override fun info(message: String, throwable: Throwable) = slf4jLogger.info(message, throwable)

    override fun warn(message: String) = slf4jLogger.warn(message)
    override fun warn(message: String, vararg args: Any?) = slf4jLogger.warn(message, *args)
    override fun warn(message: String, throwable: Throwable) = slf4jLogger.warn(message, throwable)

    override fun error(message: String) = slf4jLogger.error(message)
    override fun error(message: String, vararg args: Any?) = slf4jLogger.error(message, *args)
    override fun error(message: String, throwable: Throwable) = slf4jLogger.error(message, throwable)

    override fun isTraceEnabled(): Boolean = slf4jLogger.isTraceEnabled
    override fun isDebugEnabled(): Boolean = slf4jLogger.isDebugEnabled
    override fun isInfoEnabled(): Boolean = slf4jLogger.isInfoEnabled
    override fun isWarnEnabled(): Boolean = slf4jLogger.isWarnEnabled
    override fun isErrorEnabled(): Boolean = slf4jLogger.isErrorEnabled
}
