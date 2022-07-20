plugins {
    id("org.sonarqube") version "3.4.0.2513"
}

//System.setProperty("sonar.projectKey", "LajosCseppento_ruthless")

allprojects {
    tasks.withType {
        val task = this
        if (task.name == "check") {
            rootProject.tasks.sonarqube { dependsOn(task) }
        }
    }
}

sonarqube {
    properties {
//        property("projectKey", "LajosCseppento_ruthless")
        property("sonar.projectKey", "LajosCseppento_ruthless")
    }
}
