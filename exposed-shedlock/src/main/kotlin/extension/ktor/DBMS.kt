package extension.ktor

internal object DriverType {
    var dbms: DBMS = DBMS.ELSE
}

internal enum class DBMS {
    POSTGRESQL,
    MYSQL,
    ORACLE,
    MARIADB,
    ELSE,
}