package net.blugrid.server.standalone.inspection

import org.hibernate.resource.jdbc.spi.StatementInspector

/**
 * Simple statement inspector for standalone applications
 */
class SimpleJdbcStatementInspector : StatementInspector {

    override fun inspect(sql: String): String {
        // In standalone mode, no tenant filtering needed
        // Could add logging, performance monitoring, etc.
        return sql
    }
}
