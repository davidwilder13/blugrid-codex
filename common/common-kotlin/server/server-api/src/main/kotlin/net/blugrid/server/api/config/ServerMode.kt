package net.blugrid.server.api.config

enum class ServerMode {
    /**
     * Multi-tenant mode: Multiple organizations/tenants share the same application instance
     */
    MULTI_TENANT,

    /**
     * Standalone mode: Single organization deployment
     * Perfect for POS, kiosks, local installations
     */
    STANDALONE
}
