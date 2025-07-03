plugins {
    alias(libs.plugins.jvm)
}

version = "0.1"
group = "net.blugrid.api"

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
