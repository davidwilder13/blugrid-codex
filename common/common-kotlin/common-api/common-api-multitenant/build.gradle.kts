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
