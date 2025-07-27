plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.allopen)
    alias(libs.plugins.kapt)
    alias(libs.plugins.application)
}

dependencies {
    implementation(project(":common:common-kotlin:security:security-core"))
    implementation(project(":common:common-kotlin:platform:platform-logging"))
    implementation(project(":common:common-kotlin:platform:platform-serialization"))

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))

    // Core Kotlin
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)

    // Specifically needed dependencies
    implementation(libs.micronaut.security)                   // For Authentication interface
    implementation(libs.micronaut.security.jwt)               // For JWT support
    implementation(libs.nimbus.jose.jwt)                      // For JWT processing
    implementation(libs.jackson.kotlin)                       // For JSON processing
    implementation(libs.micronaut.jackson)                    // For Micronaut JSON integration

    // Annotation processing
    kapt(libs.micronaut.inject.java)

    // Test dependencies
    testImplementation(libs.bundles.testing)
}

kapt {
    arguments {
        arg("micronaut.openapi.project.dir", projectDir.toString())
    }
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("net.blugrid.*")
    }
}

tasks.test {
    useJUnitPlatform()
}
