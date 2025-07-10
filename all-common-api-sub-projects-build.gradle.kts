===== ./common/common-kotlin/common-api/common-api-audit/build.gradle.kts =====
plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.jpa)
    alias(libs.plugins.application)
    alias(libs.plugins.shadow)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common-api:common-api-persistence"))
    api(project(":common:common-kotlin:common-api:common-api-logging"))
    api(project(":common:common-kotlin:common-api:common-api-model"))
    api(project(":common:common-kotlin:common-api:common-api-multitenant"))

    // Platform BOM
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.micronautWeb)
    implementation(libs.bundles.micronautData)
    implementation(libs.bundles.micronautSecurity)

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Runtime dependencies
    runtimeOnly(libs.bundles.runtimeCore)
    runtimeOnly(libs.bundles.runtimeDatabase)
    runtimeOnly(libs.bundles.runtimeSecurity)

    // Test dependencies
    testImplementation(project(":common:common-kotlin:common-api:common-api-test"))
    testImplementation(libs.bundles.testing) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
}

application {
    mainClass.set("net.blugrid.api.ApplicationKt")
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

===== ./common/common-kotlin/common-api/common-api-client/build.gradle.kts =====
plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.application)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common-api:common-api-model"))

    // Platform BOM
    implementation(platform(libs.micronaut.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.micronautWeb)
    implementation(libs.bundles.micronautSecurity)

    // Data model support (needed for Page, Pageable in controllers)
    implementation(libs.micronaut.data.model)      // Just the model types, not full data stack

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

graalvmNative.toolchainDetection = false
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("net.blugrid.api.*")
    }
}

===== ./common/common-kotlin/common-api/common-api-domain/build.gradle.kts =====
plugins {
    alias(libs.plugins.jvm)
}

dependencies {
    api(libs.jakarta.validation)

    // Domain modules should be pure - no framework dependencies
    implementation(libs.bundles.kotlinCore)
}

===== ./common/common-kotlin/common-api/common-api-grpc/build.gradle.kts =====
plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common-api:common-api-model"))
    api(project(":common:common-kotlin:common-api:common-api-grpc-proto"))

    // Platform BOM
    implementation(platform(libs.micronaut.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.grpcCore)
    implementation(libs.bundles.grpcServer)

    // Micronaut Data
    implementation(libs.bundles.micronautData)
    implementation(libs.micronaut.data.model)

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
        arg("micronaut.processing.annotations", "net.blugrid.api.*")
    }
}

===== ./common/common-kotlin/common-api/common-api-grpc-proto/build.gradle.kts =====
import com.google.protobuf.gradle.id

plugins {
   alias(libs.plugins.jvm)
    id("com.google.protobuf") version "0.9.4"
}

version = "0.1.0"
group = "net.blugrid.api"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.proto.google.common)

    implementation("io.grpc:grpc-kotlin-stub:1.3.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    implementation("io.grpc:grpc-stub:1.62.2")
    implementation("io.grpc:grpc-netty-shaded:1.62.2")
    implementation("io.grpc:grpc-protobuf:1.62.2")
    implementation("com.google.api.grpc:proto-google-common-protos:2.59.0")
    implementation("com.google.protobuf:protobuf-java:4.31.1")
    implementation("com.google.protobuf:protobuf-kotlin:4.31.1")
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.31.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.62.2"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
                id("kotlin")
            }
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}

sourceSets["main"].java.srcDirs(
    "build/generated/source/proto/main/java",
    "build/generated/source/proto/main/grpc",
    "build/generated/source/proto/main/grpckt"
)

sourceSets["main"].proto.srcDir("src/main/proto")


===== ./common/common-kotlin/common-api/common-api-json/build.gradle.kts =====
plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.application)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common-api:common-api-model"))

    // Platform BOM
    implementation(platform(libs.micronaut.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.jsonLibs)  // Complete JSON stack including JSONPat
    implementation(libs.micronaut.serde)
    implementation(libs.micronaut.validation)

    // Annotation processing
    kapt(libs.micronaut.inject.java)
    kapt(libs.micronaut.serde.processor)
    kapt(libs.micronaut.openapi)
    kapt(libs.mapstruct.processor)

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

===== ./common/common-kotlin/common-api/common-api-jwt/build.gradle.kts =====
plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.application)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common-api:common-api-json"))
    api(project(":common:common-kotlin:common-api:common-api-model"))
    api(project(":common:common-kotlin:common-api:common-api-logging"))

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.jacksonLibs)

    // JWT-specific dependencies
    implementation(libs.nimbus.jose.jwt)     // Main JWT library
    implementation(libs.jjwt.api)           // Additional JWT support
    implementation(libs.micronaut.security.jwt) // Micronaut JWT integration

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Runtime dependencies
    runtimeOnly(libs.bundles.runtimeCore)
    runtimeOnly(libs.bundles.runtimeSecurity)

    // Test dependencies
    testImplementation(project(":common:common-kotlin:common-api:common-api-test"))
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

graalvmNative.toolchainDetection = false
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("net.blugrid.api.*")
    }
}

===== ./common/common-kotlin/common-api/common-api-logging/build.gradle.kts =====
plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.application)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common-api:common-api-model"))

    // Platform BOM
    implementation(platform(libs.micronaut.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)

    // Logging-specific dependencies
    implementation(libs.logback.classic)         // Primary logging implementation
    implementation(libs.micronaut.jackson)       // For structured logging (JSON)
    implementation(libs.jackson.kotlin)          // Kotlin support for JSON logging

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

===== ./common/common-kotlin/common-api/common-api-model/build.gradle.kts =====
plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.application)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common-api:common-api-domain"))

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)

    // Data model support (needed for Page, Pageable in controllers)
    implementation(libs.micronaut.data.model)      // Just the model types, not full data stack
    implementation(libs.bundles.micronautWeb)      // Full web stack including Netty

    // Model-specific dependencies
    implementation(libs.micronaut.serde)           // Serialization framework
    implementation(libs.micronaut.validation)      // Validation annotations
    implementation(libs.jackson.kotlin)            // JSON serialization
    implementation(libs.mapstruct)                 // Object mapping
    implementation(libs.kotlin.builder.annotation) // Builder pattern support

    // Annotation processing
    kapt(libs.micronaut.inject.java)
    kapt(libs.micronaut.data.processor)
    kapt(libs.micronaut.serde.processor)
    kapt(libs.micronaut.openapi)
    kapt(libs.mapstruct.processor)
    kapt(libs.kotlin.builder.processor)

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

===== ./common/common-kotlin/common-api/common-api-multitenant/build.gradle.kts =====
plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.jpa)
    alias(libs.plugins.application)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common-api:common-api-json"))
    api(project(":common:common-kotlin:common-api:common-api-model"))
    api(project(":common:common-kotlin:common-api:common-api-security"))
    api(project(":common:common-kotlin:common-api:common-api-web"))

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.micronautWeb)
    implementation(libs.bundles.micronautData)
    implementation(libs.bundles.micronautSecurity)

    // Multitenancy-specific dependencies
    implementation(libs.micronaut.multitenancy)    // Core multitenancy support

    // Reactive support for AuthenticationFetcher
    implementation(libs.micronaut.reactor)               // Reactor core integration
    implementation(libs.micronaut.reactor.http.client)   // HTTP reactive support

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Runtime dependencies
    runtimeOnly(libs.bundles.runtimeCore)
    runtimeOnly(libs.bundles.runtimeDatabase)
    runtimeOnly(libs.bundles.runtimeSecurity)

    // Test dependencies
    testImplementation(project(":common:common-kotlin:common-api:common-api-test"))
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

graalvmNative.toolchainDetection = false
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("net.blugrid.api.*")
    }
}

// Integration test configuration
configureIntegrationTests()

fun Project.configureIntegrationTests() {
    sourceSets {
        create("integTest") {
            compileClasspath += sourceSets.main.get().output
            runtimeClasspath += sourceSets.main.get().output
        }
    }

    val integTestImplementation by configurations.getting {
        extendsFrom(configurations.implementation.get())
        extendsFrom(configurations.testImplementation.get())
    }
    configurations["integTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

    dependencies.add("kaptIntegTest", libs.micronaut.inject.java.get())
    dependencies.add("integTestImplementation", libs.micronaut.test.junit5.get())

    tasks.register<Test>("integrationTest") {
        group = "verification"
        description = "Runs integration tests"
        testClassesDirs = sourceSets["integTest"].output.classesDirs
        classpath = sourceSets["integTest"].runtimeClasspath
        shouldRunAfter("test")
    }
}

===== ./common/common-kotlin/common-api/common-api-persistence/build.gradle.kts =====
plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.jpa)
    alias(libs.plugins.application)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common-api:common-api-domain"))
    api(project(":common:common-kotlin:common-api:common-api-multitenant"))

    // Platform BOM
    implementation(platform(libs.micronaut.bom))

    // Core dependencies
    implementation(libs.bundles.kotlinCore)

    // Data persistence stack using new bundles
    implementation(libs.bundles.micronautData)

    // Validation support
    implementation(libs.jakarta.validation)

    // Test dependencies
    testImplementation(libs.bundles.testing) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("net.blugrid.api.*")
    }
}

===== ./common/common-kotlin/common-api/common-api-security/build.gradle.kts =====
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

===== ./common/common-kotlin/common-api/common-api-test/build.gradle.kts =====
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

===== ./common/common-kotlin/common-api/common-api-web/build.gradle.kts =====
plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common-api:common-api-model"))

    // Platform BOM
    implementation(platform(libs.micronaut.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.micronautWeb)      // Complete web stack including Netty

    // Data model support (for pagination utilities)
    implementation(libs.micronaut.data.model)

    // Security support (for auth-related HTTP utilities)
    implementation(libs.bundles.micronautSecurity)

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
        arg("micronaut.processing.annotations", "net.blugrid.api.*")
    }
}

