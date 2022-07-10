plugins {
    id("dev.lajoscseppento.ruthless") version "0.4.0"
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
