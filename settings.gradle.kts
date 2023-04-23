pluginManagement {
    plugins {
        id("dev.lajoscseppento.ruthless") version "0.6.0"
        id("com.gradle.plugin-publish") version "1.1.0"
        // Note: newer versions do not seem to work with Gradle 7.6.1
        id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
        id("pl.droidsonroids.jacoco.testkit") version "1.0.9"
        id("org.sonarqube") version "4.0.0.2929"
    }
}

plugins {
    id("dev.lajoscseppento.ruthless")
    id("com.gradle.enterprise") version "+"
}

rootProject.name = "ruthless"
include("ruthless-plugin")

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}
