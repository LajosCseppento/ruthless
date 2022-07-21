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

//System.setProperty("sonar.projectKey", "LajosCseppento_ruthless")

allprojects {
    tasks.withType {
        if (name == "check") {
            rootProject.tasks.sonarqube { dependsOn(this@withType) }
        }

        if (name == "closeSonatypeStagingRepository") {
            doLast {
                logger.lifecycle("Staging repository has been closed, continue at https://oss.sonatype.org/#stagingRepositories to finalise the release")
            }
        }
    }
}

sonarqube {
    properties {
//        property("projectKey", "LajosCseppento_ruthless")
        property("sonar.projectKey", "LajosCseppento_ruthless")
    }
}
