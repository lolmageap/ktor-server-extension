val exposedVersion : String by project
val ktorVersion : String by project

plugins {
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "3.0.0"
}

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-core:$ktorVersion")
    testImplementation("io.ktor:ktor-server-netty:$ktorVersion")
    testImplementation("com.zaxxer:HikariCP:5.1.0")
    testImplementation("org.postgresql:postgresql:42.7.4")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    jar {
        exclude("io.ktor:ktor-server-core")
        exclude("io.ktor:ktor-server-netty")
        exclude("io.ktor:ktor-server-test-host")
        archiveFileName.set("exposed-shedlock.jar")
    }

    shadowJar {
        enabled = false
    }
}