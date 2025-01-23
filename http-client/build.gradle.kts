import Dependencies.KTOR_CLIENT_CIO
import Dependencies.KTOR_CLIENT_CONTENT_NEGOTIATION

plugins {
    kotlin(Plugins.JVM) version PluginVersions.JVM_VERSION
    id(Plugins.SHADOW_JAR) version PluginVersions.SHADOW_JAR_VERSION
    id(Plugins.KTOR_PLUGIN) version PluginVersions.KTOR_PLUGIN_VERSION
}

dependencies {
    compileOnly(KTOR_CLIENT_CIO)
    compileOnly(KTOR_CLIENT_CONTENT_NEGOTIATION)
}

tasks.test {
    useJUnitPlatform()
}