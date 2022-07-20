plugins {
    id("org.sonarqube") version "3.4.0.2513"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

if (hasProperty("ossrhUsername")) {
    nexusPublishing {
        repositories {
            sonatype {
                val ossrhUsername: String by project
                val ossrhPassword: String by project
                username.set(ossrhUsername)
                password.set(ossrhPassword)
            }
        }
    }
} else {
    logger.warn("Configure project without OSSRH publishing")
}

allprojects {
    tasks.withType {
        val task = this
        if (name == "check") {
            rootProject.tasks.sonarqube { dependsOn(task) }
        }
    }
}
