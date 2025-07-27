package net.blugrid.platform.logging.config

import jakarta.inject.Singleton

/**
 * Platform-agnostic logging configuration.
 * This class provides configuration options that work across different platforms.
 */
@Singleton
class LoggingConfig {
    
    /**
     * Default log level for the platform
     */
    var defaultLevel: LogLevel = LogLevel.INFO
    
    /**
     * Whether to include timestamps in log output
     */
    var includeTimestamp: Boolean = true
    
    /**
     * Whether to include thread names in log output
     */
    var includeThreadName: Boolean = true
    
    /**
     * Maximum length for log messages before truncation
     */
    var maxMessageLength: Int = 1000
    
    /**
     * Package patterns to exclude from logging
     */
    val excludePatterns: MutableSet<String> = mutableSetOf()
    
    /**
     * Package-specific log levels
     */
    val packageLevels: MutableMap<String, LogLevel> = mutableMapOf()
}

/**
 * Platform-agnostic log levels
 */
enum class LogLevel {
    TRACE, DEBUG, INFO, WARN, ERROR, OFF
}