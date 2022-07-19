beforeSettings {
    val gradleProperties = java.util.Properties()
    settings.rootDir.resolve("../gradle.properties").inputStream().use { gradleProperties.load(it) }
    val pluginVersion = gradleProperties.getProperty("version")

    pluginManagement {
        resolutionStrategy {
            eachPlugin {
                if (requested.id.id.startsWith("dev.lajoscseppento.ruthless")) {
                    useVersion(pluginVersion)
                }
            }
        }

        repositories {
            mavenLocal()
            gradlePluginPortal()
        }
    }
}
