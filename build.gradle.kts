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
    version = "0.0.9"

    repositories {
        mavenCentral()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")

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

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "17"
        }
    }
}

tasks {
    shadowJar {
        enabled = false
    }
}