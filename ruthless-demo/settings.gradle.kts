plugins {
    id("dev.lajoscseppento.ruthless") version "0.5.0"
    id("com.gradle.enterprise") version "+"
}

rootProject.name = "ruthless-demo"
include(
    "ruthless-demo-java-application",
    "ruthless-demo-java-gradle-plugin",
    "ruthless-demo-java-library",
    "ruthless-demo-spring-boot-application",
    "ruthless-demo-spring-boot-library"
)

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}
