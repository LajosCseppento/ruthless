plugins {
    id("dev.lajoscseppento.ruthless.spring-boot-library")
}

dependencies {
    api(project(":ruthless-demo-java-library"))
    api("com.google.guava:guava")
    api("org.springframework.boot:spring-boot-starter")
}
