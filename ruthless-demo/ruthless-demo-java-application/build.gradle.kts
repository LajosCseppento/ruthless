plugins {
    id("dev.lajoscseppento.ruthless.java-application")
}

dependencies {
    implementation(project(":ruthless-demo-java-library"))
}

application {
    mainClass.set("dev.lajoscseppento.ruthless.demo.javapplication.ItemApplication")
}
