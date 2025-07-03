plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.application)
}

dependencies {
    // API dependencies - expose ALL to test consumers
    api(project(":common:common-kotlin:common-api:common-api-model"))
    api(project(":common:common-kotlin:common-api:common-api-client"))
    api(project(":common:common-kotlin:common-api:common-api-json"))
    api(project(":common:common-kotlin:common-api:common-api-logging"))
    api(project(":common:common-kotlin:common-api:common-api-security"))
    api(project(":common:common-kotlin:common-api:common-api-web"))

    // Platform BOMs - exposed to test consumers
    api(platform(libs.micronaut.bom))
    api(platform(libs.aws.bom))

    // Core dependencies using new bundles
    api(libs.bundles.kotlinCore)
    api(libs.bundles.micronautCore)
    api(libs.bundles.micronautWeb)
    api(libs.bundles.micronautSecurity)

    // Data model support (needed for Page, Pageable in tests)
    api(libs.micronaut.data.model)

    // gRPC testing support
    api(libs.bundles.grpcCore)
    api(libs.bundles.grpcServer)
    api(libs.bundles.grpcClient)

    // AWS testing support
    api(libs.aws.sdk.ecr)

    // HTTP client for integration testing
    api(libs.micronaut.http.client)

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Runtime dependencies for test environments
    runtimeOnly(libs.bundles.runtimeCore)
    runtimeOnly(libs.bundles.runtimeSecurity)

    // Testing framework - exposed as API
    api(libs.bundles.testing) {
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
