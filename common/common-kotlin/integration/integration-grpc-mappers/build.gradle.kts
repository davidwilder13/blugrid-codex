plugins {
    alias(libs.plugins.jvm)
}

dependencies {
    api(project(":common:common-kotlin:common:common-domain"))
    api(project(":common:common-kotlin:common:common-model"))
    api(project(":common:common-kotlin:integration:integration-grpc-proto"))

    // Minimal dependencies for mapping only
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    // gRPC proto types - minimal set
    implementation(libs.grpc.protobuf)
    implementation(libs.protobuf.java)
    implementation(libs.protobuf.kotlin)

    // Test dependencies
    testImplementation(libs.bundles.testing)
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

