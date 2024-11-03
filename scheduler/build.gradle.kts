plugins {
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "3.0.0"
}

dependencies {
    implementation("com.cronutils:cron-utils:9.2.1")
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