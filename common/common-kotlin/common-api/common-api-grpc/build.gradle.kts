plugins {
   alias(libs.plugins.jvm)
   alias(libs.plugins.kapt)
}

version = "0.1.0"
group = "net.blugrid.api"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":common:common-kotlin:common-api:common-api-model"))
    implementation(project(":common:common-kotlin:common-api:common-api-grpc-proto"))

    implementation(platform("io.micronaut.platform:micronaut-platform"))

    implementation(libs.bundles.grpcCommonLibs)
    implementation(libs.bundles.grpcServerLibs)
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

