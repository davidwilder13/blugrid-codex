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
