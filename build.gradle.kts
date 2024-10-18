import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    kotlin("jvm") version "2.0.21"
    id("maven-publish")
    id("io.ktor.plugin") version "3.0.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
    withJavadocJar()
}

allprojects {
    group = "extension.ktor"
    version = "0.0.1"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "com.github.johnrengelman.shadow")

    dependencies {
        implementation("io.ktor:ktor-server-core-jvm")
        testImplementation("io.ktor:ktor-server-test-host-jvm")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    }
}

tasks {
    shadowJar {
        enabled = false
    }
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