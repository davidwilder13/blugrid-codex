plugins {
   alias(libs.plugins.jvm)
   alias(libs.plugins.kapt)
   alias(libs.plugins.allopen)
   alias(libs.plugins.jpa)
   alias(libs.plugins.shadow)
   alias(libs.plugins.application)
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":common:common-kotlin:common-api:common-api-model"))
    api(project(":common:common-kotlin:common-api:common-api-client"))
    api(project(":common:common-kotlin:common-api:common-api-json"))
    api(project(":common:common-kotlin:common-api:common-api-logging"))
    api(project(":common:common-kotlin:common-api:common-api-security"))

    api(platform("io.micronaut.platform:micronaut-platform"))
    api(platform("aws.sdk.kotlin:bom:1.4.92"))
    api("io.micronaut:micronaut-http-client")
    api("aws.sdk.kotlin:ecr")
    api("io.grpc:grpc-kotlin-stub:1.3.0")

    api("io.micronaut.grpc:micronaut-grpc-server-runtime")
    api("io.micronaut.grpc:micronaut-grpc-client-runtime")
    api("io.grpc:grpc-stub:1.62.2")
    api("com.google.protobuf:protobuf-kotlin:4.31.1")

    kapt(annotationProcessorLibs.bundles.commonAnnotationProcessors)

    implementation(libs.bundles.commonLibs)
    implementation(libs.bundles.securityLibs)

    runtimeOnly(runTimeLibs.bundles.securityRuntimeLibs)

    api(testLibs.bundles.testImplementationLibs) {
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
        annotations("net.blugrid.api.*")
    }
}

application {
    mainClass.set("net.blugrid.api.ApplicationKt")
}
