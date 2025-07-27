plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.jpa)
    alias(libs.plugins.application)
    alias(libs.plugins.shadow)
    alias(libs.plugins.protobuf)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common:common-model"))
    api(project(":common:common-kotlin:data:data-persistence"))
    api(project(":common:common-kotlin:integration:integration-grpc"))
    api(project(":common:common-kotlin:integration:integration-grpc-proto"))
    api(project(":common:common-kotlin:platform:platform-config"))
    api(project(":common:common-kotlin:platform:platform-logging"))
    api(project(":common:common-kotlin:platform:platform-serialization"))
    api(project(":common:common-kotlin:security:security-core"))
    api(project(":common:common-kotlin:server:server-multitenancy"))

    // Domain-specific modules
    api(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-model"))
    api(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-db"))
    api(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-grpc-proto"))

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.micronautData)     // Database access
    implementation(libs.bundles.grpcCore)          // gRPC core functionality
    implementation(libs.bundles.grpcServer)        // gRPC server support

    api(libs.kotlinx.coroutines.core)

    // Service discovery for gRPC
    implementation(libs.micronaut.discovery)

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
    testImplementation(libs.kotlinx.coroutines.test)
}

application {
    mainClass.set("net.blugrid.api.core.organisation.grpc.ApplicationKt")
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
        annotations("net.blugrid.api.core.organisation.grpc.*")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${libs.versions.grpc.get()}"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("kotlin")
            }
            task.plugins {
                create("grpc")
                create("grpckt")
            }
        }
    }
}

sourceSets["main"].java.srcDirs(
    "build/generated/source/proto/main/java",
    "build/generated/source/proto/main/grpc",
    "build/generated/source/proto/main/grpckt"
)

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}

tasks.register<Exec>("buildLocalGrpcDockerImage") {
    dependsOn("shadowJar")
    commandLine("docker", "build", "-t", "core-organisation-api-grpc:local", ".")
}
