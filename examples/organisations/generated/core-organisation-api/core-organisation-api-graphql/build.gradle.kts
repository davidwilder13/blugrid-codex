plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.application)
}

application {
    mainClass.set("net.blugrid.api.core.organisation.graphql.ApplicationKt")
}

dependencies {
    // Internal modules
    api(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-model"))
    api(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-grpc-client"))

    // Platform BOM
    implementation(platform(libs.micronaut.bom))

    // GraphQL Kotlin (code-first + federation)
    implementation("com.expediagroup:graphql-kotlin-server:8.2.1")
    implementation("com.expediagroup:graphql-kotlin-federation:8.2.1")

    // Micronaut GraphQL (wires graphql-java into HTTP)
    implementation("io.micronaut.graphql:micronaut-graphql")

    // Micronaut core
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-runtime")
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Runtime dependencies
    runtimeOnly(libs.bundles.runtimeCore)

    // Test dependencies
    testImplementation(project(":common:common-kotlin:platform:platform-testing"))
    testImplementation(project(":common:common-kotlin:integration:integration-grpc-client"))
    testImplementation(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-test"))
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.grpcCore)
    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("org.mockito:mockito-core:5.14.2")
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
        arg("micronaut.processing.incremental", "true")
        arg("micronaut.processing.annotations", "net.blugrid.api.core.organisation.graphql.*")
    }
}

micronaut {
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("net.blugrid.api.core.organisation.graphql.*")
    }
}

tasks.test {
    useJUnitPlatform()
}
