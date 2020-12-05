import java.util.*

plugins {
    id("com.gradle.plugin-publish") version "0.12.0"
    `java-gradle-plugin`
    `maven-publish`
    `signing`
    id("com.diffplug.spotless") version "5.8.2"
}

val lombokVersion = "1.18.16"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }

    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

    val gradlePlugins = Properties()
    project.file("src/main/resources/gradle-plugins.xml").inputStream().use { gradlePlugins.loadFromXML(it) }
    for (gradlePlugin in gradlePlugins) {
        logger.info("Adding ${gradlePlugin.key}:${gradlePlugin.value} as dependency")
        implementation("${gradlePlugin.key}:${gradlePlugin.value}")
    }

    // TODO spring dependency mgmt plugin is not necessary, it is pulled in by the other spring plugin

    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.18.1")
    testImplementation("commons-io:commons-io:2.8.0")
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
}

signing {
    if (hasProperty("signing.keyId")) {
        sign(publishing.publications)
    } else {
        logger.warn("Configure project without code signing")
    }
}

val functionalTestSourceSet = sourceSets.create("functionalTest")

gradlePlugin.testSourceSets(functionalTestSourceSet)
configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])

val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
}

tasks.check {
    dependsOn(functionalTest)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

spotless {
    java {
        removeUnusedImports()
        googleJavaFormat()
    }
}
