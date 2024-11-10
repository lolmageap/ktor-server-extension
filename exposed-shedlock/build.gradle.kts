plugins {
    kotlin("jvm") version PluginVersions.JVM_VERSION
    id("io.ktor.plugin") version PluginVersions.KTOR_PLUGIN_VERSION
}

dependencies {
    implementation(Dependencies.EXPOSED_CORE)
    implementation(Dependencies.EXPOSED_DAO)
    implementation(Dependencies.EXPOSED_JAVA_TIME)
    implementation(Dependencies.EXPOSED_JDBC)

    testImplementation(Dependencies.HIKARI_CP)
    testImplementation(Dependencies.POSTGRESQL)
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