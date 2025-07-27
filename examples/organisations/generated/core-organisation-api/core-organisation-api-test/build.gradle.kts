plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
}

dependencies {
    // API dependencies - expose ALL to test consumers
    api(project(":common:common-kotlin:platform:platform-testing"))
    api(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-model"))

    // Platform BOM - exposed to test consumers
    api(platform(libs.micronaut.bom))

    // Core dependencies using new bundles
    api(libs.bundles.kotlinCore)
    api(libs.bundles.micronautCore)

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Runtime dependencies for test environments
    runtimeOnly(libs.bundles.runtimeCore)
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
