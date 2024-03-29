# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.8.0] - 2023-10-15

### Added

- Add Jakarta Annotation API 2.1.1 as available dependency

### Changed

- Upgrade to Java 17
- Upgrade to Gradle 8.4
- Upgrade Guava from 31.1-jre to 32.1.3-jre
- Upgrade Jackson from 2.14.2 to 2.15.3
- Upgrade JaCoCo from 0.8.8 to 0.8.10
- Upgrade JUnit from 5.9.2 to 5.10.0
- Upgrade JUnit Pioneer from 2.0.1 to 2.1.0
- Upgrade Lombok from 1.18.26 to 1.18.30
- Upgrade Mockito from 5.3.1 to 5.6.0
- Upgrade Spring Boot from 2.7.11 to 3.1.4
- Upgrade Spring Dependency Management Plugin from 1.0.15.RELEASE to 1.1.3

### Removed

- Java 11 support
- Gradle 7 support

## [0.7.1] - 2023-10-14

### Changed

- Upgrade Spotless from 6.18.0 to 6.22.0

## [0.7.0] - 2023-04-24

### Changed

- Upgrade to Java 11
- Upgrade to Gradle 7.6.1
- Upgrade JaCoCo from 0.8.8 to 0.8.9
- Upgrade JUnit Pioneer from 1.9.1 to 2.0.1
- Upgrade Mockito from 4.11.0 to 5.3.1
- Upgrade Spotless from 6.13.0 to 6.18.0
- Upgrade Spring Boot from 2.7.9 to 2.7.11

### Removed

- Java 8 support

## [0.6.0] - 2023-03-04

### Changed

- Declare Gradle Plugin functional test as JVM test suite
- Upgrade to Gradle 7.5.1
- Upgrade AssertJ from 3.23.1 to 3.24.2
- Upgrade Jackson from 2.13.3 to 2.14.2
- Upgrade JUnit 5 from 5.8.2 to 5.9.2
- Upgrade JUnit Pioneer from 1.7.1 to 1.9.1
- Upgrade Lombok from 1.18.24 to 1.18.26
- Upgrade Mockito from 4.6.1 to 4.11.0
- Upgrade Spotless from 6.7.2 to 6.13.0
- Upgrade Spring Boot from 2.7.0 to 2.7.9
- Upgrade Spring Dependency Management Plugin from 1.0.11.RELEASE to 1.0.15.RELEASE

## [0.5.0] - 2022-07-21

### Added

- Lombok support for `*Test` source sets
- Lombok is enabled by default
- JUnit Pioneer 1.7.1 as default test dependency
- Add Ruthless Logging with capability to record build output to file

### Changed

- Change `ruthless.lombok()` into `ruthless.lombok` DSL

## [0.4.0] - 2022-07-10

### Added

- Obligatory `ruthless.java.languageVersion` system property, which configures Java toolchain
  language version
- Java 8 compatibility
- Declare Javadoc and Source JARs
- Add Mockito BOM 4.6.1
- Add `mockito-core` and `mockito-junit-jupiter` as default test dependencies

### Changed

- Upgrade to Gradle 7.4.2
- Upgrade AssertJ from 3.22.0 to 3.23.1
- Upgrade Guava from 31.0.1-jre to 31.1-jre
- Upgrade Jackson from 2.13.1 to 2.13.3
- Upgrade JaCoCo from 0.8.7 to 0.8.8
- Upgrade Lombok from 1.18.22 to 1.18.24
- Upgrade Spotless from 6.2.1 to 6.7.2
- Upgrade Spring Boot from 2.6.3 to 2.7.0

## [0.3.0] - 2022-02-06

### Changed

- Upgrade to Gradle 7.3.3
- Upgrade dependencies

## [0.2.0] - 2021-06-09

### Added

- Basic publishing support

### Changed

- Upgrade to Gradle 7.0.2
- Upgrade dependencies

## [0.1.1] - 2020-12-05

### Added

- Check for required Gradle version
- Publication to Maven Central

## [0.1.0] - 2020-11-30

### Added

- `dev.lajoscseppento.ruthless` - multi-use plugin, can apply to init, settings or project
- `dev.lajoscseppento.ruthless.java-application` - Java application
- `dev.lajoscseppento.ruthless.java-gradle-plugin` - Java Gradle Plugin
- `dev.lajoscseppento.ruthless.java-library` - Java library
- `dev.lajoscseppento.ruthless.spring-boot-application` - Spring Boot application
- `dev.lajoscseppento.ruthless.spring-boot-library` - Spring Boot library
- Publication to Gradle Plugin Portal

[Unreleased]: https://github.com/LajosCseppento/ruthless/compare/v0.8.0...HEAD

[0.8.0]: https://github.com/LajosCseppento/ruthless/releases/tag/v0.8.0

[0.7.1]: https://github.com/LajosCseppento/ruthless/releases/tag/v0.7.1

[0.7.0]: https://github.com/LajosCseppento/ruthless/releases/tag/v0.7.0

[0.6.0]: https://github.com/LajosCseppento/ruthless/releases/tag/v0.6.0

[0.5.0]: https://github.com/LajosCseppento/ruthless/releases/tag/v0.5.0

[0.4.0]: https://github.com/LajosCseppento/ruthless/releases/tag/v0.4.0

[0.3.0]: https://github.com/LajosCseppento/ruthless/releases/tag/v0.3.0

[0.2.0]: https://github.com/LajosCseppento/ruthless/releases/tag/v0.2.0

[0.1.1]: https://github.com/LajosCseppento/ruthless/releases/tag/v0.1.1

[0.1.0]: https://github.com/LajosCseppento/ruthless/releases/tag/v0.1.0
