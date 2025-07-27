plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.jpa)
    alias(libs.plugins.application)
    alias(libs.plugins.shadow)
}

dependencies {
    // Common foundation APIs
    api(project(":common:common-kotlin:common:common-model"))
    api(project(":common:common-kotlin:data:data-persistence"))
    api(project(":common:common-kotlin:platform:platform-config"))
    api(project(":common:common-kotlin:platform:platform-logging"))
    api(project(":common:common-kotlin:platform:platform-serialization"))
    api(project(":common:common-kotlin:security:security-core"))
    api(project(":common:common-kotlin:server:server-rest"))

    // Domain-specific modules
    implementation(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-model"))
    implementation(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-db"))

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.micronautWeb)      // REST API support
    implementation(libs.bundles.micronautData)     // Database operations
    implementation(libs.bundles.micronautSecurity) // Authentication/authorization

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Runtime dependencies
    runtimeOnly(libs.bundles.runtimeCore)
    runtimeOnly(libs.bundles.runtimeDatabase)
    runtimeOnly(libs.bundles.runtimeSecurity)

    // Test dependencies
    testImplementation(project(":common:common-kotlin:platform:platform-testing"))
    testImplementation(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-test"))
    testImplementation(libs.bundles.testing) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
}

application {
    mainClass.set("net.blugrid.api.core.organisation.ApplicationKt")
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
    }
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("net.blugrid.api.core.organisation.*")
    }
}
