import java.util.*

plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.diffplug.spotless") version "5.8.2"
}

val lombokVersion = "1.18.16"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
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
        }
    }
    plugins {
        create("ruthlessJavaApplication") {
            id = "dev.lajoscseppento.ruthless.java-application"
            implementationClass = "dev.lajoscseppento.ruthless.plugin.RuthlessJavaApplicationPlugin"
        }
    }
    plugins {
        create("ruthlessJavaGradlePlugin") {
            id = "dev.lajoscseppento.ruthless.java-gradle-plugin"
            implementationClass = "dev.lajoscseppento.ruthless.plugin.RuthlessJavaGradlePluginPlugin"
        }
    }
    plugins {
        create("ruthlessJavaLibrary") {
            id = "dev.lajoscseppento.ruthless.java-library"
            implementationClass = "dev.lajoscseppento.ruthless.plugin.RuthlessJavaLibraryPlugin"
        }
    }
    plugins {
        create("ruthlessSpringBootApplication") {
            id = "dev.lajoscseppento.ruthless.spring-boot-application"
            implementationClass = "dev.lajoscseppento.ruthless.plugin.RuthlessSpringBootApplicationPlugin"
        }
    }
    plugins {
        create("ruthlessSpringBootLibrary") {
            id = "dev.lajoscseppento.ruthless.spring-boot-library"
            implementationClass = "dev.lajoscseppento.ruthless.plugin.RuthlessSpringBootLibraryPlugin"
        }
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
