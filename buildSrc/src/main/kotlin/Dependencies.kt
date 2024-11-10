object Dependencies {
    const val KOTLIN_COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependencyVersions.COROUTINES_VERSION}"
    const val KOTLIN_STD_LIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${DependencyVersions.KOTLIN_VERSION}"
    const val KOTLIN_TEST_JUNIT = "org.jetbrains.kotlin:kotlin-test-junit:${DependencyVersions.KOTLIN_VERSION}"
    const val KOTEST_RUNNER_JUNIT5 = "io.kotest:kotest-runner-junit5:${DependencyVersions.JUNIT_VERSION}"
    const val KOTEST_ASSERTIONS_CORE = "io.kotest:kotest-assertions-core:${DependencyVersions.JUNIT_VERSION}"

    const val EXPOSED_CORE = "org.jetbrains.exposed:exposed-core:${DependencyVersions.EXPOSED_VERSION}"
    const val EXPOSED_DAO = "org.jetbrains.exposed:exposed-dao:${DependencyVersions.EXPOSED_VERSION}"
    const val EXPOSED_JDBC = "org.jetbrains.exposed:exposed-jdbc:${DependencyVersions.EXPOSED_VERSION}"
    const val EXPOSED_JAVA_TIME = "org.jetbrains.exposed:exposed-java-time:${DependencyVersions.EXPOSED_VERSION}"

    const val HIKARI_CP = "com.zaxxer:HikariCP:${DependencyVersions.HIKARI_CP_VERSION}"
    const val POSTGRESQL = "org.postgresql:postgresql:${DependencyVersions.POSTGRESQL_VERSION}"

    const val CRON_UTILS = "com.cronutils:cron-utils:${DependencyVersions.CRON_UTILS_VERSION}"
}