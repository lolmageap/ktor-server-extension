# ktor-extension

## Description

This is a simple extension for the Ktor framework that provides an easy way to use a scheduler and ShedLock.

## Dependencies

It requires JDK version 17 or higher

add the following to your build.gradle.kts file:

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.lolmageap.ktor-server-extension:scheduler:1.0.2")
    implementation("com.github.lolmageap.ktor-server-extension:exposed-shedlock:1.0.2")
}
```

## Usage

By combining the scheduler with ShedLock, you can ensure safe execution in a scale-out environment.
ShedLock guarantees that scheduled tasks will run only once across multiple instances, preventing race conditions or
duplicate executions in distributed setups.

```kotlin
fun Application.module() {
    schedule("0 0 0 * * *") {
        shedlock("shedlock", 5.minutes) {
            println("Hello, world!")
        }
    }
}
```

### Scheduler

This extension provides a simple way to schedule tasks within the Ktor framework.

```kotlin
fun Application.module() {
    schedule("0 0 0 * * *") {
        println("Hello, world!")
    }
}
```

### Exposed Shedlock

This extension offers an easy way to integrate ShedLock for managing distributed locks in the Ktor framework.

#### Configuration

Before using ShedLock, you need to create the necessary schema:

```kotlin
fun Application.module() {
    transaction { SchemaUtils.create(Shedlocks) }
}
```

#### Usage

To use ShedLock, specify a lock name and a duration for how long the lock should be held.

```kotlin
fun Application.module() {
    shedlock(
        name = "shedlock", lockAtMostFor = 5.minutes,
    ) {
        println("Hello, world!")
    }
}
```

or

```kotlin
fun Application.module() {
    shedlock(
        name = "shedlock", lockAtMostFor = Duration.ofMinutes(5),
    ) {
        println("Hello, world!")
    }
}
```

## TODO

- [ ] Add Redis Shedlock To Redisson
- [ ] Add Redis Distributed Lock To Redisson - SpinLock, RedLock

### Redis Shedlock

#### Configuration

Before using ShedLock, you need to create the necessary schema:

```kotlin
fun Application.module() {
    RedissonClientHolder.redissonClient = Redisson.create()
}
```

```kotlin
fun Application.module() {
    shedlock(
        name = "shedlock", lockAtMostFor = 5.minutes,
    ) {
        println("Hello, world!")
    }
}
```

### Redis Distributed Lock