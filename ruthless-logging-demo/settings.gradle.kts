plugins {
    id("dev.lajoscseppento.ruthless.logging") version "0.8.0"
    id("com.gradle.enterprise") version "+"
}

rootProject.name = "ruthless-logging-demo"

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}
