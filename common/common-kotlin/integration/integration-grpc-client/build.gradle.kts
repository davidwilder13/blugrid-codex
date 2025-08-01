plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
}

dependencies {
    // Core domain dependencies
    api(project(":common:common-kotlin:common:common-domain"))
    api(project(":common:common-kotlin:common:common-model"))
    api(project(":common:common-kotlin:platform:platform-config"))
    api(project(":common:common-kotlin:platform:platform-logging"))
    api(project(":common:common-kotlin:security:security-core"))
    api(project(":common:common-kotlin:integration:integration-grpc-mappers"))
    api(project(":common:common-kotlin:integration:integration-grpc-proto"))

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Client dependencies
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautClient)
    implementation(libs.bundles.grpcClientOnly)

    // gRPC client-specific dependencies
    implementation(libs.bundles.grpcCore)
    implementation(libs.bundles.grpcClient)

    // Micronaut gRPC client integration
    implementation(libs.micronaut.grpc.client)

    // Coroutines for async client operations
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Service discovery for client connections
    implementation(libs.micronaut.discovery)

    // Validation for client-side data
    implementation(libs.bundles.validationLibs)

    // JSON processing (for debugging/logging)
    implementation(libs.bundles.jsonLibs)

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)
    kapt(libs.micronaut.inject.java)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Runtime dependencies
    runtimeOnly(libs.bundles.runtimeClientOnly)
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

kapt {
    arguments {
        arg("micronaut.openapi.project.dir", projectDir.toString())
        arg("micronaut.processing.incremental", "true")
        arg("micronaut.processing.annotations", "net.blugrid.integration.grpc.client.*")
    }
}
