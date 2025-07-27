plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.allopen)
    alias(libs.plugins.kapt)
    alias(libs.plugins.application)
}

dependencies {
    implementation(project(":common:common-kotlin:security:security-core"))
    implementation(project(":common:common-kotlin:security:security-tokens"))
    implementation(project(":common:common-kotlin:common:common-model"))

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))

    // Core dependencies
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)

    // Security dependencies
    implementation(libs.micronaut.security)
    implementation(libs.micronaut.security.jwt)

    // Specific dependencies for JWT decoding
    implementation(libs.nimbus.jose.jwt)        // For JWT class
    implementation(libs.jackson.databind)       // For ObjectMapper and JsonNode
    implementation(libs.jackson.core)           // For Jackson core classes
    implementation(libs.jackson.kotlin)         // For Kotlin Jackson integration

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
