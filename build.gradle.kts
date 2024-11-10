import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version PluginVersions.JVM_VERSION
    id("maven-publish")
    id("io.ktor.plugin") version PluginVersions.KTOR_PLUGIN_VERSION
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
        implementation(Dependencies.KOTLIN_COROUTINES)
        implementation(Dependencies.KOTLIN_STD_LIB)
        testImplementation(Dependencies.KOTLIN_TEST_JUNIT)
        testImplementation(Dependencies.KOTEST_RUNNER_JUNIT5)
        testImplementation(Dependencies.KOTEST_ASSERTIONS_CORE)
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