plugins {
    id("dev.lajoscseppento.ruthless.java-library")
}

ruthless.lombok()

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind")
}
