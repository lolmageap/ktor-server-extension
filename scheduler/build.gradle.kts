plugins {
    kotlin("jvm") version PluginVersions.JVM_VERSION
    id("io.ktor.plugin") version PluginVersions.KTOR_PLUGIN_VERSION
}

dependencies {
    implementation(Dependencies.CRON_UTILS)
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    jar {
        archiveFileName.set("scheduler.jar")
    }

    shadowJar {
        enabled = false
    }
}