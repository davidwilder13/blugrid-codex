package net.blugrid.api.security.config

import jakarta.inject.Singleton
import net.blugrid.api.security.context.IsUnscoped
import org.hibernate.resource.jdbc.spi.StatementInspector

@Singleton
open class CustomJdbcStatementInspector : StatementInspector {
    override fun inspect(sql: String): String {
        val modifier = if (isUnscoped) "_unscoped" else ""
        return sql.replace("_##scope_option##", modifier)
    }

    private val isUnscoped: Boolean
        get() = when {
            IsUnscoped.isSet() -> IsUnscoped.value
            else -> false
        }
}
