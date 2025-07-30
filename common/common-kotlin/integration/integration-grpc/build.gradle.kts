plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.application)
}

dependencies {
    api(project(":common:common-kotlin:common:common-domain"))
    api(project(":common:common-kotlin:common:common-model"))
    api(project(":common:common-kotlin:platform:platform-config"))
    api(project(":common:common-kotlin:platform:platform-logging"))
    api(project(":common:common-kotlin:security:security-core"))
    api(project(":common:common-kotlin:integration:integration-grpc-proto"))

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Core dependencies
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)

    // gRPC dependencies
    implementation(libs.bundles.grpcCore)
    implementation(libs.bundles.grpcServer)
    implementation(libs.bundles.grpcClient)

    // Micronaut gRPC integration
    implementation(libs.micronaut.grpc.annotation)
    implementation(libs.micronaut.grpc.server)
    implementation(libs.micronaut.grpc.client)
    // explicit coroutines dependency
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Validation for gRPC
    implementation(libs.bundles.validationLibs)

    // JSON processing (for gRPC-Web or debugging)
    implementation(libs.bundles.jsonLibs)

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)
    kapt(libs.micronaut.inject.java)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Runtime dependencies
    runtimeOnly(libs.bundles.runtimeCore)
    runtimeOnly(libs.grpc.netty)

    // Test dependencies
    testImplementation(libs.bundles.testing)
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
        annotations("net.blugrid.*")
    }
}
