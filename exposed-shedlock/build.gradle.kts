plugins {
    kotlin(Plugins.JVM) version PluginVersions.JVM_VERSION
    id(Plugins.SHADOW_JAR) version PluginVersions.SHADOW_JAR_VERSION
}

dependencies {
    implementation(Dependencies.EXPOSED_CORE)
    implementation(Dependencies.EXPOSED_DAO)
    implementation(Dependencies.EXPOSED_JAVA_TIME)
    implementation(Dependencies.EXPOSED_JDBC)
}

tasks.test {
    useJUnitPlatform()
}