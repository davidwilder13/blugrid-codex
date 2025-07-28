plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common:common-domain"))
    api(project(":common:common-kotlin:common:common-model"))

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)

    // Model-specific dependencies
    implementation(libs.micronaut.data.model)
    implementation(libs.micronaut.validation)      // Validation annotations
    implementation(libs.jackson.kotlin)            // JSON serialization
    implementation(libs.mapstruct)                 // Object mapping
    implementation(libs.kotlin.builder.annotation) // Builder pattern support

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kapt {
    arguments {
        arg("micronaut.openapi.project.dir", projectDir.toString())
        arg("micronaut.processing.incremental", "true")
        arg("micronaut.processing.annotations", "net.blugrid.core.organisation.*")
    }
}
