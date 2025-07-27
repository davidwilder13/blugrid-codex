package net.blugrid.platform.testing.factory.base

/**
 * Extension for factories that support random variations
 */
interface RandomizableFactory<T> : BaseFactory<T> {
    /**
     * Create a random variation of the object
     */
    fun createRandom(): T

    /**
     * Create a list of random instances
     */
    fun createRandomList(count: Int): List<T> = (1..count).map { createRandom() }
}
