val exposedVersion : String by project
val ktorVersion : String by project

plugins {
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "3.0.0"
}

dependencies {
    implementation(Ktor.server.core)
    implementation(Ktor.server.netty)
}

tasks.test {
    useJUnitPlatform()
}