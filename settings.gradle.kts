rootProject.name = "ruthless"
include("ruthless-plugin")

gradle.allprojects{
    buildscript {
        repositories {
            mavenCentral()
        }
    }
}
