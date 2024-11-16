import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
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
        implementation(Dependencies.KOTLIN_STD_LIB)
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
                    from(components["java"])
                    artifact(tasks.named<ShadowJar>("shadowJar"))
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
            archiveFileName.set("${project.name}.jar")
        }

        build {
            dependsOn(shadowJar)
        }
    }
}