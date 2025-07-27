plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
}

dependencies {
    implementation(project(":common:common-kotlin:common:common-model"))
    implementation(project(":common:common-kotlin:integration:integration-http-client"))
    implementation(project(":common:common-kotlin:platform:platform-logging"))
    implementation(project(":common:common-kotlin:platform:platform-serialization"))
    implementation(project(":common:common-kotlin:security:security-core"))
    implementation(project(":common:common-kotlin:security:security-tokens"))
    implementation(project(":common:common-kotlin:server:server-rest"))

    implementation(platform(libs.micronaut.bom))
    implementation(libs.bundles.testing)

    // ===== CORE PLATFORM =====
    implementation(platform(libs.micronaut.bom))
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)

    // ===== WEB & HTTP (for test controllers and clients) =====
    implementation(libs.micronaut.http.server)
    implementation(libs.micronaut.http.client)
    implementation(libs.micronaut.http.validation)
    implementation(libs.micronaut.management)
    implementation(libs.jackson.kotlin)

    // ===== DATA & PERSISTENCE (for test repositories and entities) =====
    implementation(libs.bundles.micronautData)
    implementation(libs.micronaut.data.model)

    // ===== SECURITY & JWT (for test authentication) =====
    implementation(libs.bundles.micronautSecurity)
    implementation(libs.nimbus.jose.jwt)

    // ===== GRPC (for gRPC testing support) =====
    implementation(libs.bundles.grpcCore)
    implementation(libs.bundles.grpcServer)
    implementation(libs.bundles.grpcClient)

    // ===== REACTIVE SUPPORT =====
    implementation(libs.bundles.micronautReactive)

    // ===== TESTING CORE =====
    api(libs.bundles.testing)
    api(libs.micronaut.test.junit5)

    // ===== TESTCONTAINERS =====
    api(libs.testcontainers)
    api(libs.testcontainers.postgresql)
    api(libs.testcontainers.junit)

    // ===== AWS SDK (for ECR login helper) =====
    implementation(platform(libs.aws.bom))
    implementation(libs.aws.sdk.ecr)

    // ===== VALIDATION =====
    implementation(libs.bundles.validationLibs)
    implementation(libs.jakarta.annotation)

    // ===== UTILITIES =====
    implementation(libs.kotlin.faker)
    implementation(libs.jsonpath.kt)

    // ===== ANNOTATION PROCESSING =====
    kapt(platform(libs.micronaut.bom))
    kapt(libs.bundles.annotationProcessors)

    // ===== RUNTIME DEPENDENCIES =====
    runtimeOnly(libs.bundles.runtimeCore)
    runtimeOnly(libs.bundles.runtimeDatabase)
    runtimeOnly(libs.bundles.runtimeSecurity)

    // ===== TEST SCOPE ONLY =====
    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.logback.classic)
}


kapt {
    arguments {
        arg("micronaut.processing.incremental", "true")
        arg("micronaut.processing.annotations", "net.blugrid.platform.testing.*")
        arg("micronaut.processing.group", "net.blugrid.platform.testing")
        arg("micronaut.processing.module", "platform-testing")
    }
}


tasks.test {
    useJUnitPlatform()
}
