plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.jpa)
    alias(libs.plugins.application)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common:common-model"))
    api(project(":common:common-kotlin:data:data-persistence"))
    api(project(":common:common-kotlin:platform:platform-config"))
    api(project(":common:common-kotlin:platform:platform-logging"))
    api(project(":common:common-kotlin:platform:platform-serialization"))
    api(project(":common:common-kotlin:server:server-multitenancy"))

    // Domain-specific model
    api(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-model"))
    api(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-test"))

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.micronautData)     // Complete database stack

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Runtime dependencies
    runtimeOnly(libs.bundles.runtimeCore)
    runtimeOnly(libs.bundles.runtimeDatabase)

    // Test dependencies
    testImplementation(project(":common:common-kotlin:platform:platform-testing"))
    testImplementation(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-test"))
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
