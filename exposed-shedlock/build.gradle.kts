plugins {
    kotlin(Plugins.JVM) version PluginVersions.JVM_VERSION
    id(Plugins.SHADOW_JAR) version PluginVersions.SHADOW_JAR_VERSION
}

dependencies {
    compileOnly(Dependencies.EXPOSED_CORE)
    compileOnly(Dependencies.EXPOSED_DAO)
    compileOnly(Dependencies.EXPOSED_JAVA_TIME)
    compileOnly(Dependencies.EXPOSED_JDBC)
}

tasks.test {
    useJUnitPlatform()
}