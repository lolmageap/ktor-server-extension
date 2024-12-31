plugins {
    kotlin(Plugins.JVM) version PluginVersions.JVM_VERSION
    id(Plugins.SHADOW_JAR) version PluginVersions.SHADOW_JAR_VERSION
}

dependencies {
    implementation(Dependencies.LETTUCE_CORE)
    implementation(Dependencies.REDISSON)
}

tasks.test {
    useJUnitPlatform()
}