# Ruthless

[![Release](https://img.shields.io/github/v/release/LajosCseppento/ruthless?label=Release)](https://github.com/LajosCseppento/ruthless/releases/latest)
[![Plugin Portal](https://img.shields.io/maven-metadata/v?label=Plugin%20Portal&metadataUrl=https://plugins.gradle.org/m2/dev/lajoscseppento/ruthless/ruthless-plugin/maven-metadata.xml)](https://plugins.gradle.org/plugin/dev.lajoscseppento.ruthless)
[![Maven Central](https://img.shields.io/maven-central/v/dev.lajoscseppento.ruthless/dev.lajoscseppento.ruthless.gradle.plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22dev.lajoscseppento.ruthless%22%20AND%20a:%22dev.lajoscseppento.ruthless.gradle.plugin%22)
[![CI](https://github.com/LajosCseppento/ruthless/workflows/CI/badge.svg)](https://github.com/LajosCseppento/ruthless/actions)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Ruthless conventions for Gradle projects to keep them DRY. Inspired by `buildSrc` conventions
generated by Gradle `init`. Selfishly tailored for my taste, sorry.

> If you are looking for a flexible way of sharing build logic between projects, without enforcing
> someone else's coding conventions and dependency versions, you might want to have a look at
> [Blowdryer](https://github.com/diffplug/blowdryer).

## Features

- `dev.lajoscseppento.ruthless` - multi-use plugin, can apply to init, settings or project
- 5 plugins, choose the best fit:
    - `dev.lajoscseppento.ruthless.java-application` - Java application
    - `dev.lajoscseppento.ruthless.java-gradle-plugin` - Java Gradle Plugin
    - `dev.lajoscseppento.ruthless.java-library` - Java library
    - `dev.lajoscseppento.ruthless.spring-boot-application` - Spring Boot application
    - `dev.lajoscseppento.ruthless.spring-boot-library` - Spring Boot library
- Multi-project support
- Lombok by default (it can be disabled via `ruthless.lombok`)
- Default dependency versions for Guava, Lombok, Jackson, JUnit 5, Mockito, AssertJ... and more!
- `dev.lajoscseppento.ruthless.logging` - logging conventions are also available as a standalone plugin

For examples see `ruthless-demo` and `ruthless-logging-demo`. For details, see the source code.

## Usage

First, create `gradle.properties`:

```properties
group=com.example
version=0.1.0-SNAPSHOT

org.gradle.caching=true
org.gradle.parallel=true
org.gradle.configureondemand=true
```

Then, `settings.gradle.kts` to apply defaults. This will apply a base Ruthless plugin to all
subprojects (multi-project is not enforced, you can go with single-project if you like that
setup more):

```kotlin
plugins {
    id("dev.lajoscseppento.ruthless") version "0.2.0"
}

rootProject.name = "my-project"
include(
    "my-project-app",
    "my-project-lib"
)
```

Then in `build.gradle.kts` for each project, you can specify the project type to apply basic
setup (not obligatory):

```kotlin
// For a Java library:
plugins {
    id("dev.lajoscseppento.ruthless.java-library")
}

// For a Spring Boot application
plugins {
    id("dev.lajoscseppento.ruthless.spring-boot-application")
}
```

See `ruthless-demo` for a complete example.

## Developing Ruthless

Ruthless is a standard Gradle plugin project.

For manual local testing, it is recommended to publish the plugin to the local Maven repository.

```shell script
./gradlew publishToMavenLocal
```

Then use the local snapshot in the demo project:

```shell script
# Full demo
cd ruthless-demo
../gradlew --init-script init-dev.gradle.kts ...

# Logging-only demo
cd ruthless-logging-demo
../gradlew --init-script init-dev.gradle.kts ...
```

### Release Procedure

1. Fix version, finalise change log
2. Publish to Maven Central
    1. Run `./gradlew publishToSonatype closeSonatypeStagingRepository`
    2. Open https://oss.sonatype.org/#stagingRepositories
    3. Close staging repository
    4. Inspect contents
    5. Release
3. Publish to Gradle Plugin Portal using `./gradlew publishPlugins`
4. Bump version
5. Upgrade to recently released version (both `ruthless`, `ruthless-demo` and  `ruthless-logging-demo`)
