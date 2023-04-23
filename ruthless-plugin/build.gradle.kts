import org.yaml.snakeyaml.Yaml

buildscript {
    dependencies {
        "classpath"("org.yaml:snakeyaml:2.0")
    }
}

plugins {
    id("com.gradle.plugin-publish") version "1.1.0"
    id("dev.lajoscseppento.ruthless.java-gradle-plugin")
    id("pl.droidsonroids.jacoco.testkit") version "1.0.11"
    `maven-publish`
}

dependencies {
    val yamlString = project.file("src/main/resources/configuration.yml").readText()
    val yaml: Map<String, Any> = Yaml().load(yamlString)

    @Suppress("UNCHECKED_CAST")
    val gradlePlugins = yaml["gradlePlugins"] as List<Map<String, String>>

    for (gradlePlugin in gradlePlugins) {
        val gav = "${gradlePlugin["groupId"]}:${gradlePlugin["artifactId"]}:${gradlePlugin["version"]}"

        logger.info("Adding $gav as dependency")
        implementation(gav)
    }

    implementation("dev.lajoscseppento.gradle:gradle-plugin-common:0.3.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    // #55 Direct declaration over dependency constraints to also propagate to the POM
    implementation("commons-codec:commons-codec:1.15")
    functionalTestImplementation("commons-io:commons-io:2.11.0")
    functionalTestImplementation("io.github.java-diff-utils:java-diff-utils:4.12")
}

// Set up JaCoCo coverage for Gradle TestKit tests
val functionalTest = tasks.named("functionalTest")
val jacocoTestReport = tasks.named("jacocoTestReport")

functionalTest.configure {
    finalizedBy(jacocoTestReport)

    // See https://github.com/koral--/jacoco-gradle-testkit-plugin/issues/9
    doLast {
        val jacocoTestExec = checkNotNull(extensions.getByType(JacocoTaskExtension::class).destinationFile)
        val delayMs = 1000L
        val intervalMs = 200L
        val maxRetries = 50
        var retries = 0

        TimeUnit.MILLISECONDS.sleep(delayMs) // Linux

        while (!(jacocoTestExec.exists() && jacocoTestExec.renameTo(jacocoTestExec))) { // Windows
            if (retries >= maxRetries) {
                val waitTime = delayMs + intervalMs * retries
                throw GradleException("$jacocoTestExec.name is not ready, waited at least $waitTime ms")
            }

            retries++
            logger.info("Waiting $intervalMs ms for $jacocoTestExec to be ready, try #$retries...")
            TimeUnit.MILLISECONDS.sleep(intervalMs)
        }

        logger.info("$jacocoTestExec is ready")
    }
}

jacocoTestReport.configure {
    dependsOn(functionalTest)
    (this as JacocoReport).executionData.from(buildDir.absolutePath + "/jacoco/functionalTest.exec")
}

tasks.named("compileFunctionalTestJava").configure {
    dependsOn("generateJacocoFunctionalTestKitProperties")
}

jacocoTestKit {
    applyTo("functionalTestImplementation", functionalTest)
}

val TAGS = listOf("ruthless", "conventions", "defaults", "standards", "dry")
val TAGS_LOGGING = TAGS + "logging"
val DESCRIPTION = "Ruthless conventions for Gradle projects to keep them DRY"
val VCS_URL = "https://github.com/LajosCseppento/ruthless.git"
val WEBSITE = "https://github.com/LajosCseppento/ruthless"

val PLUGIN_MAVEN_PUBLICATION_NAME = "Ruthless"

val LICENSE_NAME = "Apache License, Version 2.0"
val LICENSE_URL = "https://www.apache.org/licenses/LICENSE-2.0"

val DEVELOPER_ID = "LajosCseppento"
val DEVELOPER_NAME = "Lajos Cseppentő"
val DEVELOPER_URL = "https://www.lajoscseppento.dev"

val POM_SCM_CONNECTION = "scm:git:git://github.com/LajosCseppento/ruthless.git"
val POM_SCM_DEVELOPER_CONNECTION = "scm:git:ssh://git@github.com/LajosCseppento/ruthless.git"
val POM_SCM_URL = "https://github.com/LajosCseppento/ruthless"

pluginBundle {
    description = DESCRIPTION
    tags = TAGS
    vcsUrl = VCS_URL
    website = WEBSITE

    pluginTags = mapOf("ruthlessLogging" to TAGS_LOGGING)
}

gradlePlugin {
    plugins {
        create("ruthless") {
            id = "dev.lajoscseppento.ruthless"
            implementationClass = "dev.lajoscseppento.ruthless.plugin.RuthlessPlugin"
            displayName = "Ruthless"
            description = "Ruthless base plugin"
        }

        create("ruthlessLogging") {
            id = "dev.lajoscseppento.ruthless.logging"
            implementationClass = "dev.lajoscseppento.ruthless.plugin.logging.RuthlessLoggingPlugin"
            displayName = "Ruthless Logging"
            description = "Ruthless logging conventions"
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

publishing.publications.withType<MavenPublication> {
    val publicationName = name

    pom {
        if (publicationName == "pluginMaven") {
            name.set(PLUGIN_MAVEN_PUBLICATION_NAME)
            description.set(DESCRIPTION)
        }

        url.set(WEBSITE)

        licenses {
            license {
                name.set(LICENSE_NAME)
                url.set(LICENSE_URL)
            }
        }

        developers {
            developer {
                id.set(DEVELOPER_ID)
                name.set(DEVELOPER_NAME)
                url.set(DEVELOPER_URL)
            }
        }

        scm {
            connection.set(POM_SCM_CONNECTION)
            developerConnection.set(POM_SCM_DEVELOPER_CONNECTION)
            url.set(POM_SCM_URL)
        }
    }
}

tasks.withType {
    if (name == "publishToSonatype") {
        doLast {
            publishing.publications
                    .filterIsInstance<MavenPublication>()
                    .map {
                        Pair(
                                String.format("%s:%s:%s", it.groupId, it.artifactId, it.version),
                                String.format("https://oss.sonatype.org/index.html#nexus-search;gav~%s~%s~%s~~", it.groupId, it.artifactId, it.version)
                        )
                    }
                    .sortedBy { it.first }
                    .forEach {
                        logger.lifecycle("Published {}: {}", it.first, it.second)
                    }
        }
    }
}

if (hasProperty("signing.keyId")) {
    apply(plugin = "signing")
    configure<SigningExtension> {
        sign(publishing.publications)
    }
} else {
    logger.warn("Configure project without code signing")
}

sonarqube {
    properties {
        @Suppress("UNCHECKED_CAST")
        val orig = properties["sonar.tests"] as MutableList<Any>
        properties["sonar.tests"] = orig + sourceSets.functionalTest.get().allSource.srcDirs.filter { it.exists() }
    }
}
