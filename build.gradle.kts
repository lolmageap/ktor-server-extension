import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    kotlin("jvm") version "2.0.21"
    id("maven-publish")
    id("io.ktor.plugin") version "3.0.0"
}

allprojects {
    group = "com.github.lolmageap"
    version = "1.0.0"

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        withSourcesJar()
        withJavadocJar()
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
        implementation("io.ktor:ktor-server-core-jvm")
        implementation("io.ktor:ktor-serialization-jvm")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
        testImplementation("io.ktor:ktor-server-test-host-jvm")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
        testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
        testImplementation("io.kotest:kotest-assertions-core:5.7.2")
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

    tasks.withType<KotlinCompile> {
        compilerOptions  {
            jvmTarget = JvmTarget.JVM_17
        }
    }
}

tasks {
    shadowJar {
        enabled = false
    }
}