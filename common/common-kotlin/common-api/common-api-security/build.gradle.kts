plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.application)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common-api:common-api-model"))
    api(project(":common:common-kotlin:common-api:common-api-jwt"))

    // JSON processing for JWT token parsing
    implementation(project(":common:common-kotlin:common-api:common-api-json"))

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.micronautSecurity)

    // Reactive support for AuthenticationFetcher
    implementation(libs.micronaut.reactor)               // Reactor core integration
    implementation(libs.micronaut.reactor.http.client)   // HTTP reactive support

    // Jackson dependencies for JWT parsing (explicit)
    implementation(libs.bundles.jsonLibs)        // Complete JSON stack
    implementation(libs.micronaut.jackson)       // Micronaut Jackson integration

    // Additional security libraries
    implementation(libs.nimbus.jose.jwt)         // JWT/JOSE support

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Runtime dependencies
    runtimeOnly(libs.bundles.runtimeCore)
    runtimeOnly(libs.bundles.runtimeSecurity)

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

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("net.blugrid.api.*")
    }
}
