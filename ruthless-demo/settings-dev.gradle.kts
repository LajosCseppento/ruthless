buildscript {
    repositories {
        gradlePluginPortal()
        mavenLocal()
    }

    val gradleProperties = java.util.Properties()
    settings.rootDir.resolve("../gradle.properties").inputStream().use { gradleProperties.load(it) }
    val pluginVersion = gradleProperties.getProperty("version")

    dependencies {
        classpath("dev.lajoscseppento.ruthless:ruthless-plugin:$pluginVersion")
    }
}

apply(plugin = "dev.lajoscseppento.ruthless")

rootProject.name = "ruthless-demo"
include(
        "ruthless-demo-java-application",
        "ruthless-demo-java-gradle-plugin",
        "ruthless-demo-java-library",
        "ruthless-demo-spring-boot-application",
        "ruthless-demo-spring-boot-library"
)
