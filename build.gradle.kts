import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(Plugins.JVM) version PluginVersions.JVM_VERSION
    id(Plugins.SHADOW_JAR) version PluginVersions.SHADOW_JAR_VERSION
    id(Plugins.MAVEN_PUBLISH)
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
    apply(plugin = "com.github.johnrengelman.shadow")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        withSourcesJar()
        withJavadocJar()
    }

    dependencies {
        implementation(Dependencies.KOTLIN_COROUTINES)
        compileOnly(Dependencies.KOTLIN_STD_LIB)
    }

    if (project.name == "ktor-extension-test") {
        tasks.matching { it.name != "clean" }.configureEach { enabled = false }
    } else {
        publishing {
            publications {
                create<MavenPublication>("mavenJava") {
                    groupId = project.group.toString()
                    artifactId = project.name
                    version = project.version.toString()
                    artifact(tasks["shadowJar"])
                    artifact(tasks["sourcesJar"])
                    artifact(tasks["javadocJar"])
                }
            }
        }
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    tasks {
        jar {
            enabled = false
        }

        shadowJar {
            archiveFileName = "${project.name}.jar"
            exclude("kotlin/**")
            exclude("kotlinx/**")
            exclude("org/**")
        }

        build {
            dependsOn(shadowJar)
        }
    }
}