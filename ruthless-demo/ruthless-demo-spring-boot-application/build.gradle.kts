plugins {
    id("dev.lajoscseppento.ruthless.spring-boot-application")
}

dependencies {
    implementation(project(":ruthless-demo-spring-boot-library"))
    implementation("org.springframework.boot:spring-boot-starter-web")
}

application {
    mainClass.set("dev.lajoscseppento.ruthless.demo.springbootapplication.ItemWebApplication")
}
