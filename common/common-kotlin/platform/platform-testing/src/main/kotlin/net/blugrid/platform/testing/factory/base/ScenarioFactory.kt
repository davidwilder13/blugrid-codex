package net.blugrid.platform.testing.factory.base

/**
 * Extension for factories that support common scenarios
 */
interface ScenarioFactory<T> : BaseFactory<T> {
    /**
     * Create instances for common test scenarios
     */
    fun createForScenario(scenario: String): T
}
