import com.google.protobuf.gradle.id

plugins {
   alias(libs.plugins.jvm)
   alias(libs.plugins.kapt)
   alias(libs.plugins.allopen)
   alias(libs.plugins.jpa)
   alias(libs.plugins.shadow)
   alias(libs.plugins.application)

    id("com.google.protobuf") version "0.9.4"
}

version = "0.1.0"
group = "net.blugrid.api"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":common:common-kotlin:common-api:common-api-grpc"))
    api(project(":common:common-kotlin:common-api:common-api-model"))
    api(project(":common:common-kotlin:common-api:common-api-json"))
    api(project(":common:common-kotlin:common-api:common-api-multitenant"))

    api(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-grpc-proto"))
    api(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-model"))

    implementation(platform("io.micronaut.platform:micronaut-platform"))

    // Micronaut discovery + gRPC client support
    implementation("io.micronaut.discovery:micronaut-discovery-client")
    implementation("io.micronaut.grpc:micronaut-grpc-client-runtime")

    // gRPC client core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("io.grpc:grpc-kotlin-stub:1.3.0")
    implementation("io.grpc:grpc-stub:1.62.2")
    implementation("io.grpc:grpc-protobuf:1.62.2")
    implementation("com.google.protobuf:protobuf-java:4.31.1")
    implementation("com.google.protobuf:protobuf-kotlin:4.31.1")

    // required for annotation support
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    kapt(annotationProcessorLibs.bundles.commonAnnotationProcessors)
    implementation(libs.bundles.commonLibs)
    implementation(libs.bundles.dbLibs)

    runtimeOnly(runTimeLibs.bundles.commonRuntimeLibs)
    runtimeOnly(runTimeLibs.bundles.dbRuntimeLibs)

    compileOnly(libs.bundles.compileOnlyLibs)
    testImplementation(project(":common:common-kotlin:common-api:common-api-test"))
    testImplementation(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-test"))
}

application {
    mainClass.set("net.blugrid.api.core.organisation.grpc.ApplicationKt")
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

kapt {
    arguments {
        arg("micronaut.openapi.project.dir", projectDir.toString())
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

graalvmNative.toolchainDetection = false

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
    "build/generated/source/proto/main/grpc"
)

