package extension.ktor

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.vendors.ForUpdateOption

// 이게 Spring 에서 AutoConfiguration 처럼 구현 하는 건데.... 참... 애매~~하다. 어카지 고민해 봐야 겠다...
fun Application.module() {
    val driverToString =
        environment.config.propertyOrNull("ktor.datasource.driverClassName")?.getString()
            ?: environment.config.propertyOrNull("shedlock.datasource.driverClassName")?.getString()
            ?: throw IllegalArgumentException("Driver class name is not found in the configuration file")

    // TODO : 여기 별도의 class 로 만들어서 인터페이스에 구현체로 만들어야함.
    val driver: Any = when (driverToString) {
        "org.h2.Driver" -> ForUpdateOption.ForUpdate
        "org.postgresql.Driver" -> ForUpdateOption.PostgreSQL
        "com.mysql.cj.jdbc.Driver" -> ForUpdateOption.MySQL
        "oracle.jdbc.OracleDriver" -> ForUpdateOption.Oracle
        "org.mariadb.jdbc.Driver" -> ForUpdateOption.MariaDB
        else -> throw IllegalArgumentException("Unsupported driver class name")
    }
}