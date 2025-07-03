package net.blugrid.api.config

import org.hibernate.dialect.PostgreSQLDialect
import java.sql.Types

/*
New Hibernate Type to handle the JDBC Types.OTHER either globally or on a per-query basis.
This is required to work around hibernates incompatibility with json types and union queries.

When unions for inherited type the 1111 JDBC type corresponds to Types.OTHER which is what the PostgreSQL JDBC Driver uses for jsonb column types.
Hibernate will throw the following MappingException in this case.
*/
//class PostgreSQL10JsonDialect : PostgreSQL10Dialect() {
//    init {
//        registerColumnType(Types.OTHER, "jsonb")
//        registerHibernateType(Types.OTHER, "jsonb")
//    }
//}
