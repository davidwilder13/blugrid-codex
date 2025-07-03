plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.application)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common-api:common-api-domain"))

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)

    // Data model support (needed for Page, Pageable in controllers)
    implementation(libs.micronaut.data.model)      // Just the model types, not full data stack
    implementation(libs.bundles.micronautWeb)      // Full web stack including Netty

    // Model-specific dependencies
    implementation(libs.micronaut.serde)           // Serialization framework
    implementation(libs.micronaut.validation)      // Validation annotations
    implementation(libs.jackson.kotlin)            // JSON serialization
    implementation(libs.mapstruct)                 // Object mapping
    implementation(libs.kotlin.builder.annotation) // Builder pattern support

    // Annotation processing
    kapt(libs.micronaut.inject.java)
    kapt(libs.micronaut.data.processor)
    kapt(libs.micronaut.serde.processor)
    kapt(libs.micronaut.openapi)
    kapt(libs.mapstruct.processor)
    kapt(libs.kotlin.builder.processor)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Runtime dependencies
    runtimeOnly(libs.bundles.runtimeCore)

    // Test dependencies
    testImplementation(libs.bundles.testing) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

kapt {
    arguments {
        arg("micronaut.openapi.project.dir", projectDir.toString())
    }
}

graalvmNative.toolchainDetection = false
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("net.blugrid.api.*")
    }
}
