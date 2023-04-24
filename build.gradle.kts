plugins {
    id("org.sonarqube")
    id("io.github.gradle-nexus.publish-plugin")
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
