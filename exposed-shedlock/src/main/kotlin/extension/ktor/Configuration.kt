package extension.ktor

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.yaml.snakeyaml.Yaml
import java.io.File

/**TODO:
 *  1일차 - 이게 Spring 에서 AutoConfiguration 처럼 구현 하는 건데.... 참... 애매~~하다. 어카지 고민해 봐야 겠다...
 *  2일차 -customizing 한 경로는 어떻게 처리 해야할 지 모르겠네 ㅠㅠ
 *  spring 에서는 @Configuration 이나 @ComponentScan 이런거로 처리 하지만... ktor 에서는 어떻게 해야할 지 모르겠다......
 */
fun Application.shedlockModule() {
    val config = ConfigFactory.load()
    val isYaml = try {
        config.getString("ktor")
        false
    } catch (e: Exception) {
        true
    }

    val configuration =
        if (isYaml) {
            val yamlFile = File("src/main/resources/application.yml")
            val yaml = Yaml()
            val data = yaml.load<Map<String, Any>>(yamlFile.inputStream())
            ConfigFactory.parseMap(data)
        } else {
            config
        }

    val url = configuration.entrySet().find { it.key == "ktor.database.url" }?.value?.unwrapped() as? String
        ?: configuration.entrySet().find { it.key == "ktor.datasource.url" }?.value?.unwrapped() as String

    val user = configuration.entrySet().find { it.key.contains("ktor.database.user") }?.value?.unwrapped() as? String
        ?: configuration.entrySet().find { it.key.contains("ktor.datasource.user") }?.value?.unwrapped() as String

    val password = configuration.entrySet().find { it.key == "ktor.database.password" }?.value?.unwrapped() as? String
        ?: configuration.entrySet().find { it.key == "ktor.datasource.password" }?.value?.unwrapped() as String

    val driver =
        configuration.entrySet().find { it.key.contains("ktor.database.driver") }?.value?.unwrapped() as? String
            ?: configuration.entrySet().find { it.key.contains("ktor.datasource.driver") }?.value?.unwrapped() as String

    val dataSource = HikariDataSource(
        HikariConfig().also {
            it.jdbcUrl = url
            it.username = user
            it.password = password
            it.validate()
        }
    )

    Database.connect(dataSource)

    val dbms = when {
        driver.contains("postgresql") -> DBMS.POSTGRESQL
        driver.contains("mysql") -> DBMS.MYSQL
        driver.contains("oracle") -> DBMS.ORACLE
        driver.contains("mariadb") -> DBMS.MARIADB
        else -> DBMS.ELSE
    }

    DriverType.dbms = dbms
    transaction {
        SchemaUtils.create(Shedlocks)
    }
}

/**
 *         val yaml = File("src/main/resources/application.yml").readLines()
 *         url = yaml.find { it.contains("url: ") }?.replace("url: ", "")!!.trimIndent()
 *         user = yaml.find { it.contains("username") }?.replace("username: ", "")!!.trimIndent()
 *         password = yaml.find { it.contains("password") }?.replace("password: ", "")!!.trimIndent()
 *
 *         위 부분 src/main/resources/*.yml 다 읽기 -> 없으면 src/test/resources/*.yml 다 읽기
 *
 *         .conf file 도 동일함
 *
 */