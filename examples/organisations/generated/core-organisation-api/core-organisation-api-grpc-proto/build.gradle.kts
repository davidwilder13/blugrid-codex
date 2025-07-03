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
    implementation("io.grpc:grpc-kotlin-stub:1.3.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    implementation("io.grpc:grpc-stub:1.62.2")
    implementation("io.grpc:grpc-netty-shaded:1.62.2")
    implementation("io.grpc:grpc-protobuf:1.62.2")
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
        all().forEach { task ->
            task.builtins {
                id("kotlin")
            }
            task.plugins {
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

sourceSets {
    main {
        proto {
            srcDir("src/main/proto")

            // Add common proto directory from external module
            srcDir(project(":common:common-kotlin:common-api:common-api-grpc-proto").file("src/main/proto"))
        }
    }
}
