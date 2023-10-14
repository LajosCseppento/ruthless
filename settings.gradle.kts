pluginManagement {
    plugins {
        id("dev.lajoscseppento.ruthless") version "0.7.0"
        id("com.gradle.plugin-publish") version "1.2.1"
        id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
        id("pl.droidsonroids.jacoco.testkit") version "1.0.12"
        id("org.sonarqube") version "4.4.1.3373"
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
