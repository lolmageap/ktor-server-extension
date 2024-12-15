plugins {
    kotlin(Plugins.JVM) version PluginVersions.JVM_VERSION
    id(Plugins.SHADOW_JAR) version PluginVersions.SHADOW_JAR_VERSION
}

dependencies {
    implementation(project(":scheduler"))
    implementation(project(":exposed-shedlock"))
    implementation(project(":redis-shedlock"))

    testImplementation(Dependencies.REDISSON)
    testImplementation(Dependencies.LETTUCE_CORE)
    testImplementation(Dependencies.HIKARI_CP)
    testImplementation(Dependencies.POSTGRESQL)
    testImplementation(Dependencies.EXPOSED_DAO)
    testImplementation(Dependencies.EXPOSED_JDBC)
    testImplementation(Dependencies.EXPOSED_CORE)
    testImplementation(Dependencies.EXPOSED_JAVA_TIME)
    testImplementation(Dependencies.KOTLIN_TEST_JUNIT)
    testImplementation(Dependencies.KOTEST_RUNNER_JUNIT5)
    testImplementation(Dependencies.KOTEST_ASSERTIONS_CORE)
    testImplementation(Dependencies.TEST_CONTAINERS_POSTGRESQL)
    testImplementation(Dependencies.TEST_CONTAINERS_JDBC)
    testImplementation(Dependencies.TEST_CONTAINERS_JUNIT_JUPITER)
    testImplementation(Dependencies.TEST_CONTAINERS_KOTEST_EXTENSIONS)
}

tasks.test {
    useJUnitPlatform()
}