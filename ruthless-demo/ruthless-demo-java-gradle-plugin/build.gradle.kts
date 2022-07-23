plugins {
    id("dev.lajoscseppento.ruthless.java-gradle-plugin")
}

dependencies {
    functionalTestImplementation("com.google.guava:guava")
}

gradlePlugin {
    plugins {
        create("ruthless") {
            id = "dev.lajoscseppento.ruthless.demo.java-gradle-plugin"
            implementationClass = "dev.lajoscseppento.ruthless.demo.javagradleplugin.GreetingPlugin"
        }
    }
}
