plugins {
    id("dev.lajoscseppento.ruthless") version "0.3.0"
}

rootProject.name = "ruthless-demo"
include(
        "ruthless-demo-java-application",
        "ruthless-demo-java-gradle-plugin",
        "ruthless-demo-java-library",
        "ruthless-demo-spring-boot-application",
        "ruthless-demo-spring-boot-library"
)
