plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.jpa)
}

version = "0.1"
group = "net.blugrid.api"

repositories {
    mavenCentral()
}

dependencies {
    // core JPA + Hibernate types
    implementation(libs.bundles.dbLibs)

    // depends on domain primitives and scope interfaces only
    api(project(":common:common-kotlin:common-api:common-api-domain"))
    api(project(":common:common-kotlin:common-api:common-api-multitenant"))

    // optional — for validation annotations
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
