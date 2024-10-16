package extension.ktor

import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.vendors.ForUpdateOption

internal fun Query.pessimisticLock(): Query {
    when (DriverType.dbms) {
        DBMS.POSTGRESQL -> this.forUpdate(ForUpdateOption.PostgreSQL.ForUpdate())
        DBMS.ORACLE -> this.forUpdate(ForUpdateOption.Oracle.ForUpdateWait(DEFAULT_TRANSACTION_TIMEOUT))
        DBMS.MYSQL, DBMS.MARIADB, DBMS.ELSE -> this.forUpdate()
    }

    return this
}

private const val DEFAULT_TRANSACTION_TIMEOUT = 30