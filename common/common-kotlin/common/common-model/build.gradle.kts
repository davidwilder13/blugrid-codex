plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.application)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common:common-domain"))

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Core dependencies
    implementation(libs.bundles.kotlinCore)

    // jackson
    implementation(libs.bundles.jacksonLibs)

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Test dependencies
    testImplementation(libs.bundles.testing)
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

kapt {
    arguments {
        arg("micronaut.openapi.project.dir", projectDir.toString())
        arg("micronaut.processing.incremental", "true")
        arg("micronaut.processing.annotations", "net.blugrid.*")
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
