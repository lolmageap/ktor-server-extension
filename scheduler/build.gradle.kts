plugins {
    kotlin(Plugins.JVM) version PluginVersions.JVM_VERSION
    id(Plugins.SHADOW_JAR) version PluginVersions.SHADOW_JAR_VERSION
}

dependencies {
    implementation(Dependencies.CRON_UTILS)
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        enabled = true
        archiveFileName.set("scheduler.jar")
    }

    build {
        dependsOn(shadowJar)
    }
}