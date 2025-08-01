plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.application)
}

dependencies {
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common:common-model"))
    api(project(":common:common-kotlin:integration:integration-grpc-client"))
    api(project(":common:common-kotlin:integration:integration-grpc-proto"))
    api(project(":common:common-kotlin:platform:platform-serialization"))

    // Domain-specific modules
    api(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-model"))
    api(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-grpc-proto"))

    // Platform BOM
    implementation(platform(libs.micronaut.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautClient)
    implementation(libs.bundles.grpcClientOnly)

    // Micronaut Data
    implementation(libs.micronaut.data.model)

    // Kotlin coroutines for async gRPC calls
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Runtime dependencies
    runtimeOnly(libs.bundles.runtimeClientOnly)

    // Test dependencies
    testImplementation(project(":common:common-kotlin:platform:platform-testing"))
    testImplementation(project(":common:common-kotlin:integration:integration-grpc-client"))
    testImplementation(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-test"))
    testImplementation(libs.bundles.testing) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    // ===== CRITICAL: EXCLUDE ALL JPA/HIBERNATE DEPENDENCIES =====
    configurations.all {
        exclude(group = "io.micronaut.sql", module = "micronaut-hibernate-jpa")
        exclude(group = "io.micronaut.sql", module = "micronaut-jdbc-hikari")
        exclude(group = "io.micronaut.data", module = "micronaut-data-hibernate-jpa")
        exclude(group = "io.micronaut.flyway", module = "micronaut-flyway")
        exclude(group = "org.hibernate")
        exclude(group = "org.postgresql", module = "postgresql")
        exclude(group = "com.zaxxer", module = "HikariCP")
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
        arg("micronaut.processing.annotations", "net.blugrid.api.core.organisation.grpc.*")
    }
}

micronaut {
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

tasks.test {
    dependsOn(":examples:organisations:generated:core-organisation-api:core-organisation-api-grpc:buildLocalGrpcDockerImage")
    useJUnitPlatform()
}
