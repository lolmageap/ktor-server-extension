val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    kotlin("jvm") version "2.0.21"
    id("maven-publish")
    id("io.ktor.plugin") version "3.0.0"
}

allprojects {
    group = "com.github.lolmageap"
    version = "0.0.6"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
        withSourcesJar()
        withJavadocJar()
    }

    dependencies {
        implementation("io.ktor:ktor-server-core-jvm")
        testImplementation("io.ktor:ktor-server-test-host-jvm")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                from(components["java"])
            }
        }
    }
}

tasks {
    shadowJar {
        enabled = false
    }
}