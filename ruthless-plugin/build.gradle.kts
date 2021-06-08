import java.util.*

plugins {
    id("com.gradle.plugin-publish") version "0.15.0"
    id("dev.lajoscseppento.ruthless.java-gradle-plugin")
    `maven-publish`
    `signing`
}

ruthless.lombok()

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    val gradlePlugins = Properties()
    project.file("src/main/resources/gradle-plugins.xml").inputStream().use { gradlePlugins.loadFromXML(it) }
    for (gradlePlugin in gradlePlugins) {
        logger.info("Adding ${gradlePlugin.key}:${gradlePlugin.value} as dependency")
        implementation("${gradlePlugin.key}:${gradlePlugin.value}")
    }

    functionalTestImplementation("commons-io:commons-io:2.9.0")
}

gradlePlugin {
    plugins {
        create("ruthless") {
            id = "dev.lajoscseppento.ruthless"
            implementationClass = "dev.lajoscseppento.ruthless.plugin.RuthlessPlugin"
            displayName = "Ruthless"
            description = "Ruthless base plugin"
        }

        create("ruthlessJavaApplication") {
            id = "dev.lajoscseppento.ruthless.java-application"
            implementationClass = "dev.lajoscseppento.ruthless.plugin.RuthlessJavaApplicationPlugin"
            displayName = "Ruthless / Java Application"
            description = "Ruthless plugin for Java applications"
        }

        create("ruthlessJavaGradlePlugin") {
            id = "dev.lajoscseppento.ruthless.java-gradle-plugin"
            implementationClass = "dev.lajoscseppento.ruthless.plugin.RuthlessJavaGradlePluginPlugin"
            displayName = "Ruthless / Java Gradle Plugin"
            description = "Ruthless plugin for Gradle plugins (implemented in Java)"
        }

        create("ruthlessJavaLibrary") {
            id = "dev.lajoscseppento.ruthless.java-library"
            implementationClass = "dev.lajoscseppento.ruthless.plugin.RuthlessJavaLibraryPlugin"
            displayName = "Ruthless / Java Library"
            description = "Ruthless plugin for Java libraries"
        }

        create("ruthlessSpringBootApplication") {
            id = "dev.lajoscseppento.ruthless.spring-boot-application"
            implementationClass = "dev.lajoscseppento.ruthless.plugin.RuthlessSpringBootApplicationPlugin"
            displayName = "Ruthless / Spring Boot Application"
            description = "Ruthless plugin for Spring boot applications"
        }

        create("ruthlessSpringBootLibrary") {
            id = "dev.lajoscseppento.ruthless.spring-boot-library"
            implementationClass = "dev.lajoscseppento.ruthless.plugin.RuthlessSpringBootLibraryPlugin"
            displayName = "Ruthless / Spring Boot Library"
            description = "Ruthless plugin for Spring boot libraries"
        }
    }
}

val TAGS = listOf("ruthless", "conventions", "defaults", "standards", "dry")
val DESCRIPTION = "Ruthless conventions for Gradle projects to keep them DRY"
val VCS_URL = "https://github.com/LajosCseppento/ruthless.git"
val WEBSITE = "https://github.com/LajosCseppento/ruthless"

val POM_SCM_CONNECTION = "scm:git:git://github.com/LajosCseppento/ruthless.git"
val POM_SCM_DEVELOPER_CONNECTION = "scm:git:ssh://git@github.com/LajosCseppento/ruthless.git"
val POM_SCM_URL = "https://github.com/LajosCseppento/ruthless"

pluginBundle {
    description = DESCRIPTION
    tags = TAGS
    vcsUrl = VCS_URL
    website = WEBSITE
}

if (hasProperty("ossrhUsername")) {
    publishing {
        repositories {
            val ossrhUsername: String by project
            val ossrhPassword: String by project

            maven {
                name = "snapshots"
                url = uri("https://oss.sonatype.org/content/repositories/snapshots")
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }

            maven {
                name = "staging"
                url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }
        }
    }
} else {
    logger.warn("Configure project without OSSRH publishing")
}

publishing.publications.withType<MavenPublication> {
    val publicationName = name

    pom {
        if (publicationName == "pluginMaven") {
            name.set("Ruthless")
            description.set(DESCRIPTION)
        }

        url.set(WEBSITE)

        licenses {
            license {
                name.set("Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }

        developers {
            developer {
                id.set("LajosCseppento")
                name.set("Lajos Cseppentő")
                this.url.set("https://www.lajoscseppento.dev")
            }
        }

        scm {
            connection.set(POM_SCM_CONNECTION)
            developerConnection.set(POM_SCM_DEVELOPER_CONNECTION)
            url.set(POM_SCM_URL)
        }
    }

    // TODO Try to remove after released #15 Add Base Support for Publishing
    versionMapping {
        allVariants {
            fromResolutionResult()
        }
    }
}

signing {
    if (hasProperty("signing.keyId")) {
        sign(publishing.publications)
    } else {
        logger.warn("Configure project without code signing")
    }
}

tasks.jacocoTestReport {
    reports.xml.isEnabled = true
}
