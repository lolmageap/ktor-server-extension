val exposedVersion : String by project
val ktorVersion : String by project

plugins {
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "3.0.0"
}

dependencies {
    implementation(JetBrains.exposed.core)
    implementation(JetBrains.exposed.dao)
    implementation(JetBrains.exposed.jdbc)
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    testImplementation(Ktor.server.core)
    testImplementation(Ktor.server.netty)
    testImplementation("com.zaxxer:HikariCP:5.1.0")
    testImplementation("org.postgresql:postgresql:42.7.4")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    jar {
        archiveFileName.set("exposed-shedlock.jar")
    }

    shadowJar {
        enabled = false
    }
}