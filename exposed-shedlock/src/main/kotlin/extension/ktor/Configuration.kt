package extension.ktor

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.SchemaUtils

/**
 * 1일차 - 이게 Spring 에서 AutoConfiguration 처럼 구현 하는 건데.... 참... 애매~~하다. 어카지 고민해 봐야 겠다...
 *
 * 2일차 -customizing 한 경로는 어떻게 처리 해야할 지 모르겠네 ㅠㅠ
 * spring 에서는 @Configuration 이나 @ComponentScan 이런거로 처리 하지만... ktor 에서는 어떻게 해야할 지 모르겠다......
 *
 */
fun Application.shedlockModule() {
    val driverToString =
        environment.config.propertyOrNull("ktor.datasource.driverClassName")?.getString()?.lowercase()
            ?: environment.config.propertyOrNull("ktor.shedlock.driver")?.getString()?.lowercase()

    val driver = when {
        driverToString == null -> DBMS.ELSE
        driverToString.contains("postgresql") -> DBMS.POSTGRESQL
        driverToString.contains("mysql") -> DBMS.MYSQL
        driverToString.contains("oracle") -> DBMS.ORACLE
        driverToString.contains("mariadb") -> DBMS.MARIADB
        else -> DBMS.ELSE
    }

    DriverType.dbms = driver
    SchemaUtils.create(Shedlocks)
}