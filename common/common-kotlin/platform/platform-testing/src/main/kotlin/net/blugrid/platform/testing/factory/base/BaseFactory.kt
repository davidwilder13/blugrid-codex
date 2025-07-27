package net.blugrid.platform.testing.factory.base

/**
 * Base trait for all test factories
 * Provides common utilities and patterns
 */
interface BaseFactory<T> {

    /**
     * Create an instance with all default/generated values
     */
    fun createDefault(): T

    /**
     * DSL builder function
     */
    fun build(block: Builder<T>.() -> Unit = {}): T

    /**
     * Base builder interface
     */
    interface Builder<T> {
        fun build(): T
    }
}
