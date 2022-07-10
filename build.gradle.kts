plugins {
    id("org.sonarqube") version "3.4.0.2513"
}

allprojects {
    tasks.withType {
        val task = this
        if (task.name == "check") {
            rootProject.tasks.sonarqube { dependsOn(task) }
        }
    }
}
